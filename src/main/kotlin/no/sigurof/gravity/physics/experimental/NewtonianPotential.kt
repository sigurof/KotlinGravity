package no.sigurof.gravity.physics.experimental

import no.sigurof.gravity.physics.ForcePair
import no.sigurof.gravity.physics.gravity.newtonian.forceBetween
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus


class NewtonianPotential(
    private val g: Float,
    private val forcePairs: Array<ForcePair>
) : Potential {

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