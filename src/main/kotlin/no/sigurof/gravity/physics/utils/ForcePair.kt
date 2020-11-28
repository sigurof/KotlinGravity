package no.sigurof.gravity.physics.utils

import no.sigurof.gravity.physics.ForceLaw
import org.joml.Vector3f

class ForcePair<F : ForceLaw>(
    val i: Int,
    val j: Int,
    private val forceLaw: F
) {
    fun force(state: GeneralState): Vector3f {
        return forceLaw.forceBetween(i, j, state)


    }
}