package no.sigurof.gravity.physics

import no.sigurof.gravity.physics.utils.GeneralState
import org.joml.Vector3f

interface ForceLaw {
    fun forceBetween(i: Int, j: Int, state: GeneralState): Vector3f
}

