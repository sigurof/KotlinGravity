package no.sigurof.gravity.simulation.verlet

/*

internal class VerletTest : BehaviorSpec() {
    init {
        given("a list of parameters") {
            val origin = Vector3f(0f, 0f, 0f)
            val expectedNumberOfFrames = 5
            val g = 1f
            val dt = 1f
            val stepsPerFrame = 1
            val objects = listOf(
                PointMass(0.1f, origin, origin),
                PointMass(0.2f, Vector3f(1f, 0f, 0f), origin)
            )
            `when`("I run the simulation") {
                val positions = Simulation(
                    numFrames = expectedNumberOfFrames,
                    stepsPerFrame = stepsPerFrame,
                    integrator = VerletIntegrator(
                        forceLaws = listOf(
                            NewtonianForceLaw(g)
                        ),
                        m = objects.map { it.m }.toTypedArray(),
                        initialPositions = objects.map { it.r }.toTypedArray(),
                        initialVelocities = objects.map { it.v }.toTypedArray(),
                        dt = dt,
                        forcePairs = newtonianForcePairs(objects.size)
                    )
                ).record {
                    it.pos
                }
                then("The number of frames captured is equal to the requested number") {
                    positions.size shouldBe expectedNumberOfFrames
                }
            }
        }
    }
}*/
