package no.sigurof.gravity.utils

import no.sigurof.gravity.MassPosVel
import org.joml.Vector3f

class PointMass(
    override val m: Float,
    override var r: Vector3f,
    override var v: Vector3f
) : MassPosVel