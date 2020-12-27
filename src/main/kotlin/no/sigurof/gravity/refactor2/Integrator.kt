package no.sigurof.gravity.refactor2

import org.joml.Vector3f

interface Integrator {
    val time: Float
    val dt: Float

    fun updateVelocity()
    fun handle(event: Event)
    fun getState(): List<MassPos2>
    var v: List<Vector3f>
    var p: List<Vector3f>
    var radii: MutableList<Float>
    var m: List<Float>
    fun setVel(index: Int, value: Vector3f)
}