package no.sigurof.gravity.physics.gravity.newtonian

import no.sigurof.gravity.physics.ForceLaw
import no.sigurof.gravity.physics.gravity.newtonian.utils.forceBetweenn
import no.sigurof.gravity.physics.utils.GeneralState
import org.joml.Vector3f


class NewtonianForceLaw(
    private val g: Float
) : ForceLaw {


    override fun forceBetween(i: Int, j: Int, state: GeneralState): Vector3f {
        return forceBetweenn(
            state.pos[i], state.pos[j], state.m[i], state.m[j], g
        )
    }
}