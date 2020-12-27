package no.sigurof.gravity.physics.gravity.newtonian.force

import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import org.joml.Vector3f

internal fun forceBetween(r1: Vector3f, r2: Vector3f, m1: Float, m2: Float, g: Float): Vector3f {
    val r12 = r2 - r1
    val d = r12.normalized() / r12.lengthSquared()
    return d.mul(g * m1 * m2)
}
