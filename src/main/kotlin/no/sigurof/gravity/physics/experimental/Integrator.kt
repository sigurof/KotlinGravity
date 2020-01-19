package no.sigurof.gravity.physics.experimental

import org.joml.Vector3f

interface Integrator<T> {
    val m: Array<Float>
    val r: Array<Vector3f>
    // TODO Put m, r, v, a, in their own datatype and make Integrator generic wrt that datatype
    val v: Array<Vector3f>
    val a: Array<Vector3f>
    val t: Float
    fun step()
    fun getState(): T
    fun updateAcceleration()
}

