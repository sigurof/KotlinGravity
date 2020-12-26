package no.sigurof.gravity.demo

import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f

object Collisions {

    object Elastic {

        class Particle(
            val m: Float,
            val r: Vector3f,
            val v: Vector3f
        )


        fun sphereOnSphere(p1: Particle, p2: Particle): Pair<Vector3f, Vector3f> {
            val avgMass = (p1.m + p2.m) / 2f
            val x1mx2 = (p1.r - p2.r)
            val v1mv2Dotx1mx2 = (p1.v - p2.v).dot(x1mx2)
            val x1mx2lengthSquared = x1mx2.lengthSquared()
            val v1: Vector3f = p1.v - (p2.m / avgMass) * v1mv2Dotx1mx2 / x1mx2lengthSquared * (x1mx2)
            val v2: Vector3f = p2.v + (p1.m / avgMass) * v1mv2Dotx1mx2 / x1mx2lengthSquared * (x1mx2)
            return Pair(
                v1,
                v2
            )
        }

    }
}




