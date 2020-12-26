package no.sigurof.gravity.demo

import no.sigurof.gravity.utils.operators.minus
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

sealed class Event {
}

data class Collision(
    val id2: Int,
    val id1: Int,
    val time: Float
) : Event()

data class Deletion(
    val toDelete: Int
) : Event()

object EventFinder {

//    private fun findDeletionEvents(state: SimulatorState): List<Event> {
//        return state.geometries
//            .mapIndexed { i, it -> Pair(i, it) }
//            .filter { it.second.pos.length() > 1000000f }
//            .map { (i, _) ->
//                Deletion(
//                    toDelete = i
//                )
//            }
//
//    }



}


class SphereCollider(
    val pos: Vector3f,
    val vel: Vector3f,
    val radius: Float
)

fun findTimeOfCollision(one: SphereCollider, other: SphereCollider, d: Float): Float? {
    val centerToCenter = one.radius + other.radius
    val onePosMinusOtherPos = other.pos - one.pos
    val oneVelMinusOtherVel = other.vel - one.vel
    val a = onePosMinusOtherPos.lengthSquared() - centerToCenter.pow(2)
    val b = 2 * onePosMinusOtherPos.dot(oneVelMinusOtherVel)
    val c = oneVelMinusOtherVel.lengthSquared()
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
