package no.sigurof.gravity.simulation.integration.euler

import no.sigurof.gravity.physics.utils.ForcePair
import no.sigurof.gravity.physics.utils.GeneralState
import no.sigurof.gravity.simulation.integration.Integrator
import no.sigurof.gravity.simulation.integration.utils.eulerStepRV
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f

class EulerState(
    val pos: List<Vector3f>,
    val vel: List<Vector3f>,
    val acc: List<Vector3f>,
    val t: Float
)

class EulerIntegrator(
    val m: Array<Float>,
    initialPositions: Array<Vector3f>,
    initialVelocities: Array<Vector3f>,
    private val dt: Float,
    override val forcePairs: Array<ForcePair<*>>
) : Integrator<EulerState> {
    val r: Array<Vector3f> = initialPositions.copyOf()
    val v = initialVelocities.copyOf()
    val a = Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    var t = 0.0f

    override fun step() {
        updateAcceleration()
        updatePositionAndVelocity()
        updateTime()
    }

    private fun updateTime() {
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

    private fun updateAcceleration() {
        for (i in a.indices) {
            a[i] = Vector3f(0f, 0f, 0f)
        }
        for (forcePair in forcePairs){
            val f = forcePair.force(
                GeneralState(
                    pos = r,
                    vel = v,
                    acc = a,
                    m = m

                )
            )
            a[forcePair.i] += f
            a[forcePair.j] -= f
        }
        for (i in a.indices) {
            a[i] = a[i] / m[i]
        }
    }

    private fun updatePositionAndVelocity() {
        for (i in a.indices) {
            val posVel =
                eulerStepRV(r[i], v[i], a[i], dt)
            r[i] = posVel.first
            v[i] = posVel.second
        }
    }

}
