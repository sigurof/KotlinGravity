package no.sigurof.gravity.physics.experimental

import no.sigurof.gravity.physics.ForcePair
import no.sigurof.gravity.simulation.numerics.eulerStepR
import no.sigurof.gravity.simulation.numerics.verletStepR
import org.joml.Vector3f

class VerletState(
    val pos: List<Vector3f>,
    val acc: List<Vector3f>,
    val t: Float
) {
}

class VerletIntegrator(
    private val masses: Array<Float>,
    initialPositions: Array<Vector3f>,
    initialVelocities: Array<Vector3f>,
    private val forcePairs: Array<ForcePair>,
    private val dt: Float,
    private val potential: ConservativePotential
) : Integrator<VerletState> {
    private val r: List<Array<Vector3f>> = listOf(
        initialPositions.copyOf(),
        Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    )
    private val initialVelocities = initialVelocities.copyOf()
    private val a = Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    private var t = 0.0f
    private var newPosIndex = 1
    private var lastPosIndex = 0
    private var stepper: () -> Unit = ::firstIteration
    private var temp: Int = 0

    override fun step() {
        stepper()
        temp = newPosIndex
        newPosIndex = lastPosIndex
        lastPosIndex = temp
        t += dt
    }

    override fun updateAcceleration() {
        for (i in a.indices) {
            a[i] = Vector3f(0f, 0f, 0f)
        }
        potential.updateAcceleration(a, r[lastPosIndex], masses, forcePairs)
    }

    override fun getState(): VerletState {
        return VerletState(r[lastPosIndex].toList(), a.toList(), t)
    }

    private fun iteration() {
        for (i in a.indices) {
            r[newPosIndex][i] = verletStepR(
                r[lastPosIndex][i],
                r[newPosIndex][i],
                a[i],
                dt
            )
        }
    }

    private fun firstIteration() {
        for (i in a.indices) {
            r[newPosIndex][i] = eulerStepR(
                r[lastPosIndex][i],
                initialVelocities[i],
                a[i],
                dt
            )
        }
        stepper = ::iteration
    }

}