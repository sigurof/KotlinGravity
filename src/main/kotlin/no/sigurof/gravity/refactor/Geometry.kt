package no.sigurof.gravity.demo

import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f


sealed class Geometry(var center: Vector3f) {
    abstract fun intersects(that: Geometry, pos: Vector3f, pos1: Vector3f): Boolean
}


class PerfectSphere(var radius: Float, center: Vector3f) : Geometry(center = center) {
    override fun intersects(that: Geometry, pos: Vector3f, pos1: Vector3f): Boolean {
        return when (that) {
            is PerfectSphere -> sphereIntersect(this.center + pos, this.radius, that.center + pos1, that.radius)
            is PerfectPlane -> planeIntersect(this.radius, pos + this.center, that.center + pos1, that.unitNormal)
        }
    }
}

class PerfectPlane(pointOnPlane: Vector3f, normal: Vector3f) : Geometry(center = pointOnPlane) {
    val unitNormal = normal.normalized()
    override fun intersects(that: Geometry, pos: Vector3f, pos1: Vector3f): Boolean {
        TODO("Not yet implemented")
    }

    fun distanceFrom(point: Vector3f): Float {
        return (point - center).dot(unitNormal)
    }
}


private fun planeIntersect(
    radius: Float,
    spherePos: Vector3f,
    posOnPlane: Vector3f,
    planeUnitNormal: Vector3f
): Boolean {
    val distanceToPlane = (spherePos - posOnPlane).dot(planeUnitNormal)
    return distanceToPlane <= radius
}

private fun sphereIntersect(pos1: Vector3f, radius1: Float, pos2: Vector3f, radius2: Float): Boolean {
    return (pos2 - pos1).length() <= (radius1 + radius2)

}
