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
import no.sigurof.gravity.model.newtonian.NewtonianGravityModel
import no.sigurof.gravity.simulation.verlet.StepsPerFrame
import no.sigurof.gravity.simulation.verlet.Verlet
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.pow
import kotlin.random.Random


fun main() {
    gravity()
}

data class PositionsEnergy(
    val positions: List<Vector3f>,
    val energy: Float
)

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
    val numberOfFrames = 1000
    val g = 9.81f
    val dt = 0.0025f
    val stepsPerFrame = 1

    val planets = aSolarSystem(
        g,
        10000f,
        (0 until 20).map { Random.nextDouble(0.5, 3.0).toFloat() }.toTypedArray(),
        (0 until 20).map { Random.nextDouble(0.5, 3.0).toFloat() }.toTypedArray(),
        origin,
        origin
    )

    val positions: List<List<Vector3f>> = Verlet.simulationOf(
        model = NewtonianGravityModel(g),
        masses = planets.map { it.m }.toTypedArray(),
        initialPositions = planets.map { it.r }.toTypedArray(),
        initialVelocities = planets.map { it.v }.toTypedArray(),
        settings = StepsPerFrame(
            dt = dt,
            numFrames = numberOfFrames,
            numStepsPerFrame = stepsPerFrame
        )
    ).iterate { r, a, t ->
        r
    }

    visualize(planets, positions)
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
