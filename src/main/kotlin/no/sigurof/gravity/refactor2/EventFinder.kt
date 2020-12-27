package no.sigurof.gravity.refactor2

import no.sigurof.gravity.utils.operators.minus
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

interface EventFinder {
    fun findNextEvent(integrator: Integrator): Event?
}


class SphereCollider(
    val pos: Vector3f,
    val vel: Vector3f,
    val radius: Float
)

class MyEventFinder(
    private val collisionIndexPairs: List<Pair<Int, Int>>
) : EventFinder {


    override fun findNextEvent(integrator: Integrator): Event? {
        return collisionIndexPairs.mapNotNull { (i, j) ->
            findTimeOfCollisionH(
                SphereCollider(
                    pos = integrator.p[i],
                    vel = integrator.v[i],
                    radius = integrator.radii[i]
                ),
                SphereCollider(
                    pos = integrator.p[j],
                    vel = integrator.v[j],
                    radius = integrator.radii[j]
                )
            )?.let {
                SphereSphereCollision(
                    i1 = i,
                    i2 = j,
                    time = it + integrator.time
                )
            }
        }.minBy { it.time }
    }
}

fun findTimeOfCollisionH(one: SphereCollider, other: SphereCollider): Float? {
    val centerToCenter = one.radius + other.radius
    val otherPosMinusOnePos = other.pos - one.pos
    val otherVelMinusOneVel = other.vel - one.vel
    val c = otherPosMinusOnePos.lengthSquared() - centerToCenter.pow(2)
    val b = 2 * otherPosMinusOnePos.dot(otherVelMinusOneVel)
    val a = otherVelMinusOneVel.lengthSquared()
    val bsquaredMinus4ac = b.pow(2) - 4 * a * c
    if (bsquaredMinus4ac > 0) {
        val t1 = (-b + sqrt(bsquaredMinus4ac)) / 2 / a
        val t2 = (-b - sqrt(bsquaredMinus4ac)) / 2 / a
        if (t1 < 0 && t2 < 0) {
            // Time of collision is negative (i.e. may have collided in the past)
            return null;
        }
        if (t1 > 0 && t2 > 0) {
            // Both positive return the least time.
            return min(t1, t2)
        }
        // One of them is negative. Return the positive one
        return max(t1, t2)
    } else {
        // Time of collision is imaginary (i.e. will never collide)
        return null
    }
}
