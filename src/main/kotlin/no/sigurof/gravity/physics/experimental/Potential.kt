package no.sigurof.gravity.physics.experimental

import org.joml.Vector3f

interface Potential {
    fun <T> updateAcc(integrator: Integrator<T>)
}

interface ConservativePotential : Potential {
    fun writeToAcceleration(pos: Array<Vector3f>, acc: Array<Vector3f>, mass: Array<Float>)
}

interface NonConservativePotential: Potential{
    fun writeToAcceleration(pos: Array<Vector3f>, vel: Array<Vector3f>, acc: Array<Vector3f>, mass: Array<Float>)
}