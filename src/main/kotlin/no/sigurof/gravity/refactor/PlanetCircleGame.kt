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
import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw2
import no.sigurof.gravity.physics.gravity.newtonian.utils.simulateASolarSystem
import no.sigurof.gravity.refactor2.MassPos2
import no.sigurof.gravity.refactor2.SimulationEngine2
import no.sigurof.gravity.refactor2.SimulationEntity2
import no.sigurof.gravity.refactor2.VerletIntegrator
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.pow


/*
* Features
* Clean up code
* enable static objects
* enable other object types, like quads, perfect planes, etc.
* collision handling: colliding objects merge
* enable angular momentum - collision detection between rotating objects
* change colors based on velocity
* test other forces than gravity
* speedup recalculating of collisions within timestep by only recalculating collisions involving the two outgoing particles from the first collision
* */


class PlanetCircleGame(
    window: Long
) : Game(window = window, background = Vector4f(0f, 0f, 0f, 1f)) {
    private var light: PointLight
    private var planetObjs: List<GameObject>
    private var simulation: SimulationEngine2

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

        val solarModel = simulateASolarSystem(
            g = 9.81f,
            numObjects = 30,
            time = Pair(4f, 41f)
        )
        val objects = solarModel.map {
            Pair(
                it, PerfectSphere(
                    radius = it.m.pow(1f / 3f) * 0.4f,
                    center = Vector3f()
                )
            )
        }
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
                    radius = it.second.radius
                )
            ).at(it.second.center).build()
        }
        root.addChild(
            GameObject.withChildren(
                planetObjs
            ).build()
        )
        camera = SpaceShipCamera(
            window = window,
//            parent = planetObjs.first()
            at = Vector3f(5f, 5f, 5f),
            lookAt = ORIGIN
        )
        planetObjs.zip(objects).forEach {
            TraceRenderer.Builder(
                color = WHITE,
                numberOfPoints = 1000,
                firstPos = it.second.second.center
            ).attachTo(it.first)
                .build()
        }

        simulation = SimulationEngine2(
            integrator = VerletIntegrator(
                entities = objects.map {
                    SimulationEntity2(
                        m = it.first.m,
                        geometry = it.second,
                        vel = it.first.v,
                        pos = it.first.r
                    )
                },
                dt = 0.005f,
                forces = listOf(
                    NewtonianForceLaw2(
                        g = 9.81f,
                        affects = objects.indices.toSet()
                    )
                )
            )
        )
    }

    private fun twoColliding(): List<MassPosVel> {
        return listOf(
            PointMass(
                m = 1f,
                r = Vector3f(2f, 0f, 0f),
                v = Vector3f(-1f, 0f, 1f)
            ),
            PointMass(
                m = 1f,
                r = Vector3f(-2f, 0f, 0f),
                v = Vector3f(1f, 0f, 0f)
            )

        )

    }

    override fun onUpdate() {
        val objects: List<MassPos2> = simulation.getNextState()
        planetObjs.zip(objects).forEach { obj ->
            val gphxObj = obj.first
            val simObj = obj.second
            gphxObj.transform = Matrix4f().translate(simObj.r)
        }
        light.position.set(planetObjs.last().getPosition())

    }

}