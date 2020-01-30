package no.sigurof.gravity.simulation.integration.verlet

import no.sigurof.gravity.physics.ConservativeForceLaw
import no.sigurof.gravity.simulation.integration.Integrator
import no.sigurof.gravity.simulation.integration.utils.eulerStepR
import no.sigurof.gravity.simulation.integration.utils.verletStepR
import org.joml.Vector3f

const val VELOCITY_ERROR =
    """ERROR! You are trying to access the velocity of a verlet integrator. 
 This error is caused because the verlet algorithm does not keep the instantaneous
 velocity of the particles and so cannot answer to the request.
 The most likely cause of this problem is that you are using the
 Verlet integrator together with a NonConservativePotential, the latter of 
 which would require the velocity to find the next acceleration."""


class VerletState(
    val pos: List<Vector3f>,
    val acc: List<Vector3f>,
    val t: Float
)

class VerletIntegrator(
    override val m: Array<Float>,
    initialPositions: Array<Vector3f>,
    initialVelocities: Array<Vector3f>,
    private val dt: Float,
    private val forceLaws: List<ConservativeForceLaw>
) : Integrator<VerletState> {
    private val p: List<Array<Vector3f>> = listOf(
        initialPositions.copyOf(),
        Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    )
    override val r: Array<Vector3f>
        get() = p[lastPosIndex]
    private val initialVelocities = initialVelocities.copyOf()
    override val a = Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    override val v: Array<Vector3f>
        get() = error(VELOCITY_ERROR)
    override var t = 0.0f
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

    override fun getState(): VerletState {
        return VerletState(
            p[lastPosIndex].toList(),
            a.toList(),
            t
        )
    }

    private fun iteration() {
        for (i in a.indices) {
            p[newPosIndex][i] = verletStepR(
                p[lastPosIndex][i],
                p[newPosIndex][i],
                a[i],
                dt
            )
        }
    }

    private fun firstIteration() {
        for (i in a.indices) {
            p[newPosIndex][i] = eulerStepR(
                p[lastPosIndex][i],
                initialVelocities[i],
                a[i],
                dt
            )
        }
        stepper = ::iteration
    }


    override fun updateAcceleration(){
        for (i in a.indices) {
            a[i] = Vector3f(0f, 0f, 0f)
        }
        for (potential in forceLaws){
            potential.updateAcc(this)
        }
        for (i in a.indices) {
            a[i] = a[i] / m[i]
        }
    }

}