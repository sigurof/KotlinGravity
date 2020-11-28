package no.sigurof.gravity.physics.utils

import org.joml.Vector3f

class GeneralState(
    val pos: Array<Vector3f>,
    val vel: Array<Vector3f>,
    val acc: Array<Vector3f>,
    val m: Array<Float>
)