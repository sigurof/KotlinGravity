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
import no.sigurof.gravity.model.newtonian.BodyState
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.randomVector3f
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f
import kotlin.random.Random

internal class SolarSystemKtTest : StringSpec({
    "createSunEarthMars" should {
        val g = 9.81f
        val origin = Vector3f(0f, 0f, 0f)
        val planets = getSunEarthMars(g, 1000f, 10f, 1f, 7f, 2f, origin, origin)
        "return a result where" {
            val momentum = momentumOf(planets)
            forall(
                row(momentum.x),
                row(momentum.y),
                row(momentum.z)
            ) { it shouldBe (0f plusOrMinus 0.001f) }
        }
    }
})

internal class SolarSystemKtTest2 : FunSpec({
    context("when we have the following three bodies") {
        val g = Random.nextDouble(0.1, 1000.0).toFloat()
        val ps = (0 until 3).map {
            BodyState(
                Random.nextDouble(0.1, 1000.0).toFloat(),
                randomVector3f(),
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
            calculateEnergy(ps, g) shouldBe (energy plusOrMinus 0.002f)
        }
    }
    context("when calling restingTwoBodySystem:") {
        val g = Random.nextDouble(0.1, 1000.0).toFloat()
        val (a, b) = restingTwoBodySystem(
            Random.nextDouble(0.1, 1000.0).toFloat(),
            Random.nextDouble(0.1, 1000.0).toFloat(),
            g,
            Random.nextDouble(0.1, 1000.0).toFloat()
        )
        val planets = listOf(a, b)
        test("that restingTwoBodySystem returns a result where the total momentum is zero") {
            val momentum = momentumOf(planets)
            assertSoftly {
                forall(
                    row(momentum.x),
                    row(momentum.y),
                    row(momentum.z)
                ) { it shouldBe (0f plusOrMinus 0.001f) }
            }
        }
        test("That the resulting energy is less than zero") {
            calculateEnergy(planets, g) shouldBeLessThan 0f

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
