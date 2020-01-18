package no.sigurof.gravity.physics.hookeslaw

import no.sigurof.gravity.physics.NonConservativeForceModel
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f


class HookesLaw(
    private val harmonicOscillation: HarmonicOscillation
) : NonConservativeForceModel {

    override fun addAccelerationContribution(
        a: Array<Vector3f>,
        v: Array<Vector3f>,
        r: Array<Vector3f>,
        m: Array<Float>,
        forcePairs: Array<Pair<Int, Int>>
    ) {
        for ((i, j) in forcePairs) {
            val f = harmonicOscillation.forceOnFrom(r[i], r[j], v[i], v[j])
            a[i] += f
            a[j] -= f
        }
        for (i in a.indices) {
            a[i] = a[i] / m[i]
        }
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
