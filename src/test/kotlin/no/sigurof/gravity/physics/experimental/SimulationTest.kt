package no.sigurof.gravity.physics.experimental

/*

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
                            g = g
                        )
                    ),
                    dt = dt,
                    forcePairs = newtonianForcePairs(
                        objects.size
                    )
                ),
                stepsPerFrame = stepsPerFrame,
                numFrames = numberOfFrames
            ).record {
                it.pos
            }
            " take as many frames as supplied in the arguments" {
                positions.size shouldBe numberOfFrames
            }
        }
    }
}*/
