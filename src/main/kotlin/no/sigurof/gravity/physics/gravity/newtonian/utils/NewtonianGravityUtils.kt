package no.sigurof.gravity.physics.gravity.newtonian.utils

import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.physics.utils.ForcePair
import no.sigurof.gravity.utils.maths.combinatorics.UniqueCombinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.maths.combinatorics.combinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import no.sigurof.gravity.utils.randomAngle
import no.sigurof.gravity.utils.randomDirection
import no.sigurof.gravity.utils.randomFloatBetween
import org.joml.Vector3f
import kotlin.math.*
import kotlin.random.Random

fun getSunEarthMoon(g: Float): List<MassPosVel> {
    val m1 = 50f
    val m21 = 50f
    val m22 = 5f
    val t1 = 5f
    val t2 = 0.5f
    val baryPos = Vector3f(0f, 0f, 0f)
    val baryVel = Vector3f(0f, 0f, 0f)

    val m2 = m21 + m22
    val (b1, b2) = twoBodySystem(
        baryPos,
        baryVel,
        m1,
        m2,
        g,
        t1,
        0f
    )

    val (b21, b22) = twoBodySystem(
        b2.r,
        b2.v,
        m21,
        m22,
        g,
        t2,
        0f
    )
    return listOf(b1, b21, b22)
}

