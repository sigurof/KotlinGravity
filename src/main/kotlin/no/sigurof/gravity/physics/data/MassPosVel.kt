package no.sigurof.gravity.physics.data

import org.joml.Vector3f

interface MassPosVel {
    val m: Float
    var r: Vector3f
    var v: Vector3f
}