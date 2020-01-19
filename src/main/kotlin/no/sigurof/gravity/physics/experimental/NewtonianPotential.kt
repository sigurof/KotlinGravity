package no.sigurof.gravity.physics.experimental

import no.sigurof.gravity.physics.ForcePair
import no.sigurof.gravity.physics.gravity.newtonian.forceBetween
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f


class NewtonianPotential(
    private val g: Float,
    private val forcePairs: Array<ForcePair>
) : ConservativePotential {
    override fun writeToAcceleration(pos: Array<Vector3f>, acc: Array<Vector3f>, mass: Array<Float>) {
        for ((i, j) in forcePairs) {
            val f =
                forceBetween(pos[i], pos[j], mass[i], mass[j], g)
            acc[i] += f
            acc[j] -= f
        }
        for (i in acc.indices) {
            acc[i] = acc[i] / mass[i]
        }
    }

    override fun <T> updateAcc(integrator: Integrator<T>) {
        for ((i, j) in forcePairs) {
            val f =
                forceBetween(integrator.r[i], integrator.r[j], integrator.m[i], integrator.m[j], g)
            integrator.a[i] += f
            integrator.a[j] -= f
        }
        for (i in integrator.a.indices) {
            integrator.a[i] = integrator.a[i] / integrator.m[i]
        }
    }
}