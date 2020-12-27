package no.sigurof.gravity.demo

import no.sigurof.gravity.refactor2.GravityNode
import no.sigurof.gravity.refactor2.MassPosVel
import no.sigurof.gravity.refactor2.buildRandomGravityNode
import no.sigurof.gravity.refactor2.randomFloatBetween
import org.joml.Vector3f

fun demoStarWithManySatellites(n: Int): List<MassPosVel> {
    val g = 0.981f
    return GravityNode(
        1000f,
        satellites = (0 until n).map {
            GravityNode(
                mass = randomFloatBetween(0.1f, 1f),
                satellites = listOf(),
                period = 5f,
                eccentricity = 0.7f
            )
        },
        period = 0f,
        eccentricity = 0.1f

    ).buildPlanets(
        g = g,
        baryPos = Vector3f(0f, 0f, 0f),
        baryVel = Vector3f(0f, 0f, 0f)
    )
}

fun demoRandomGravityNode(): List<MassPosVel> {
    val g = 0.981f
    val originPos = Vector3f(0f, 0f, 0f)
    val originVel = Vector3f(0f, 0f, 0f)
    return buildRandomGravityNode(
        mass = 1000f,
        orbitalPeriod = 500f,
        eccentricityBetween = Pair(0.0f, 0.5f),
        numPlanets = Pair(2, 5),
        remainingDepth = 2
    ).buildPlanets(
        g = g,
        baryVel = originVel,
        baryPos = originPos
    )
}
