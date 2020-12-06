package no.sigurof.gravity.demo

import org.joml.Vector3f


sealed class Shape() {
    abstract fun collides(other: Shape, distance: Float): Collision.Builder?
}

data class PerfectSphere(val radius: Float) : Shape() {
    override fun collides(other: Shape, distance: Float): Collision.Builder? {
        return when (other) {
            is PerfectSphere -> sphereCollision(this, other, distance)
            is PerfectPlane -> planeCollision(this, other)
        }
    }

    private fun planeCollision(one: PerfectSphere, other: PerfectPlane): Collision.Builder? {
        TODO("Not yet implemented")
    }

    private fun sphereCollision(one: PerfectSphere, other: PerfectSphere, distance: Float): Collision.Builder? {
        if (one.radius + other.radius >= distance) {
            return Collision.Builder()
        }
        return null
    }
}

data class PerfectPlane(val normal: Vector3f) : Shape() {
    override fun collides(other: Shape, distance: Float): Collision.Builder? {
        TODO("Not yet implemented")
    }
}
