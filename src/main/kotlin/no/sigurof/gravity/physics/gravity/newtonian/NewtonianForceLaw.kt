package no.sigurof.gravity.physics.gravity.newtonian

import no.sigurof.gravity.physics.ConservativeForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.forceBetween
import no.sigurof.gravity.physics.utils.ForcePair
import no.sigurof.gravity.simulation.integration.Integrator
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f


class NewtonianForceLaw(
    private val g: Float,
    private val forcePairs: Array<ForcePair>
) : ConservativeForceLaw {
    override fun writeToAcceleration(pos: Array<Vector3f>, acc: Array<Vector3f>, mass: Array<Float>) {
        for ((i, j) in forcePairs) {
            val f =
                forceBetween(
                    pos[i],
                    pos[j],
                    mass[i],
                    mass[j],
                    g
                )
            acc[i] += f
            acc[j] -= f
        }
    }

    // TODO Should not take in Integrator. Should take in a ForceState.
    override fun updateAcc(integrator: Integrator<*>) {
        for ((i, j) in forcePairs) {
            val f =
                forceBetween(
                    integrator.r[i],
                    integrator.r[j],
                    integrator.m[i],
                    integrator.m[j],
                    g
                )
            integrator.a[i] += f
            integrator.a[j] -= f
        }
    }
}