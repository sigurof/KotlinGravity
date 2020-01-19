package no.sigurof.gravity.simulation.integration.euler

import no.sigurof.gravity.physics.ForceLaw
import no.sigurof.gravity.simulation.integration.Integrator
import no.sigurof.gravity.simulation.integration.utils.eulerStepRV
import org.joml.Vector3f

class EulerState(
    val pos: List<Vector3f>,
    val vel: List<Vector3f>,
    val acc: List<Vector3f>,
    val t: Float
)

class EulerIntegrator(
    override val m: Array<Float>,
    initialPositions: Array<Vector3f>,
    initialVelocities: Array<Vector3f>,
    private val forceLaws: List<ForceLaw>,
    private val dt: Float
) : Integrator<EulerState> {
    override val r: Array<Vector3f> = initialPositions.copyOf()
    override val v = initialVelocities.copyOf()
    override val a = Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    override var t = 0.0f

    override fun step() {
        iteration()
        t += dt
    }

    override fun getState(): EulerState {
        return EulerState(
            r.toList(),
            v.toList(),
            a.toList(),
            t
        )
    }

    override fun updateAcceleration(){
        for (i in a.indices) {
            a[i] = Vector3f(0f, 0f, 0f)
        }
        for (potential in forceLaws){
            potential.updateAcc(this)
        }
    }

    private fun iteration() {
        for (i in a.indices) {
            val posVel =
                eulerStepRV(r[i], v[i], a[i], dt)
            r[i] = posVel.first
            v[i] = posVel.second
        }
    }

//    override fun zeroOutAcceleration() {
//        for (i in a.indices) {
//            a[i] = Vector3f(0f, 0f, 0f)
//        }
//    }

}
