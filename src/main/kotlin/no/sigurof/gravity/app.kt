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
import no.sigurof.grajuny.utils.randomFloatBetween
import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.experimental.EulerIntegrator
import no.sigurof.gravity.physics.experimental.NewtonianSimulation
import no.sigurof.gravity.physics.experimental.Simulation
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianGravityModel
import no.sigurof.gravity.physics.gravity.newtonian.aSolarSystem
import no.sigurof.gravity.physics.gravity.newtonian.newtonianForcePairs
import no.sigurof.gravity.physics.hookeslaw.DampedHarmonic
import no.sigurof.gravity.physics.hookeslaw.HookesLaw
import no.sigurof.gravity.utils.operators.minus
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.pow


fun main() {
    gravity()
}

/**
 * EulersMethod: Simulation
 * Verlet : Simulation
 * VelocityVerlet : Simulation
 * LeapFrog : Simulation
 *
 *
 *
 * NewtonianGravity : Model
 * SchwarzhildMetric : Model
 * DoublePendulum : Model
 */


fun gravity() {
    val origin = Vector3f(0f, 0f, 0f)
    val numberOfFrames = 5000
    val g = 9.81f
    val dt = 0.01f
    val stepsPerFrame = 5

    val objects = aSolarSystem(
        g,
        20f,
        (0 until 3).map { randomFloatBetween(0.5f, 3f) }.toTypedArray(),
        (0 until 3).map { randomFloatBetween(1f, 8f) }.toTypedArray(),
        origin,
        origin
    )
//
///*
/*
    val objects = listOf(
        PointMass(10f, randomDirection() * randomFloatBetween(0.0f, 5f), origin),
        PointMass(10f, randomDirection() * randomFloatBetween(0.0f, 5f), origin),
        PointMass(10f, randomDirection() * randomFloatBetween(0.0f, 5f), origin),
        PointMass(10f, randomDirection() * randomFloatBetween(0.0f, 5f), origin),
        PointMass(10f, randomDirection() * randomFloatBetween(0.0f, 5f), origin),
        PointMass(10f, randomDirection() * randomFloatBetween(0.0f, 5f), origin),
        PointMass(10f, randomDirection() * randomFloatBetween(0.0f, 5f), origin)
    )
*/
//*/

    val hookesLawModel = HookesLaw(
        DampedHarmonic(
            equilibriumDistance = 5f,
            springConstant = 5f,
            dampingTerm = 6f
        )
    )
    val newtonianModel = NewtonianGravityModel(
        g = g
    )

    /**
     * Som jeg ser avhenger den spesifikke fysikkmodellen av forskjellige parametre.
     * Newton avhenger kun av posisjon mens dempet oscillator avhenger av r og v.
     * Dette ser ut til å tyde på at jeg har invertert ansvarsforholdet
     *
     * Å la den spesifikke modellen inneholde pos, vel og acc istedenfor algoritmen
     * vil også gjøre at jeg ikke må sende inn akselerasjon som en output-parameter
     * inn i modellen. Vil også gjøre at forcePairs kan sendes direkte inn der de
     * brukes
     *
     *
     *
     * */

//    val positions = DefaultSimulation(
//        g = g,
//        forcePairs = newtonianForcePairs(objects.size),
//        masses = objects.map { it.m }.toTypedArray(),
//        initialPositions = objects.map { it.r }.toTypedArray(),
//        initialVelocities = objects.map { it.v }.toTypedArray(),
//        settings = StepsPerFrame(
//            dt = dt,
//            numFrames = numberOfFrames,
//            numStepsPerFrame = stepsPerFrame
//        )
//    ).iterate { r, _, _, _ ->
//        r
//    }


//    val positions: List<List<Vector3f>> = Verlet.simulationOf(
//        model = NewtonianGravityModel(g = g),
//        forcePairs = newtonianForcePairs(objects.size),
//        masses = objects.map { it.m }.toTypedArray(),
//        initialPositions = objects.map { it.r }.toTypedArray(),
//        initialVelocities = objects.map { it.v }.toTypedArray(),
//        settings = StepsPerFrame(
//            dt = dt,
//            numFrames = numberOfFrames,
//            numStepsPerFrame = stepsPerFrame
//        )
//    ).iterate { r, _, _ ->
//        r
//    }
    val positions = Simulation(
        integrator = EulerIntegrator(
            masses = objects.map { it.m }.toTypedArray(),
            forcePairs = newtonianForcePairs(objects.size),
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            dt = dt,
            potential = NewtonianSimulation(
                g = g
            )
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
            .position(Vector3f(0f, 100f, 0f))
            .ambient(0.15f)
            .build()
        val camera = Camera.Builder()
            .at(Vector3f(10f, 10f, 10f))
            .lookingAt(recording[0][0])
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
        var frame = 0
        while (DisplayManager.isOpen()) {
            DisplayManager.eachFrameDo {
                println(frame)
                for ((i: Int, position: Vector3f) in recording[frame].withIndex()) {
                    objects[i].position = position
                }
                scenario.run()
                frame = (frame + 1) % recording.size
            }
        }
        scenario.cleanUp()
    }
}

fun debugDistances(positions: List<Vector3f>){
    val distances = mutableListOf<Float>()
    for (i in 0 until positions.size - 2) {
        distances.add((positions[i] - positions[i + 1]).length())
    }
    println("$distances")
}
