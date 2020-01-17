package no.sigurof.gravity.model

import org.joml.Vector3f

interface Model {
    fun writeAccelerations(a: Array<Vector3f>, r: Array<Vector3f>, m: Array<Float>)
}
