package no.sigurof.gravity.demo

import no.sigurof.grajuny.camera.Camera
import no.sigurof.grajuny.camera.impl.SpaceShipCamera
import no.sigurof.grajuny.color.BLUE
import no.sigurof.grajuny.color.Gradient
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
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.demoSolarSystemWithMoons2
import no.sigurof.gravity.physics.gravity.newtonian.utils.newtonianForcePairs
import no.sigurof.gravity.simulation.Simulation
import no.sigurof.gravity.simulation.integration.verlet.VerletIntegrator
import no.sigurof.gravity.simulation.integration.verlet.VerletState
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f


//val avgSpeed = recording[2].vel
//    .map { it.length() }
//    .reduce { subTotal, next -> subTotal + next } / recording[2].vel.size.toFloat()
//val weight: Vector3f = (WHITE - defaultColor) / (avgSpeed)
//objects[i].surface.color = defaultColor + weight * image.vel[i].length()

class PlanetCircleGame(
    window: Long
) : Game(window = window, background = Vector4f(0f, 0f, 0f, 1f)) {
    private var light: PointLight
    private var planetObjs: List<GameObject>
    private var simulation: Simulation<VerletState>
    private var characteristicV: Float? = null

    //    private var simulation: Simulation<VerletState>
    private val camera: Camera
    private val gradient = Gradient(
        start = BLUE,
        end = RED
    )

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
//        val objects = generateCircleOfPlanets(g = g, numberOfPlanetPairs = 11, radius = 3f)
//        val objects = demoRandomGravityNode()
//        val objects = demoStarWithManySatellites(n = 10)
//        val objects = demoSolarSystemWithMoons1()
//        val objects = demoSolarSystemWithMoons2()
//        val objects = demoSolarSystemAndMoons2()
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
                    radius = 1f
                )
            ).at(it.r).build()
        }
        planetObjs.forEach {
            TraceRenderer.Builder(color = WHITE, numberOfPoints = 200)
                .firstPos(it.getPosition())
                .attachTo(it)
                .build()
        }
        root.addChild(
            GameObject.withChildren(
                planetObjs
            ).build()
        )
        camera = SpaceShipCamera(
            window = window,
            parent = root,
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
//        characteristicV ?: run {
//            characteristicV = state.pos.zip(state.posOld).map { (it.first - it.second).length() }.average().toFloat()
//        }
//        val v = state.pos.zip(state.posOld).map { (it.first - it.second).length() }
//        planetObjs.zip(v).forEach {
//            val value = it.second / characteristicV!!
//            val evaluate = gradient.evaluate(value)
//            ((it.first.components[0] as SphereBillboardRenderer).material as PhongMaterial).color =
//                evaluate
//        }
//        light.position.set(planetObjs.last().getPosition())

    }
}