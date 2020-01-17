package no.sigurof.gravity.physics.gravity.newtonian

import no.sigurof.gravity.physics.Model
import no.sigurof.gravity.physics.gravity.forceBetween
import no.sigurof.gravity.utils.maths.combinatorics.combinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f


class NewtonianGravityModel(
    private val g: Float
) : Model {

    override fun writeAccelerations(a: Array<Vector3f>, r: Array<Vector3f>, m: Array<Float>) {
        for (i in a.indices) {
            a[i] = Vector3f(0f, 0f, 0f)
        }
        for ((i, j) in combinationsOfTwoUniqueUntil(a.size)) {
            val f = forceBetween(r[i], r[j], m[i], m[j], g)
            a[i] += f
            a[j] -= f
        }
        for (i in a.indices) {
            a[i] = a[i] / m[i]
        }
    }

}