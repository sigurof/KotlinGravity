package no.sigurof.gravity.model.newtonian

import no.sigurof.gravity.model.Model
import no.sigurof.gravity.utils.combinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f


internal fun force(r1: Vector3f, r2: Vector3f, m1: Float, m2: Float, g: Float): Vector3f {
    val r12 = r2 - r1
    val d = r12.normalized() / r12.lengthSquared()
    return g * m1 * m2 * d
}

class NewtonianGravityModel(
    private val g: Float
) : Model {

    override fun writeAccelerations(a: Array<Vector3f>, r: Array<Vector3f>, m: Array<Float>) {
        for (i in a.indices) {
            a[i] = Vector3f(0f, 0f, 0f)
        }
        for ((i, j) in combinationsOfTwoUniqueUntil(a.size)) {
            val f = force(r[i], r[j], m[i], m[j], g)
            a[i] += f
            a[j] -= f
        }
        for (i in a.indices) {
            a[i] = a[i] / m[i]
        }
    }

}