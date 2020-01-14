package no.sigurof.gravity.model.newtonian

import io.kotlintest.IsolationMode
import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import no.sigurof.gravity.utils.operators.randomFloatBetween
import org.joml.Vector3f


internal class NewtonianModelKtTest : StringSpec() {
    override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerTest

    init {
        val dt: Float = randomFloatBetween(0.0, 100.0)
        val rx: Float = randomFloatBetween(-5.0, 5.0)
        val ry: Float = randomFloatBetween(-5.0, 5.0)
        val rz: Float = randomFloatBetween(-5.0, 5.0)
        val vx: Float = randomFloatBetween(-5.0, 5.0)
        val vy: Float = randomFloatBetween(-5.0, 5.0)
        val vz: Float = randomFloatBetween(-5.0, 5.0)
        val ax: Float = randomFloatBetween(-5.0, 5.0)
        val ay: Float = randomFloatBetween(-5.0, 5.0)
        val az: Float = randomFloatBetween(-5.0, 5.0)
        val bodyState = BodyState(
            1f,
            Vector3f(rx, ry, rz),
            Vector3f(vx, vy, vz),
            Vector3f(ax, ay, az)
        )


        "newtonian step" should {
            val newPosVel = newtonianStep(bodyState, dt)
            val pos = newPosVel.first
            val vel = newPosVel.second
            "calculate next position as x0 + v0*dt + ½a0*dt²"{
                pos.x.shouldBe(rx + vx * dt + 0.5f * ax * dt * dt plusOrMinus 0.001f)
                pos.y.shouldBe(ry + vy * dt + 0.5f * ay * dt * dt plusOrMinus 0.001f)
                pos.z.shouldBe(rz + vz * dt + 0.5f * az * dt * dt plusOrMinus 0.001f)
            }
            "calculate next velocity as v0 + a0*dt"{
                vel.x.shouldBe(vx + ax * dt plusOrMinus 0.001f)
                vel.y.shouldBe(vy + ay * dt plusOrMinus 0.001f)
                vel.z.shouldBe(vz + az * dt plusOrMinus 0.001f)
            }
        }

    }


}