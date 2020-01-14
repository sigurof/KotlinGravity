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
import no.sigurof.gravity.model.newtonian.NewtonianModel
import no.sigurof.gravity.model.newtonian.NewtonianSettings
import no.sigurof.gravity.model.newtonian.Recording
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.pow


fun main() {
    gravity()
}

data class PositionsEnergy(
    val positions: List<Vector3f>,
    val energy: Float
)

fun gravity() {
    val origin = Vector3f(0f, 0f, 0f)
    val numberOfFrames = 10000
    val g = 9.81f
    val dt = 0.0025f
    val stepsPerFrame = 4

//    val planets = getSunEarth(g, 3f, 1f, 1f, origin, origin)
    val planets = getSunEarthMars(g, 100f, 3f, 1f, 1f, 1f, origin, origin)

    val recording = Recording.of(
        NewtonianModel(
            planets,
            NewtonianSettings(g, dt)
        ),
        stepsPerFrame,
        numberOfFrames
    ) { bodies ->
        PositionsEnergy(
            bodies.map { body -> body.r },
            calculateEnergy(bodies, g)
        )
    }
    visualize(planets, recording)
}

fun visualize(planets: List<MassPosVel>, recording: Recording<PositionsEnergy>) {
    val density = 100f
    DisplayManager.withWindowOpen { window ->
        val light = Light.Builder()
            .position(Vector3f(0f, 100f, 0f))
            .ambient(0.15f)
            .build()
        val camera = Camera.Builder()
            .at(Vector3f(10f, 10f, 10f))
            .lookingAt(recording.positionImages[0].positions[0])
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

        val models = mutableListOf(renderer)
        val context = DefaultSceneContext(
            camera = camera,
            light = light
        )
        val background = Vector4f(0f, 0f, 0f, 1f)

        DisplayManager.FPS = 60
        val scenario = Scenario(window, models, context, background)

        scenario.prepare()
        var frame = 0
        while (DisplayManager.isOpen()) {
            DisplayManager.eachFrameDo {
                for ((i, position) in recording.positionImages[frame].positions.withIndex()) {
                    objects[i].position = position
                }
                scenario.run()
                frame = (frame + 1) % recording.positionImages.size
            }
        }
        scenario.cleanUp()
    }
}
