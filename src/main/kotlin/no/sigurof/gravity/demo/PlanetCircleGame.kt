package no.sigurof.gravity.demo

import no.sigurof.grajuny.camera.Camera
import no.sigurof.grajuny.color.BLUE
import no.sigurof.grajuny.color.Gradient
import no.sigurof.grajuny.color.RED
import no.sigurof.grajuny.color.WHITE
import no.sigurof.grajuny.components.SphereBillboardRenderer
import no.sigurof.grajuny.game.Game
import no.sigurof.grajuny.light.LightSource
import no.sigurof.grajuny.node.GameObject
import no.sigurof.grajuny.resource.material.ReflectiveMaterial
import no.sigurof.grajuny.shader.shaders.SphereBillboardShader
import no.sigurof.grajuny.utils.ORIGIN
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.newtonianForcePairs
import no.sigurof.gravity.simulation.Simulation
import no.sigurof.gravity.simulation.integration.verlet.VerletIntegrator
import no.sigurof.gravity.simulation.integration.verlet.VerletState
import no.sigurof.gravity.utils.operators.minus
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.pow


//val avgSpeed = recording[2].vel
//    .map { it.length() }
//    .reduce { subTotal, next -> subTotal + next } / recording[2].vel.size.toFloat()
//val weight: Vector3f = (WHITE - defaultColor) / (avgSpeed)
//objects[i].surface.color = defaultColor + weight * image.vel[i].length()

class PlanetCircleGame(
    window: Long
) : Game(window = window, background = Vector4f(0f, 0f, 0f, 1f)) {
    private var light: LightSource = LightSource.Builder().position(Vector3f(0f, 15f, 0f)).build()
    private var planetObjs: List<GameObject>
    private var simulation: Simulation<VerletState>
    private var characteristicV: Float? = null

    //    private var simulation: Simulation<VerletState>
    override val camera: Camera
    private val gradient = Gradient(
        start = BLUE,
        end = RED
    )

    init {
        val stepsPerFrame = 5
        val numberOfFrames = 5000
        val dt = 0.005f
        val g = 0.981f
//        val objects = generateCircleOfPlanets(g = g, numberOfPlanetPairs = 5, radius = 3f)
//        val objects = demoRandomGravityNode()
        val objects = demoStarWithManySatellites()
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
        planetObjs = objects.map {
            GameObject.withComponent(
                SphereBillboardRenderer(
                    texture = null,
                    material = ReflectiveMaterial(WHITE, 10f, 1000f),
                    shadersToUse = listOf(SphereBillboardShader),
                    position = ORIGIN,
                    radius = it.m.pow(0.5f) / 5f
                )
            ).at(it.r).build()
        }
//        planetObjs.forEach {
//            TraceRenderer.Builder(color = WHITE, numberOfPoints = 2)
//                .firstPos(it.getPosition())
//                .attachTo(it)
//                .build()
//        }
        root.addChild(
            GameObject.withChildren(
                planetObjs
            ).build()
        )
        camera = Camera.Builder()
            .at(Vector3f(2f, objects.map { it.r.length() }.max()!!, 0f))
            .capturingMouseInput(window)
            .lookingAt(ORIGIN).build()

    }

    override fun onUpdate() {
        simulation.step()
        val state = simulation.getState()
        planetObjs.zip(state.pos).forEach {
            it.first.transform = Matrix4f().translate(it.second)
        }
        characteristicV ?: run {
            characteristicV = state.pos.zip(state.posOld).map { (it.first - it.second).length() }.average().toFloat()
        }
        val v = state.pos.zip(state.posOld).map { (it.first - it.second).length() }
        planetObjs.zip(v).forEach {
            val value = it.second / characteristicV!!
            val evaluate = gradient.evaluate(value)
            ((it.first.components[0] as SphereBillboardRenderer).material as ReflectiveMaterial).color =
                evaluate
        }
        light.position = planetObjs.last().getPosition()

    }
}