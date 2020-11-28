package no.sigurof.gravity.physics.uniformforce

import no.sigurof.gravity.physics.ForceLaw
import no.sigurof.gravity.physics.utils.GeneralState
import org.joml.Vector3f


class UniformForceLaw(
    private val force: Vector3f
) : ForceLaw {

    override fun forceBetween(i: Int, j: Int, state: GeneralState): Vector3f {
        return force
    }
}

