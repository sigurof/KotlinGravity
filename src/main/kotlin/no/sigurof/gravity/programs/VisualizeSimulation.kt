package no.sigurof.gravity.programs

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
import no.sigurof.grajuny.utils.WHITE
import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.pow

val defaultColor = Vector3f(0.0f, 0.0f, 0.9f)


fun visualize(planets: List<MassPosVel>, recording: List<List<Vector3f>>) {
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
            ResourceManager.getBillboardResource(),
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
                frame = (frame + 1) % recording.size
                for ((i: Int, position: Vector3f) in recording[frame].withIndex()) {
                    objects[i].position = position
                    objects[i].surface.color = defaultColor + objects[i].position
                }
                scenario.run()
            }
        }
        scenario.cleanUp()
    }
}

fun visualizeWithVel(planets: List<MassPosVel>, recording: List<ImagePosVel>) {
    val density = 100f
    DisplayManager.withWindowOpen { window ->
        val light = Light.Builder()
            .position(recording.first().pos.first())
            .ambient(0.15f)
            .build()
        val camera = Camera.Builder()
            .at(Vector3f(10f, 10f, 10f))
            .lookingAt(recording.first().pos.last())
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
            ResourceManager.getBillboardResource(),
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

        val avgSpeed = recording[2].vel
            .map { it.length() }
            .reduce { subTotal, next -> subTotal + next } / recording[2].vel.size.toFloat()
        val weight: Vector3f = (WHITE - defaultColor) / (avgSpeed)
        var frame = -1
        scenario.prepare()
        while (DisplayManager.isOpen()) {
            DisplayManager.eachFrameDo {
                frame = (frame + 1) % recording.size
                val image: ImagePosVel = recording[frame]
                for (i: Int in image.pos.indices) {
                    light.position = image.pos.first()
                    objects[i].position = image.pos[i]
                    objects[i].surface.color = defaultColor + weight * image.vel[i].length()
                }
                scenario.run()
            }
        }
        scenario.cleanUp()
    }
}
