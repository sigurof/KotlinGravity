package no.sigurof.gravity

import no.sigurof.gravity.utils.PointMass
import no.sigurof.gravity.utils.UniqueCombinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.operators.*
import org.joml.Vector3f
import kotlin.math.*

/**
 * OrbitalParameters describes the planet's mass and orbital period.
 * The orbital period t is ignored for the top level node
 * */
class PlanetParameters(
    val m: Float,
    val t: Float?
)

/**
 * PlanetNode represents an orbiting body which accords to Newtonian gravity
 * It stores `self` representing its own parameters, and `satelites`, a list
 * of bodies which should orbit
 * */
class PlanetNode(
    val self: PlanetParameters,
    val moons: List<PlanetNode>?
) {
    init {
        if (moons == null) {
            error("orbitalNode's properties were both null. One of them should be set.")
        }
    }
}

fun getSunEarthMoonOrbitalNode(): PlanetNode {
    val moon = PlanetNode(
        PlanetParameters(0.01f, 1f),
        null
    )
    val earth = PlanetNode(
        PlanetParameters(1f, 10f),
        listOf(
            moon
        )
    )
    return PlanetNode(
        PlanetParameters(1000f, null),
        listOf(
            earth
        )
    )
}


fun getSunEarthMoon(g: Float): List<MassPosVel> {
    val m1 = 50f
    val m21 = 50f
    val m22 = 5f
    val t1 = 5f
    val t2 = 0.5f
    val baryPos = Vector3f(0f, 0f, 0f)
    val baryVel = Vector3f(0f, 0f, 0f)

    val m2 = m21 + m22
    val (b1, b2) = twoBodySystem(baryPos, baryVel, m1, m2, g, t1)

    val (b21, b22) = twoBodySystem(b2.r, b2.v, m21, m22, g, t2)
    return listOf(b1, b21, b22)
}


fun getSunEarthMars(
    g: Float,
    msun: Float,
    mearth: Float,
    tearth: Float,
    mmars: Float,
    tmars: Float,
    baryPos: Vector3f,
    baryVel: Vector3f
): List<MassPosVel> {
    val (fictSun1, earth) = restingTwoBodySystem(msun, mearth, g, tearth)
    val (fictSun2, mars) = restingTwoBodySystem(msun, mmars, g, tmars)
    earth.r += baryPos
    earth.v += baryVel
    mars.r += baryPos
    mars.v += baryVel
    val sun = PointMass(
        msun,
        fictSun1.r + fictSun2.r + baryPos,// TODO Find out if this component can be chosen so as to preserve or minimize some property
        fictSun1.v + fictSun2.v + baryVel // This velocity component ensures that total momentum is zero relative to the barycenter
    )
    return listOf(sun, earth, mars)
}


fun getSunEarth(
    g: Float,
    msun: Float,
    mearth: Float,
    tearth: Float,
    baryPos: Vector3f,
    baryVel: Vector3f
): List<MassPosVel> {
    val (fictSun1, earth) = restingTwoBodySystem(msun, mearth, g, tearth)
    earth.r += baryPos
    earth.v += baryVel
    val sun = PointMass(
        msun,
        fictSun1.r + baryPos,// TODO Find out if this component can be chosen so as to preserve or minimize some property
        fictSun1.v + baryVel // This velocity component ensures that total momentum is zero relative to the barycenter
    )
    return listOf(sun, earth)
}

fun twoBodySystem(
    baryPos: Vector3f,
    baryVel: Vector3f,
    m1: Float,
    m2: Float,
    g: Float,
    t: Float
): Pair<MassPosVel, MassPosVel> {
    val mu = m1 * m2 / (m1 + m2)
    val (rVec, vVec) = getCentralForceProblemPositionAndVelocity(g, m1, m2, t)

    val r1Vec = baryPos + mu / m1 * rVec
    val v1Vec = baryVel + mu / m1 * vVec

    val r2Vec = baryPos - mu / m2 * rVec
    val v2Vec = baryVel - mu / m2 * vVec

    val b1 = PointMass(m1, r1Vec, v1Vec)
    val b2 = PointMass(m2, r2Vec, v2Vec)

    return Pair(b1, b2)
}


fun restingTwoBodySystem(
    m1: Float,
    m2: Float,
    g: Float,
    t: Float
): Pair<MassPosVel, MassPosVel> {
    val mu = m1 * m2 / (m1 + m2)
    val (rVec, vVec) = getCentralForceProblemPositionAndVelocity(g, m1, m2, t)

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
    t: Float
): Pair<Vector3f, Vector3f> {
    val mu: Float = m1 * m2 / (m1 + m2)
    val m = m1 + m2

    val rHat = randomDirection().normalize(Vector3f())
    val thetaHat = rHat.cross(randomDirection(), Vector3f()).normalize(Vector3f())
    val e = randomFloatBetween(0.2, 0.8)
    val theta = randomAngle()

    val gamma = g * m1 * m2

    val l = m1 * m2 * (g * g * t / 2f / PI.toFloat() / m).pow(1f / 3f) / sqrt(1f - e * e)

    val rVec = (l * l / mu / gamma) / (1f + e * cos(theta)) * rHat
    val vVec = (gamma / l) * (e * sin(theta) * rHat + (1f + e * cos(theta)) * thetaHat)

    return Pair(rVec, vVec)
}

fun calculateEnergy(bodies: List<MassPosVel>, g: Float): Float {
    val totKinEnergy = bodies
        .map { kineticEnergy(it) }
        .sum()
    val totPotEnergy = UniqueCombinationsOfTwoUniqueUntil(bodies.size)
        .map { potential(bodies[it.i], bodies[it.j], g) }
        .sum()
    return totKinEnergy + totPotEnergy
}

fun kineticEnergy(body: MassPosVel): Float {
    return 0.5f * body.m * body.v * body.v
}

fun calculateEnergy(b1: MassPosVel, b2: MassPosVel, g: Float): Float {
    val distance = (b1.r - b2.r).length()
    val m1 = b1.m
    val m2 = b2.m
    val v1 = b1.v
    val v2 = b2.v
    return 0.5f * m1 * v1.lengthSquared() + 0.5f * m2 * v2.lengthSquared() - g * m1 * m2 / distance
}

fun momentum(b: MassPosVel): Vector3f {
    return b.m * b.v
}

fun potential(body1: MassPosVel, body2: MassPosVel, g: Float): Float {
    return -g * body1.m * body2.m / (body1.r - body2.r).length()
}


