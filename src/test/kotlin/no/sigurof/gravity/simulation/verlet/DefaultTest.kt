package no.sigurof.gravity.simulation.verlet

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.physics.gravity.newtonian.NewtonianGravityModel
import no.sigurof.gravity.simulation.settings.StepsPerFrame
import org.joml.Vector3f


internal class DefaultTest : BehaviorSpec() {
    init {
        given("a list of parameters") {
            val origin = Vector3f(0f, 0f, 0f)
            val expectedNumberOfFrames = 5
            val g = 1f
            val dt = 1f
            val stepsPerFrame = 1
            val planets = listOf(
                PointMass(0.1f, origin, origin),
                PointMass(0.2f, Vector3f(1f, 0f, 0f), origin)
            )
            `when`("I run the simulation") {
                val positions: List<List<Vector3f>> = Verlet.simulationOf(
                    model = NewtonianGravityModel(g),
                    masses = planets.map { it.m }.toTypedArray(),
                    initialPositions = planets.map { it.r }.toTypedArray(),
                    initialVelocities = planets.map { it.v }.toTypedArray(),
                    settings = StepsPerFrame(
                        dt = dt,
                        numFrames = expectedNumberOfFrames,
                        numStepsPerFrame = stepsPerFrame
                    )
                ).iterate { r, _, _ ->
                    r
                }
                then("The number of frames captures is equal to the requested number") {
                    positions.size shouldBe expectedNumberOfFrames
                }
            }
        }
    }
}