fun solarSystemWithMoons(
    g: Float,
    node: GravityNode,
    baryPos: Vector3f,
    baryVel: Vector3f
): List<MassPosVel> {
    val planets = mutableListOf<MassPosVel>()
    val sun = PointMass(node.mass, baryPos, baryVel)
    for (i in node.planets.indices) {
        val (fictSun, planetsBaryCenter) = restingTwoBodySystem(
            g = g,
            m1 = node.mass,
            m2 = node.planets[i].totalMass,
            t = node.planets[i].period,
            e = node.planets[i].eccentricity
        )
        planets.addAll(solarSystemWithMoons(g, node.planets[i], planetsBaryCenter.r, planetsBaryCenter.v))
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

class GravityNode(
    val mass: Float,
    val planets: List<GravityNode>,
    val period: Float,
    val eccentricity: Float
) {
    val totalMass: Float
        get() = mass + planets.map { it.totalMass }.sum()
}

fun aSolarSystem(
    g: Float,
    msun: Float,
    ms: Array<Float>,
    ts: Array<Float>,
    es: Array<Float>,
    baryPos: Vector3f,
    baryVel: Vector3f
): List<MassPosVel> {
    val fictSuns = mutableListOf<MassPosVel>()
    val planets = mutableListOf<MassPosVel>()
    val sun =
        PointMass(msun, Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, 0f))
    for (i in ms.indices) {
        val (fictSun, planet) = restingTwoBodySystem(
            g = g,
            m1 = msun,
            m2 = ms[i],
            t = ts[i],
            e = es[i]
        )
        fictSuns.add(fictSun)
        planets.add(planet)
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


fun twoBodySystem(
    baryPos: Vector3f,
    baryVel: Vector3f,
    m1: Float,
    m2: Float,
    g: Float,
    t: Float,
    e: Float
): Pair<MassPosVel, MassPosVel> {
    val mu = m1 * m2 / (m1 + m2)
    val (rVec, vVec) = getCentralForceProblemPositionAndVelocity(
        g,
        m1,
        m2,
        t,
        e
    )

    val r1Vec = baryPos + mu / m1 * rVec
    val v1Vec = baryVel + mu / m1 * vVec

    val r2Vec = baryPos - mu / m2 * rVec
    val v2Vec = baryVel - mu / m2 * vVec

    val b1 = PointMass(m1, r1Vec, v1Vec)
    val b2 = PointMass(m2, r2Vec, v2Vec)

    return Pair(b1, b2)
}


fun restingTwoBodySystem(
    g: Float,
    m1: Float,
    m2: Float,
    t: Float,
    e: Float
): Pair<MassPosVel, MassPosVel> {
    val mu = m1 * m2 / (m1 + m2)
    val (rVec, vVec) = getCentralForceProblemPositionAndVelocity(
        g = g,
        m1 = m1,
        m2 = m2,
        t = t,
        e = e
    )

    val r1Vec = mu / m1 * rVec
    val v1Vec = mu / m1 * vVec

    val r2Vec = -mu / m2 * rVec
    val v2Vec = -mu / m2 * vVec

    val b1 = PointMass(m1, r1Vec, v1Vec)
    val b2 = PointMass(m2, r2Vec, v2Vec)

    return Pair(b1, b2)
}


fun getCentralForceProblemPositionAndVelocity(
    g: Float,
    m1: Float,
    m2: Float,
    t: Float,
    e: Float
): Pair<Vector3f, Vector3f> {
    val mu: Float = m1 * m2 / (m1 + m2)
    val m = m1 + m2

    val rHat = randomDirection().normalize(Vector3f())
    val thetaHat = rHat.cross(randomDirection(), Vector3f()).normalize(Vector3f())
    val theta = randomAngle()

    val gamma = g * m1 * m2

    val l = m1 * m2 * (g * g * t / 2f / PI.toFloat() / m).pow(1f / 3f) / sqrt(1f - e * e)

    val rVec = (l * l / mu / gamma) / (1f + e * cos(theta)) * rHat
    val vVec = (gamma / l) * (e * sin(theta) * rHat + (1f + e * cos(theta)) * thetaHat)

    return Pair(rVec, vVec)
}

fun totalEnergyOf(bodies: List<MassPosVel>, g: Float): Float {
    val totKinEnergy = bodies
        .map { kineticEnergyOf(it) }
        .sum()
    val totPotEnergy = UniqueCombinationsOfTwoUniqueUntil(bodies.size)
        .map {
            potentialEnergyBetween(
                bodies[it.first],
                bodies[it.second],
                g
            )
        }
        .sum()
    return totKinEnergy + totPotEnergy
}

fun kineticEnergyOf(body: MassPosVel): Float {
    return 0.5f * body.m * body.v * body.v
}

fun potentialEnergyBetween(body1: MassPosVel, body2: MassPosVel, g: Float): Float {
    return -g * body1.m * body2.m / (body1.r - body2.r).length()
}

internal fun forceBetween(r1: Vector3f, r2: Vector3f, m1: Float, m2: Float, g: Float): Vector3f {
    val r12 = r2 - r1
    val d = r12.normalized() / r12.lengthSquared()
    return g * m1 * m2 * d
}


internal fun newtonianForcePairs(numberOfObjects: Int): Array<ForcePair> {
    val forcePairs = mutableListOf<ForcePair>()
    for (forcePair in combinationsOfTwoUniqueUntil(numberOfObjects)) {
        forcePairs.add(forcePair)
    }
    return forcePairs.toTypedArray()
}

internal fun randomDistributionAveragingTo(total: Float, n: Int): List<Float> {
    val masses = (0 until n).map { randomFloatBetween(0f, 1f) }
    val tot = masses.sum()
    return masses.map { it * total / tot }
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
        planets = planets,
        period = sunT,
        eccentricity = randomFloatBetween(eccentricityBetween.first, eccentricityBetween.second)
    )
}


internal fun getASolarSystem(): GravityNode {
    val tearth = 50f
    val tmoon = tearth / 12f

    val mmoon = 0.01f
    val mearth = 1f
    val msun = 2000f

    val moon = GravityNode(
        mass = mmoon,
        planets = listOf(),
        period = tmoon,
        eccentricity = 0f
    )
    val earth = GravityNode(
        mass = mearth,
        planets = listOf(
            moon
        ),
        period = tearth,
        eccentricity = 0f

    )
    return GravityNode(
        msun,
        planets = listOf(
            earth
        ),
        period = 0f,
        eccentricity = 0f
    )
}