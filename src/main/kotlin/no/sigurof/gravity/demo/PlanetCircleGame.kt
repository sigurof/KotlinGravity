package no.sigurof.gravity.demo

import no.sigurof.grajuny.camera.Camera
import no.sigurof.grajuny.camera.impl.SpaceShipCamera
import no.sigurof.grajuny.color.RED
import no.sigurof.grajuny.color.WHITE
import no.sigurof.grajuny.components.SphereBillboardRenderer
import no.sigurof.grajuny.components.TraceRenderer
import no.sigurof.grajuny.game.Game
import no.sigurof.grajuny.light.LightManager
import no.sigurof.grajuny.light.phong.PointLight
import no.sigurof.grajuny.node.GameObject
import no.sigurof.grajuny.resource.material.PhongMaterial
import no.sigurof.grajuny.utils.ORIGIN
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw2
import no.sigurof.gravity.physics.gravity.newtonian.utils.demoSolarSystemWithMoons2
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.pow

class PlanetCircleGame(
    window: Long
) : Game(window = window, background = Vector4f(0f, 0f, 0f, 1f)) {
    private var light: PointLight
    private var planetObjs: List<GameObject>
    private var simulation: VerletSimulation

    private val camera: Camera

    init {

        light = PointLight(
            position = ORIGIN,
            constant = 1f,
            linear = 0.005f,
            quadratic = 0.001f,
            ambient = WHITE,
            diffuse = WHITE,
            specular = WHITE
        )
        LightManager.LIGHT_SOURCES.add(light)
        val stepsPerFrame = 5
        val dt = 0.01f
        val g = 0.981f
        val radius = 40f
        val objects = demoSolarSystemWithMoons2(g = g)
        simulation = VerletSimulation(
            initialStates = objects,
            dt = dt,
            forces = listOf(
//                ForceField(
//                    { it.m.times(Vector3f(0f, -1f, 0f)) },
//                    affects = objects.mapIndexed { i, _ -> i }
//                )
                NewtonianForceLaw2(
                    g = 0.981f,
                    affects = objects.indices.toList()
                )
            ),
            applyConstraints = { _, defaultNewPosition ->
                defaultNewPosition
            },
            stepsPerFrame = stepsPerFrame
        )
        val redMaterial = PhongMaterial(
            ambient = RED,
            diffuse = RED,
            specular = WHITE,
            shine = 0.1f
        )
        planetObjs = objects.map {
            GameObject.withComponent(
                SphereBillboardRenderer(
                    material = redMaterial,
                    position = ORIGIN,
                    radius = it.m.pow(1f / 3f) * 0.4f
                )
            ).at(it.r).build()
        }
        root.addChild(
            GameObject.withChildren(
                planetObjs
            ).build()
        )
        camera = SpaceShipCamera(
            window = window,
            parent = planetObjs.first(),
            at = Vector3f(5f, 5f, 5f),
            lookAt = ORIGIN
        )
        planetObjs.forEach {
            TraceRenderer.Builder(
                color = WHITE,
                numberOfPoints = 1000
            ).attachTo(it)
                .build()
        }

    }

    override fun onUpdate() {
        simulation.step()
        val objects = simulation.getState()
        planetObjs.zip(objects).forEach { obj ->
            val gphxObj = obj.first
            val simObj = obj.second
            gphxObj.transform = Matrix4f().translate(simObj.r)
        }
        light.position.set(planetObjs.last().getPosition())

    }

    fun onCollision(state: List<VerletSingleBody>){
        
    }
}