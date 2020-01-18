package no.sigurof.gravity.physics.experimental

import org.joml.Vector3f

interface ConservativePotential : Potential{
    fun updateAcceleration(a: Array<Vector3f>, r: Array<Vector3f>, m: Array<Float>, forcePairs: Array<Pair<Int, Int>>)
}