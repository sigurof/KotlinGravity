package no.sigurof.gravity.physics

import org.joml.Vector3f

// TODO instead of force pairs, generalize to forceGroup: Array<Array<Int>>
interface ConservativeForceModel {
    fun addAccelerationContribution(a: Array<Vector3f>, r: Array<Vector3f>, m: Array<Float>, forcePairs: Array<Pair<Int, Int>>)
}


interface NonConservativeForceModel {
    fun addAccelerationContribution(a: Array<Vector3f>, v: Array<Vector3f>, r: Array<Vector3f>, m: Array<Float>, forcePairs: Array<Pair<Int, Int>>)
}
