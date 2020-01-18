package no.sigurof.gravity.physics.uniformforce

import no.sigurof.gravity.physics.NonConservativeForceModel
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f

class UniformAccelerationForceModel(
    private val acceleration: Vector3f
) : NonConservativeForceModel {

    override fun addAccelerationContribution(
        a: Array<Vector3f>,
        v: Array<Vector3f>,
        r: Array<Vector3f>,
        m: Array<Float>,
        forcePairs: Array<Pair<Int, Int>>
    ) {
        for (i in a.indices) {
            a[i] += acceleration
        }
    }
}