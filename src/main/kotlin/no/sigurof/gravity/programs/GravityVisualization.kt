package no.sigurof.gravity.programs

import no.sigurof.grajuny.utils.randomFloatBetween
import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.*
import no.sigurof.gravity.simulation.Simulation
import no.sigurof.gravity.simulation.integration.verlet.VerletIntegrator
import org.joml.Vector3f

fun sim1() {
    val g = 0.981f
    val stepsPerFrame = 5
    val numberOfFrames = 5000
    val dt = 0.005f
    val objects = simulateASolarSystem(g = g)
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    val positions = Simulation(
        integrator = VerletIntegrator(
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            m = objects.map { it.m }.toTypedArray(),
            dt = dt,
            forceLaws = listOf(newtonianPotential)
        ),
        stepsPerFrame = stepsPerFrame,
        numFrames = numberOfFrames
    ).iterate {
        it.pos
    }

    visualize(objects, positions)
}

fun sim2() {
    val g = 0.981f
    val stepsPerFrame = 5
    val numberOfFrames = 5000
    val dt = 0.005f
    val objects = simulateSolarSystemWithMoons()
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    val positions = Simulation(
        integrator = VerletIntegrator(
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            m = objects.map { it.m }.toTypedArray(),
            dt = dt,
            forceLaws = listOf(newtonianPotential)
        ),
        stepsPerFrame = stepsPerFrame,
        numFrames = numberOfFrames
    ).iterate {
        it.pos
    }

    visualize(objects, positions)
}

fun sim3() {
    val g = 0.981f
    val stepsPerFrame = 5
    val numberOfFrames = 5000
    val dt = 0.005f
    val objects = simulateSolarSystemWithMoons2()
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    val positions = Simulation(
        integrator = VerletIntegrator(
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            m = objects.map { it.m }.toTypedArray(),
            dt = dt,
            forceLaws = listOf(newtonianPotential)
        ),
        stepsPerFrame = stepsPerFrame,
        numFrames = numberOfFrames
    ).iterate {
        it.pos
    }

    visualize(objects, positions)
}

fun simulateASolarSystem(
    g: Float = 0.981f,
    numObjects: Int = 10,
    mass: Pair<Float, Float> = Pair(1f, 20f),
    time: Pair<Float, Float> = Pair(1f, 20f),
    ecc: Pair<Float, Float> = Pair(0f, 0.8f),
    msun: Float = 200f,
    originPos: Vector3f = Vector3f(0f, 0f, 0f),
    originVel: Vector3f = Vector3f(0f, 0f, 0f)
): List<MassPosVel> {
    return aSolarSystem(
        g = g,
        msun = msun,
        ms = (0 until numObjects).map { randomFloatBetween(mass.first, mass.second) }.toTypedArray(),
        ts = (0 until numObjects).map { randomFloatBetween(time.first, time.second) }.toTypedArray(),
        es = (0 until numObjects).map { randomFloatBetween(ecc.first, ecc.second) }.toTypedArray(),
        baryPos = originPos,
        baryVel = originVel
    )
}


fun simulateSolarSystemWithMoons(
    g: Float = 0.981f,
    originPos: Vector3f = Vector3f(0f, 0f, 0f),
    originVel: Vector3f = Vector3f(0f, 0f, 0f)
): List<MassPosVel> {
    return solarSystemWithMoons(
        g = g,
        node = getASolarSystem(),
        baryPos = originPos,
        baryVel = originVel
    )

}


fun simulateSolarSystemWithMoons2(
    g: Float = 0.981f,
    originPos: Vector3f = Vector3f(0f, 0f, 0f),
    originVel: Vector3f = Vector3f(0f, 0f, 0f)
): List<MassPosVel> {
    return solarSystemWithMoons(
        g = g,
        node = getRandomGravityNode(
            mass = 1000f,
            orbitalPeriod = 500f,
            eccentricityBetween = Pair(0.0f, 0.5f),
            numPlanets = Pair(2, 5),
            remainingDepth = 2
        ),
        baryVel = originVel,
        baryPos = originPos
    )
}

fun gravity() {
    val stepsPerFrame = 5
    val numberOfFrames = 5000
    val g = 0.981f
    val dt = 0.005f
    val numObjects = 10
    val mass = Pair(1f, 20f)
    val time = Pair(1f, 20f)
    val ecc = Pair(0f, 0.8f)
    val msun = 200f
    val originPos = Vector3f(0f, 0f, 0f)
    val originVel = Vector3f(0f, 0f, 0f)
    var objects = aSolarSystem(
        g = g,
        msun = msun,
        ms = (0 until numObjects).map { randomFloatBetween(mass.first, mass.second) }.toTypedArray(),
        ts = (0 until numObjects).map { randomFloatBetween(time.first, time.second) }.toTypedArray(),
        es = (0 until numObjects).map { randomFloatBetween(ecc.first, ecc.second) }.toTypedArray(),
        baryPos = originPos,
        baryVel = originVel
    )
    objects = solarSystemWithMoons(
        g = g,
        node = getASolarSystem(),
        baryPos = originPos,
        baryVel = originVel
    )
    objects = solarSystemWithMoons(
        g = g,
        node = getRandomGravityNode(
            mass = 1000f,
            orbitalPeriod = 500f,
            eccentricityBetween = Pair(0.0f, 0.5f),
            numPlanets = Pair(2, 5),
            remainingDepth = 2
        ),
        baryVel = originVel,
        baryPos = originPos
    )
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    val positions = Simulation(
        integrator = VerletIntegrator(
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            m = objects.map { it.m }.toTypedArray(),
            dt = dt,
            forceLaws = listOf(newtonianPotential)
        ),
        stepsPerFrame = stepsPerFrame,
        numFrames = numberOfFrames
    ).iterate {
        it.pos
    }

    visualize(objects, positions)
}
