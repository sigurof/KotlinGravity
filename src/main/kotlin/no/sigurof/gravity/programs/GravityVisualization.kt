package no.sigurof.gravity.programs

import no.sigurof.grajuny.utils.randomFloatBetween
import no.sigurof.gravity.physics.ConservativeForceLaw
import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.*
import no.sigurof.gravity.simulation.Simulation
import no.sigurof.gravity.simulation.integration.verlet.VerletIntegrator
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f
import kotlin.math.*

fun simuler(
    g: Float = 0.981f,
    stepsPerFrame: Int = 5,
    numberOfFrames: Int = 5000,
    dt: Float = 0.005f,
    objects: List<MassPosVel> = simulateASolarSystem(g = g),
    forceLaws: List<ConservativeForceLaw>
) {
    val positions = Simulation(
        integrator = VerletIntegrator(
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            m = objects.map { it.m }.toTypedArray(),
            dt = dt,
            forceLaws = forceLaws
        ),
        stepsPerFrame = stepsPerFrame,
        numFrames = numberOfFrames
    ).iterate {
        it.pos
    }
//    visualize(objects, positions[0].let { pos -> (0 until 1000).map { pos } })
    visualize(objects, positions);
}

fun sim1() {
    val g = 0.981f
    val objects = simulateASolarSystem(g = g)
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simuler(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}

fun sim2() {
    val g = 0.981f
    val objects = demoSolarSystemWithMoons1(g = g)
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simuler(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}

fun sim3() {
    val g = 0.981f
    val objects = demoSolarSystemWithMoons2(g = g)
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simuler(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}

fun demoCircleOfPlanets() {
    val g = 0.981f
    val objects = circleOfPlanets(g = g, numberOfPlanetPairs = 50, radius = 10f)
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simuler(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}


fun demoStarWithManySatellites() {
    val g = 0.981f
    val objects =
        GravityNode(
            1000f,
            satellites = (0 until 100).map {
                GravityNode(
                    mass = 1f,
                    satellites = listOf(),
                    period = 10f,
                    eccentricity = 0f
                )
            },
            period = 0f,
            eccentricity = 0.9f

        ).buildPlanets(
            g = g,
            baryPos = Vector3f(0f, 0f, 0f),
            baryVel = Vector3f(0f, 0f, 0f)
        )
    val newtonianPotential = NewtonianForceLaw(
        g = g,
        forcePairs = newtonianForcePairs(objects.size)
    )
    simuler(g = g, objects = objects, forceLaws = listOf(newtonianPotential))
}


fun circleOfPlanets(g: Float = 0.981f, numberOfPlanetPairs: Int = 1, radius: Float = 5f): List<MassPosVel> {
    val m = 0.1f;
    val normal = Vector3f(0f, 0f, 1f)
    val positions: List<Vector3f> = (0 until numberOfPlanetPairs)
        .map { i ->
            val angle = i * 2f * PI / numberOfPlanetPairs
            Vector3f(cos(angle).toFloat(), sin(angle).toFloat(), 0f).times(radius)
        }
    val v = sqrt(g * m *
            positions
                .last()
                .let { referencePos ->
                    positions
                        .minus(referencePos)
                        .map { pos -> (referencePos - pos).let { d -> d / d.length().pow(3) } }
                        .reduce { subTotal, next -> subTotal + next }
                        .dot(referencePos)
                }

    )
    return positions
        .map { pos ->
            PointMass(
                m,
                pos,
                normal.cross(pos, Vector3f()).normalized().times(v)
            )
        }

}

fun demoSolarSystemWithMoons2(
    g: Float = 0.981f,
    originPos: Vector3f = Vector3f(0f, 0f, 0f),
    originVel: Vector3f = Vector3f(0f, 0f, 0f)
): List<MassPosVel> {
    return buildPlanetsFromGravityNode(
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
    return buildASolarSystem(
        g = g,
        msun = msun,
        ms = (0 until numObjects).map { randomFloatBetween(mass.first, mass.second) }.toTypedArray(),
        ts = (0 until numObjects).map { randomFloatBetween(time.first, time.second) }.toTypedArray(),
        es = (0 until numObjects).map { randomFloatBetween(ecc.first, ecc.second) }.toTypedArray(),
        baryPos = originPos,
        baryVel = originVel
    )
}

fun demoSolarSystemWithMoons1(
    g: Float = 0.981f,
    originPos: Vector3f = Vector3f(0f, 0f, 0f),
    originVel: Vector3f = Vector3f(0f, 0f, 0f)
): List<MassPosVel> {
    return buildPlanetsFromGravityNode(
        g = g,
        node = getSunEarthMoonGravityNode(),
        baryPos = originPos,
        baryVel = originVel
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
    var objects = buildASolarSystem(
        g = g,
        msun = msun,
        ms = (0 until numObjects).map { randomFloatBetween(mass.first, mass.second) }.toTypedArray(),
        ts = (0 until numObjects).map { randomFloatBetween(time.first, time.second) }.toTypedArray(),
        es = (0 until numObjects).map { randomFloatBetween(ecc.first, ecc.second) }.toTypedArray(),
        baryPos = originPos,
        baryVel = originVel
    )
    objects = buildPlanetsFromGravityNode(
        g = g,
        node = getSunEarthMoonGravityNode(),
        baryPos = originPos,
        baryVel = originVel
    )
    objects = buildPlanetsFromGravityNode(
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
