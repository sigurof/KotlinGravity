package no.sigurof.gravity.demo

import no.sigurof.grajuny.camera.Camera
import no.sigurof.grajuny.camera.impl.SpaceShipCamera
import no.sigurof.grajuny.color.RED
import no.sigurof.grajuny.color.WHITE
import no.sigurof.grajuny.components.SphereBillboardRenderer
import no.sigurof.grajuny.game.Game
import no.sigurof.grajuny.light.LightManager
import no.sigurof.grajuny.light.phong.PointLight
import no.sigurof.grajuny.node.GameObject
import no.sigurof.grajuny.resource.material.PhongMaterial
import no.sigurof.grajuny.utils.ORIGIN
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.demoSolarSystemWithMoons2
import no.sigurof.gravity.physics.gravity.newtonian.utils.newtonianForcePairs
import no.sigurof.gravity.simulation.Simulation
import no.sigurof.gravity.simulation.integration.verlet.VerletIntegrator
import no.sigurof.gravity.simulation.integration.verlet.VerletState
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.pow

class PlanetCircleGame(
    window: Long
) : Game(window = window, background = Vector4f(0f, 0f, 0f, 1f)) {
    private var light: PointLight
    private var planetObjs: List<GameObject>
    private var simulation: Simulation<VerletState>

    private val camera: Camera
    init {

        light = PointLight(
            position = ORIGIN,
            constant = 1f,
            linear = 0.1f,
            quadratic = 0.01f,
            ambient = WHITE,
            diffuse = WHITE,
            specular = WHITE
        )
        LightManager.LIGHT_SOURCES.add(light)
        val stepsPerFrame = 5
        val numberOfFrames = 5000
        val dt = 0.005f
        val g = 0.981f
        val objects = demoSolarSystemWithMoons2()
        val newtonianPotential = NewtonianForceLaw(
            g = g,
            forcePairs = newtonianForcePairs(objects.size)
        )
        simulation = Simulation(
            integrator = VerletIntegrator(
                initialPositions = objects.map { it.r }.toTypedArray(),
                initialVelocities = objects.map { it.v }.toTypedArray(),
                m = objects.map { it.m }.toTypedArray(),
                dt = dt,
                forceLaws = listOf(newtonianPotential)
            ),
            stepsPerFrame = stepsPerFrame,
            numFrames = numberOfFrames
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
                    radius = it.m.pow(1f/3f)
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


    }

    override fun onUpdate() {
        simulation.step()
        val state = simulation.getState()
        planetObjs.zip(state.pos).forEach {
            it.first.transform = Matrix4f().translate(it.second)
        }
        light.position.set(planetObjs.last().getPosition())

    }
}