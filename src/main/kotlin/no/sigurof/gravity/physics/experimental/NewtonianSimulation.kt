package no.sigurof.gravity.physics.experimental

import no.sigurof.gravity.physics.gravity.newtonian.forceBetween
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f


class NewtonianSimulation(
    private val g: Float
) : ConservativePotential {

    override fun updateAcceleration(
        a: Array<Vector3f>,
        r: Array<Vector3f>,
        m: Array<Float>,
        forcePairs: Array<Pair<Int, Int>>
    ) {
        for ((i, j) in forcePairs) {
            val f =
                forceBetween(r[i], r[j], m[i], m[j], g)
            a[i] += f
            a[j] -= f
        }
        for (i in a.indices) {
            a[i] = a[i] / m[i]
        }
    }
}