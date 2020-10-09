package no.sigurof.gravity.demo

import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.*
import no.sigurof.gravity.programs.simulateVerletAndVisualize
import no.sigurof.gravity.utils.randomFloatBetween
import org.joml.Vector3f


fun demoSolarSystem() {
    val g = 0.981f
    val objects = simulateASolarSystem(g = g)
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simulateVerletAndVisualize(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}

fun demoSolarSystemAndMoons() {
    val g = 0.981f
    val objects = demoSolarSystemWithMoons1(g = g)
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simulateVerletAndVisualize(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}

fun demoSolarSystemAndMoons2() {
    val g = 0.981f
    val objects = demoSolarSystemWithMoons2(g = g)
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simulateVerletAndVisualize(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}

fun demoCircleOfPlanets() {
    val g = 0.981f
    val objects = generateCircleOfPlanets(g = g, numberOfPlanetPairs = 10, radius = 2f)
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simulateVerletAndVisualize(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}

fun demoStarWithManySatellites() {
    val g = 0.981f
    val objects =
        GravityNode(
            100f,
            satellites = (0 until 100).map {
                GravityNode(
                    mass = randomFloatBetween(0.1f, 10f),
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
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simulateVerletAndVisualize(dt = 0.001f, g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}

fun demoRandomGravityNode() {
    val g = 0.981f
    val originPos = Vector3f(0f, 0f, 0f)
    val originVel = Vector3f(0f, 0f, 0f)
    val objects = buildRandomGravityNode(
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
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simulateVerletAndVisualize(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}
