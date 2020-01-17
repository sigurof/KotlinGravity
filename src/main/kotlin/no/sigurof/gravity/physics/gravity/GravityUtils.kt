package no.sigurof.gravity.physics.gravity

import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.utils.maths.combinatorics.UniqueCombinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import no.sigurof.gravity.utils.randomAngle
import no.sigurof.gravity.utils.randomDirection
import no.sigurof.gravity.utils.randomFloatBetween
import org.joml.Vector3f
import kotlin.math.*

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

fun aSolarSystem(
    g: Float,
    msun: Float,
    ms: Array<Float>,
    ts: Array<Float>,
    baryPos: Vector3f,
    baryVel: Vector3f
): List<MassPosVel> {
    val fictSuns = mutableListOf<MassPosVel>()
    val planets = mutableListOf<MassPosVel>()
    val sun =
        PointMass(msun, Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, 0f))
    for ((m, t) in ms zip ts) {
        val (fictSun, planet) = restingTwoBodySystem(msun, m, g, t)
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

fun totalEnergyOf(bodies: List<MassPosVel>, g: Float): Float {
    val totKinEnergy = bodies
        .map { kineticEnergyOf(it) }
        .sum()
    val totPotEnergy = UniqueCombinationsOfTwoUniqueUntil(
        bodies.size
    )
        .map { potentialEnergyBetween(bodies[it.i], bodies[it.j], g) }
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

