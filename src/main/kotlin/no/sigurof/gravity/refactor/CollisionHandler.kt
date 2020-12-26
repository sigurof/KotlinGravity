package no.sigurof.gravity.demo

import no.sigurof.gravity.utils.operators.unaryMinus
import org.joml.Vector3f

class Momentum(
    val vel: Vector3f,
    val m: Float
)

object CollisionHandler {

    fun handle(it: Collision, state: SimulatorState) {
        val one = state.getCollider(it.id1)
        val other = state.getCollider(it.id2)
        println("Collision between ${it.id1} and ${it.id2}")
        val (vel1, vel2) = velocityOutOfCollisionBetween(one, other)
        state.setVelocity(it.id1, vel1)
        state.setVelocity(it.id2, vel2)
    }

    private fun velocityOutOfCollisionBetween(
        one: GeometryMomentum,
        other: GeometryMomentum
    ): Pair<Vector3f, Vector3f> {
        val (g1, g2) = Pair(one.geometry, other.geometry);
        return if (one.geometry is PerfectSphere && other.geometry is PerfectSphere) {
            sphereOnSphere(one, other)
        } else if (g1 is PerfectSphere && g2 is PerfectPlane) {
            sphereOnPlane(one, other)
        } else if (g2 is PerfectSphere && g1 is PerfectPlane) {
            sphereOnPlane(other, one).let { Pair(it.second, it.first) }
        } else {
            error("Not implemented!")
        }
    }

    private fun sphereOnPlane(one: GeometryMomentum, other: GeometryMomentum): Pair<Vector3f, Vector3f> {
        return Pair(-one.vel, Vector3f())
    }

    private fun sphereOnSphere(one: GeometryMomentum, other: GeometryMomentum): Pair<Vector3f, Vector3f> {
        return Collisions.Elastic.sphereOnSphere(
            Collisions.Elastic.Particle(
                m = one.m,
                v = one.vel,
                r = one.pos
            ),
            Collisions.Elastic.Particle(
                m = other.m,
                v = other.vel,
                r = other.pos
            )
        )

    }


}