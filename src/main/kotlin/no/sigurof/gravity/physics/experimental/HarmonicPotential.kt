package no.sigurof.gravity.physics.experimental

import no.sigurof.gravity.physics.ForcePair
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f

class HarmonicPotential(
    private val harmonicOscillation: HarmonicOscillation,
    private val forcePairs: Array<ForcePair>
) : NonConservativePotential {
    override fun writeToAcceleration(
        pos: Array<Vector3f>,
        vel: Array<Vector3f>,
        acc: Array<Vector3f>,
        mass: Array<Float>
    ) {
        for ((i, j) in forcePairs) {
            val f = harmonicOscillation.forceOnFrom(pos[i], vel[i], pos[j], vel[j])
            acc[i] += f
            acc[j] -= f
        }
        for (i in acc.indices) {
            acc[i] = acc[i] / mass[i]
        }
    }

    override fun <T> updateAcc(integrator: Integrator<T>) {
        for ((i, j) in forcePairs) {
            val f = harmonicOscillation.forceOnFrom(integrator.r[i], integrator.v[i], integrator.r[j], integrator.v[j])
            integrator.a[i] += f
            integrator.a[j] -= f
        }
        for (i in integrator.a.indices) {
            integrator.a[i] = integrator.a[i] / integrator.m[i]
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
