package no.sigurof.gravity.physics

import no.sigurof.gravity.simulation.integration.Integrator
import org.joml.Vector3f

interface ForceLaw {
    fun updateAcc(integrator: Integrator<*>)
}

interface ConservativeForceLaw : ForceLaw {
    fun writeToAcceleration(pos: Array<Vector3f>, acc: Array<Vector3f>, mass: Array<Float>)
}

interface DissipativeForceLaw: ForceLaw {
    fun writeToAcceleration(pos: Array<Vector3f>, vel: Array<Vector3f>, acc: Array<Vector3f>, mass: Array<Float>)
}