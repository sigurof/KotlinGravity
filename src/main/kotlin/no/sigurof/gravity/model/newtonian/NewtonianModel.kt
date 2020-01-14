package no.sigurof.gravity.model.newtonian

import no.sigurof.gravity.MassPosVel
import no.sigurof.gravity.utils.combinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.normalized
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f

interface MassPosVelAcc : MassPosVel {
    val a: Vector3f
}

internal class BodyState(
    override var m: Float,
    override var r: Vector3f,
    override var v: Vector3f,
    override var a: Vector3f
) : MassPosVelAcc

internal class NewtonianSettings(
    internal val g: Float,
    internal val dt: Float
)

internal fun force(on: BodyState, from: BodyState, g: Float): Vector3f {
    val d12 = from.r - on.r
    val d = d12.normalized() / d12.lengthSquared()
    return g * on.m * from.m * d
}


internal fun newtonianStep(b: BodyState, dt: Float): Pair<Vector3f, Vector3f> = Pair(
    b.r + b.v * dt + 0.5f * b.a * dt * dt,
    b.v + b.a * dt
)


internal class NewtonianModel constructor(
    bodies: List<MassPosVel>,
    private val settings: NewtonianSettings
) {
    private val bodies: List<BodyState> = bodies.map { BodyState(it.m, it.r, it.v, Vector3f(0f, 0f, 0f)) }

    private fun singleStep() {
        this.zeroOutForces()
        for ((i, j) in combinationsOfTwoUniqueUntil(this.bodies.size)) {
            val f = force(bodies[i], bodies[j], settings.g)
            bodies[i].a += f
            bodies[j].a -= f
        }
        for (body in bodies) {
            body.a /= body.m
            val (r, v) = newtonianStep(body, settings.dt)
            body.r = r
            body.v = v
        }
    }

    private fun zeroOutForces() {
        for (body in bodies) {
            body.a = Vector3f(0f, 0f, 0f)
        }
    }

    fun exposingInternalState(func: (bodies: List<MassPosVelAcc>) -> Unit) {
        func(this.bodies)
    }

    fun doSteps(stepsPerFrame: Int) {
        for (i in 0 until stepsPerFrame) {
            singleStep()
        }
    }

}

