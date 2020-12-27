package no.sigurof.gravity.physics.gravity.newtonian.utils

import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.utils.maths.combinatorics.IndexPair
import no.sigurof.gravity.utils.maths.combinatorics.UniqueCombinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.maths.combinatorics.combinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import no.sigurof.gravity.utils.randomAngle
import no.sigurof.gravity.utils.randomDirection
import no.sigurof.gravity.utils.randomFloatBetween
import org.joml.Vector3f
import kotlin.math.*


fun restingTwoBodySystem(
    g: Float,
    m1: Float,
    m2: Float,
    t: Float,
    e: Float,
    rHat: Vector3f = randomDirection().normalize(Vector3f()),
    thetaHat: Vector3f = rHat.cross(randomDirection(), Vector3f()).normalize(Vector3f()),
    theta: Float = randomAngle()
): Pair<MassPosVel, MassPosVel> {
    val origin = Vector3f(0f, 0f, 0f)
    return twoBodySystem(
        g = g,
        m1 = m1,
        m2 = m2,
        t = t,
        e = e,
        baryPos = origin,
        baryVel = origin,
        rHat = rHat,
        thetaHat = thetaHat,
        theta = theta
    )
}


fun twoBodySystem(
    baryPos: Vector3f,
    baryVel: Vector3f,
    m1: Float,
    m2: Float,
    g: Float,
    t: Float,
    e: Float,
    rHat: Vector3f = randomDirection().normalize(Vector3f()),
    thetaHat: Vector3f = rHat.cross(randomDirection(), Vector3f()).normalize(Vector3f()),
    theta: Float = randomAngle()
): Pair<MassPosVel, MassPosVel> {
    val mu = m1 * m2 / (m1 + m2)
    val (rVec, vVec) = getCentralForceProblemPositionAndVelocity(
        g = g,
        m1 = m1,
        m2 = m2,
        t = t,
        e = e,
        rHat = rHat,
        thetaHat = thetaHat,
        theta = theta
    )

    val r1Vec = baryPos + mu / m1 * rVec
    val v1Vec = baryVel + mu / m1 * vVec

    val r2Vec = baryPos - mu / m2 * rVec
    val v2Vec = baryVel - mu / m2 * vVec

    val b1 = PointMass(m1, r1Vec, v1Vec)
    val b2 = PointMass(m2, r2Vec, v2Vec)

    return Pair(b1, b2)
}


fun getCentralForceProblemPositionAndVelocity(
    g: Float,
    m1: Float,
    m2: Float,
    t: Float,
    e: Float,
    rHat: Vector3f,
    thetaHat: Vector3f,
    theta: Float
): Pair<Vector3f, Vector3f> {
    val mu: Float = m1 * m2 / (m1 + m2)
    val m = m1 + m2


    val gamma = g * m1 * m2

    val l = m1 * m2 * (g * g * t / 2f / PI.toFloat() / m).pow(1f / 3f) / sqrt(1f - e * e)

    val rVec = (l * l / mu / gamma) / (1f + e * cos(theta)) * rHat
    val vVec = (gamma / l) * (e * sin(theta) * rHat + (1f + e * cos(theta)) * thetaHat)

    return Pair(rVec, vVec)
}

fun List<MassPosVel>.totalEnergy(g: Float): Float {
    return totalEnergyOf(bodies = this, g = g)
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

internal fun newtonianIndexPairs(numberOfObjects: Int): Array<IndexPair> {
    val pairs = mutableListOf<IndexPair>()
    for (indexPair in combinationsOfTwoUniqueUntil(numberOfObjects)) {
        pairs.add(indexPair)
    }
    return pairs.toTypedArray()
}


internal fun newtonianIndexPairs(indices: Set<Int>): Array<IndexPair> {
    val indicesList = indices.toList()
    return newtonianIndexPairs(indices.size)
        .map { indexPair ->
            val i = indexPair.first
            val j = indexPair.second
            IndexPair(indicesList[i], indicesList[j])
        }
        .toTypedArray()
}

internal fun randomDistributionAveragingTo(total: Float, n: Int): List<Float> {
    val masses = (0 until n).map { randomFloatBetween(0f, 1f) }
    val tot = masses.sum()
    return masses.map { it * total / tot }
}


