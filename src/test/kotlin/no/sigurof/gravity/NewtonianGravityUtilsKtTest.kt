package no.sigurof.gravity

import io.kotlintest.assertSoftly
import io.kotlintest.data.forall
import io.kotlintest.matchers.floats.shouldBeLessThan
import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row
import no.sigurof.grajuny.utils.randomFloatBetween
import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.physics.gravity.newtonian.aSolarSystem
import no.sigurof.gravity.physics.gravity.newtonian.restingTwoBodySystem
import no.sigurof.gravity.physics.gravity.newtonian.totalEnergyOf
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import no.sigurof.gravity.utils.randomVector3f
import org.joml.Vector3f
import kotlin.random.Random

internal class NewtonianGravityUtilsKtTest : StringSpec({
    "aSolarSystem" should {
        val originPos = Vector3f(0f, 0f, 0f)
        val originVel = Vector3f(0f, 0f, 0f)
        val g = 9.81f
        val planets = aSolarSystem(
            g,
            1000f,
            (0 until 20).map { Random.nextDouble(0.5, 30.0).toFloat() }.toTypedArray(),
            (0 until 20).map { Random.nextDouble(0.5, 30.0).toFloat() }.toTypedArray(),
            (0 until 20).map { randomFloatBetween(0f, 0.8f) }.toTypedArray(),
            originPos,
            originVel
        )
        "return a result where" {
            val momentum = momentumOf(planets)
            forall(
                row(momentum.x),
                row(momentum.y),
                row(momentum.z)
            ) { it shouldBe (0f plusOrMinus 0.01f) }
        }
    }
})

internal class NewtonianGravityUtilsKtTest2 : FunSpec({
    context("when we have the following three bodies") {
        val g = Random.nextDouble(0.1, 1000.0).toFloat()
        val ps = (0 until 3).map {
            PointMass(
                Random.nextDouble(0.1, 1000.0).toFloat(),
                randomVector3f(),
                randomVector3f()
            )
        }
        val kineticEnergy = 0.5f * ps.map { it.m * it.v * it.v }.sum()
        val potEnergy = (-g * ps[0].m * ps[1].m / (ps[0].r - ps[1].r).length()
                - g * ps[0].m * ps[2].m / (ps[0].r - ps[2].r).length()
                - g * ps[2].m * ps[1].m / (ps[2].r - ps[1].r).length()
                )
        val energy = kineticEnergy + potEnergy

        test("that the energy function calculates the same energy") {
            totalEnergyOf(ps, g) shouldBe (energy plusOrMinus 0.01f)
        }
    }
    context("when calling restingTwoBodySystem:") {
        val g = Random.nextDouble(0.1, 1000.0).toFloat()
        val (a, b) = restingTwoBodySystem(
            Random.nextDouble(0.1, 1000.0).toFloat(),
            Random.nextDouble(0.1, 1000.0).toFloat(),
            g,
            Random.nextDouble(0.1, 1000.0).toFloat(),
            0f
        )
        val planets = listOf(a, b)
        test("that restingTwoBodySystem returns a result where the total momentum is zero") {
            val momentum = momentumOf(planets)
            assertSoftly {
                forall(
                    row(momentum.x),
                    row(momentum.y),
                    row(momentum.z)
                ) { it shouldBe (0f plusOrMinus 0.01f) }
            }
        }
        test("That the resulting energy is less than zero") {
            totalEnergyOf(planets, g) shouldBeLessThan 0f
        }
    }
})

internal fun momentumOf(planets: List<MassPosVel>): Vector3f {
    val momentums = planets.map { it.m * it.v }
    var total = Vector3f(0f, 0f, 0f)
    for (momentum in momentums) {
        total += momentum
    }
    return total
}
