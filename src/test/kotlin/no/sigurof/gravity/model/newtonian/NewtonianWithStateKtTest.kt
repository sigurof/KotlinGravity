package no.sigurof.gravity.model.newtonian

import io.kotlintest.IsolationMode
import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import no.sigurof.gravity.numerics.eulerStepR
import no.sigurof.gravity.numerics.eulerStepRV
import no.sigurof.gravity.utils.operators.randomFloatBetween
import org.joml.Vector3f


internal class NewtonianWithStateKtTest : StringSpec() {
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
        val r = Vector3f(rx, ry, rz)
        val v = Vector3f(vx, vy, vz)
        val a = Vector3f(ax, ay, az)

        "euler step of position and velocity" should {
            val (pos, vel) = eulerStepRV(r, v, a, dt)
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

        "euler step of position" should {
            val pos = eulerStepR(r, v, a, dt)
            "calculate next position the same way, as x0 + v0*dt + ½a0*dt²"{
                pos.x.shouldBe(rx + vx * dt + 0.5f * ax * dt * dt plusOrMinus 0.001f)
                pos.y.shouldBe(ry + vy * dt + 0.5f * ay * dt * dt plusOrMinus 0.001f)
                pos.z.shouldBe(rz + vz * dt + 0.5f * az * dt * dt plusOrMinus 0.001f)
            }
        }
    }


}