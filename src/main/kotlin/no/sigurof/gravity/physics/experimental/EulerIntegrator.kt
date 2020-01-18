package no.sigurof.gravity.physics.experimental

import no.sigurof.gravity.physics.ForcePair
import no.sigurof.gravity.simulation.numerics.eulerStepR
import org.joml.Vector3f

class EulerState(
    val pos: List<Vector3f>,
    val vel: List<Vector3f>,
    val acc: List<Vector3f>,
    val t: Float
) {
}

class EulerIntegrator(
    private val masses: Array<Float>,
    initialPositions: Array<Vector3f>,
    initialVelocities: Array<Vector3f>,
    private val forcePairs: Array<ForcePair>,
    private val dt: Float,
    private val potential: ConservativePotential
) : Integrator<EulerState> {
    private val r: Array<Vector3f> = initialPositions.copyOf()
    private val v = initialVelocities.copyOf()
    private val a = Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    private var t = 0.0f

    override fun step() {
        iteration()
        t += dt
    }

    override fun updateAcceleration() {
        for (i in a.indices) {
            a[i] = Vector3f(0f, 0f, 0f)
        }
        potential.updateAcceleration(a, r, masses, forcePairs)
    }

    override fun getState(): EulerState {
        return EulerState(r.toList(), v.toList(), a.toList(), t)
    }

    private fun iteration() {
        for (i in a.indices) {
            r[i] = eulerStepR(r[i], v[i], a[i], dt)
        }
    }
}
