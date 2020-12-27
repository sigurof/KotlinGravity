package no.sigurof.gravity.refactor2

import org.joml.Vector3f

class PointMass(
    override val m: Float,
    override var r: Vector3f,
    override var v: Vector3f
) : MassPosVel