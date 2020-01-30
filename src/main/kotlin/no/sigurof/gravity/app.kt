package no.sigurof.gravity

import no.sigurof.grajuny.context.DefaultSceneContext
import no.sigurof.grajuny.engine.DisplayManager
import no.sigurof.grajuny.entity.Camera
import no.sigurof.grajuny.entity.Light
import no.sigurof.grajuny.entity.obj.SphereBillboardObject
import no.sigurof.grajuny.entity.surface.DiffuseSpecularSurface
import no.sigurof.grajuny.renderer.CommonRenderer
import no.sigurof.grajuny.resource.ResourceManager
import no.sigurof.grajuny.scenario.Scenario
import no.sigurof.grajuny.shaders.settings.impl.BillboardShaderSettings
import no.sigurof.grajuny.utils.randomDirection
import no.sigurof.grajuny.utils.randomFloatBetween
import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.*
import no.sigurof.gravity.physics.hookeslaw.DampedHarmonic
import no.sigurof.gravity.physics.hookeslaw.HarmonicForceLaw
import no.sigurof.gravity.physics.uniformforce.UniformForceLaw
import no.sigurof.gravity.simulation.Simulation
import no.sigurof.gravity.simulation.integration.euler.EulerIntegrator
import no.sigurof.gravity.simulation.integration.verlet.VerletIntegrator
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.pow

// TODO
/**
 * Features to implement:
 *
 * More customizable solar systems (medium)
 * Coloring based on speed (easy +)
 * Multithreading calculations (hard+)
 * Simultaneous simulation with animation (hard ++)
 *
 *
 * */


fun main() {
//    harmonic()
    gravity()
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
            mass = 1000f ,
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


fun harmonic() {
    val stepsPerFrame = 1
    val numberOfFrames = 5000
    val dt = 0.01f
    val originVel = Vector3f(0f, 0f, 0f)
    val equilibriumDistance = 3.75f
    val springConstant = 200f
    val dampingTerm = 2f
    val w = 10
    val h = 10
    val objects = (0 until w * h).map {
        val i = it % w
        val j = (it - i) / w
        PointMass(
            1f, randomDirection() * randomFloatBetween(3.5f, 4f), originVel
        )
    }
    val constForce = UniformForceLaw(Vector3f(0f, -1f, 0f))
    val harmonicPotential = HarmonicForceLaw(
        harmonicOscillation = DampedHarmonic(
            equilibriumDistance
            , springConstant
            , dampingTerm
        )
        , forcePairs = newtonianForcePairs(objects.size)
    )
    val positions = Simulation(
        integrator = EulerIntegrator(
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            m = objects.map { it.m }.toTypedArray(),
            forceLaws = listOf(harmonicPotential, constForce),
            dt = dt
        ),
        stepsPerFrame = stepsPerFrame,
        numFrames = numberOfFrames
    ).iterate {
        it.pos
    }
    visualize(objects, positions)
}

fun visualize(planets: List<MassPosVel>, recording: List<List<Vector3f>>) {
    println(recording.size)
    val density = 100f
    DisplayManager.withWindowOpen { window ->
        val light = Light.Builder()
            .position(recording.first().last())
            .ambient(0.15f)
            .build()
        val camera = Camera.Builder()
            .at(Vector3f(10f, 10f, 10f))
            .lookingAt(recording[0][recording[0].size - 1])
            .withSpeed(12f)
            .build()
        val white = Vector3f(1f, 1f, 1f)
        val reflectivity = 1f
        val damper = 100f

        val objects: MutableList<SphereBillboardObject> = planets.map {
            SphereBillboardObject(
                DiffuseSpecularSurface(damper, reflectivity, white),
                it.r,
                (it.m / density).pow(1f / 3f)
            )
        }.toCollection(mutableListOf())
        val renderer = CommonRenderer(
            BillboardShaderSettings,
            ResourceManager.getBillboardResource(camera),
            objects
        )

        val context = DefaultSceneContext(
            camera = camera,
            light = light
        )
        val background = Vector4f(0f, 0f, 0f, 1f)

        DisplayManager.FPS = 60
        val models = mutableListOf(renderer)
        val scenario = Scenario(window, models, context, background)

        scenario.prepare()
        var frame = -1
        while (DisplayManager.isOpen()) {
            DisplayManager.eachFrameDo {
                println(frame)
                frame = (frame + 1) % recording.size
                for ((i: Int, position: Vector3f) in recording[frame].withIndex()) {
                    objects[i].position = position

                }
                scenario.run()
            }
        }
        scenario.cleanUp()
    }
}

fun debugDistances(positions: List<Vector3f>) {
    val distances = mutableListOf<Float>()
    for (i in 0 until positions.size - 2) {
        distances.add((positions[i] - positions[i + 1]).length())
    }
    println("$distances")
}
