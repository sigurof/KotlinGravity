package no.sigurof.gravity.physics.gravity.newtonian

import no.sigurof.gravity.physics.ConservativeForceModel
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f


class NewtonianGravityModel(
    private val g: Float
) : ConservativeForceModel {

    override fun addAccelerationContribution(
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