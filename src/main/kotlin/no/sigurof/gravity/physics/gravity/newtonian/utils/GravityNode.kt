package no.sigurof.gravity.physics.gravity.newtonian.utils

import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.randomFloatBetween
import org.joml.Vector3f
import kotlin.random.Random

class GravityNode(
    val mass: Float,
    val satellites: List<GravityNode>,
    val period: Float,
    val eccentricity: Float
) {
    val totalMass: Float
        get() = mass + satellites.map { it.totalMass }.sum()

    fun buildPlanets(
        g: Float,
        baryPos: Vector3f,
        baryVel: Vector3f
    ): List<MassPosVel> {
        val planets = mutableListOf<MassPosVel>()
        val sun = PointMass(this.mass, baryPos, baryVel)
        for (planet in this.satellites) {
            val (fictSun, planetsBaryCenter) = restingTwoBodySystem(
                g = g,
                m1 = this.mass,
                m2 = planet.totalMass,
                t = planet.period,
                e = planet.eccentricity
            )
            planets.addAll(planet.buildPlanets(g, planetsBaryCenter.r, planetsBaryCenter.v))
            sun.r += fictSun.r
            sun.v += fictSun.v
        }
        for (planet in planets) {
            planet.r += baryPos
            planet.v += baryVel
        }
        planets.add(sun)
        return planets
    }
}

internal fun getRandomGravityNode(
    mass: Float,
    orbitalPeriod: Float,
    eccentricityBetween: Pair<Float, Float>,
    numPlanets: Pair<Int, Int>,
    remainingDepth: Int
): GravityNode {
    val sunMass = mass * 0.90f
    val sunT = orbitalPeriod * 0.40f
    val planetsMass = mass - sunMass
    val planetsPeriod = orbitalPeriod - sunT
    val number = Random.nextInt(numPlanets.first, numPlanets.second)
    val masses = randomDistributionAveragingTo(planetsMass, number)
    val periods = randomDistributionAveragingTo(planetsPeriod, number)
    val planets = mutableListOf<GravityNode>()
    if (remainingDepth > 0) {
        for (i in 0 until number) {
            planets.add(
                getRandomGravityNode(masses[i], periods[i], eccentricityBetween, numPlanets, remainingDepth - 1)
            )
        }
    }
    return GravityNode(
        mass = sunMass,
        satellites = planets,
        period = sunT,
        eccentricity = randomFloatBetween(eccentricityBetween.first, eccentricityBetween.second)
    )
}

fun buildPlanetsFromGravityNode(
    g: Float,
    node: GravityNode,
    baryPos: Vector3f,
    baryVel: Vector3f
): List<MassPosVel> {
    val planets = mutableListOf<MassPosVel>()
    val sun = PointMass(node.mass, baryPos, baryVel)
    for (planet in node.satellites) {
        val (fictSun, planetsBaryCenter) = restingTwoBodySystem(
            g = g,
            m1 = node.mass,
            m2 = planet.totalMass,
            t = planet.period,
            e = planet.eccentricity
        )
        planets.addAll(buildPlanetsFromGravityNode(g, planet, planetsBaryCenter.r, planetsBaryCenter.v))
        sun.r += fictSun.r
        sun.v += fictSun.v
    }
    for (planet in planets) {
        planet.r += baryPos
        planet.v += baryVel
    }
    planets.add(sun)
    return planets
}

internal fun getSunEarthMoonGravityNode(): GravityNode {
    val tearth = 50f
    val tmoon = tearth / 12f

    val mmoon = 0.01f
    val mearth = 1f
    val msun = 2000f

    val moon = GravityNode(
        mass = mmoon,
        satellites = listOf(),
        period = tmoon,
        eccentricity = 0f
    )
    val earth = GravityNode(
        mass = mearth,
        satellites = listOf(
            moon
        ),
        period = tearth,
        eccentricity = 0f

    )
    return GravityNode(
        msun,
        satellites = listOf(
            earth
        ),
        period = 0f,
        eccentricity = 0f
    )
}
