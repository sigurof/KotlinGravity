package no.sigurof.gravity.physics.hookeslaw

import no.sigurof.gravity.physics.ForceLaw
import no.sigurof.gravity.physics.utils.GeneralState
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f

class SpringState(
    val r: Vector3f,
    val v: Vector3f
)

class HarmonicForceLaw(
    private val harmonicOscillation: HarmonicOscillation
) : ForceLaw {


    override fun forceBetween(i: Int, j: Int, state: GeneralState): Vector3f {
        return harmonicOscillation.forceOnFrom(state.pos[i], state.vel[i], state.pos[j], state.vel[j])
    }
}


interface HarmonicOscillation {
    fun forceOnFrom(r1: Vector3f, v1: Vector3f, r2: Vector3f, v2: Vector3f): Vector3f
}

class BasicHarmonic(
    private val equilibriumDistance: Float,
    private val springConstant: Float
) : HarmonicOscillation {
    override fun forceOnFrom(r1: Vector3f, v1: Vector3f, r2: Vector3f, v2: Vector3f): Vector3f {
        val r21 = r2 - r1
        return springConstant * (r21.length() - equilibriumDistance) * r21.normalized()
    }
}


class DampedHarmonic(
    private val equilibriumDistance: Float,
    private val springConstant: Float,
    private val dampingTerm: Float
) : HarmonicOscillation {
    override fun forceOnFrom(r1: Vector3f, v1: Vector3f, r2: Vector3f, v2: Vector3f): Vector3f {
        val r21 = r2 - r1
        return springConstant * (r21.length() - equilibriumDistance) * r21.normalized() - dampingTerm * (v1 - v2)
    }
}


class DampedDrivenHarmonic(
    private val equilibriumDistance: Float,
    private val springConstant: Float,
    private val dampingTerm: Float,
    private val drivingTerm: Vector3f
) : HarmonicOscillation {
    override fun forceOnFrom(r1: Vector3f, v1: Vector3f, r2: Vector3f, v2: Vector3f): Vector3f {
        val r21 = r2 - r1
        return drivingTerm + springConstant * (r21.length() - equilibriumDistance) * r21.normalized() - dampingTerm * (v1 - v2)
    }
}
