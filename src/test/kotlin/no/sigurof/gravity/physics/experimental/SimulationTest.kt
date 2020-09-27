package no.sigurof.gravity.physics.experimental

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import no.sigurof.grajuny.utils.randomFloatBetween
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.buildASolarSystem
import no.sigurof.gravity.physics.gravity.newtonian.utils.newtonianForcePairs
import no.sigurof.gravity.simulation.Simulation
import no.sigurof.gravity.simulation.integration.verlet.VerletIntegrator
import org.joml.Vector3f


internal class SimulationTest : StringSpec() {
    init {
        "A Simulation " should {
            val origin = Vector3f(0f, 0f, 0f)
            val numberOfFrames = 5000
            val g = 9.81f
            val dt = 0.01f
            val stepsPerFrame = 5

            val objects = buildASolarSystem(
                g,
                20f,
                (0 until 3).map { randomFloatBetween(0.5f, 3f) }.toTypedArray(),
                (0 until 3).map { randomFloatBetween(1f, 8f) }.toTypedArray(),
                (0 until 3).map { randomFloatBetween(0f, 0.8f) }.toTypedArray(),
                origin,
                origin
            )
            val positions = Simulation(
                integrator = VerletIntegrator(
                    initialPositions = objects.map { it.r }.toTypedArray(),
                    initialVelocities = objects.map { it.v }.toTypedArray(),
                    m = objects.map { it.m }.toTypedArray(),
                    forceLaws = listOf(
                        NewtonianForceLaw(
                            g = g,
                            forcePairs = newtonianForcePairs(
                                objects.size
                            )
                        )
                    ),
                    dt = dt
                ),
                stepsPerFrame = stepsPerFrame,
                numFrames = numberOfFrames
            ).iterate {
                it.pos
            }
            " take as many frames as supplied in the arguments" {
                positions.size shouldBe numberOfFrames
            }
        }
    }
}