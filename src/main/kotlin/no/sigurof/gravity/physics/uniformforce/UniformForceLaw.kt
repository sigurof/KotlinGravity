package no.sigurof.gravity.physics.uniformforce

import no.sigurof.gravity.physics.DissipativeForceLaw
import no.sigurof.gravity.simulation.integration.Integrator
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f

class UniformForceLaw(
    private val force: Vector3f
) : DissipativeForceLaw {
    override fun writeToAcceleration(
        pos: Array<Vector3f>,
        vel: Array<Vector3f>,
        acc: Array<Vector3f>,
        mass: Array<Float>
    ) {
    }

    override fun  updateAcc(integrator: Integrator<*>) {
        for (i in integrator.a.indices) {
            val f = force
            integrator.a[i] += f
        }
    }
}

