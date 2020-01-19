package no.sigurof.gravity.physics.hookeslaw

import io.kotlintest.assertSoftly
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec


internal class HookesLawUtilsKtTest : FunSpec() {
    init {
        context("Given some indices on an array with a certain width and height") {
            val indices = listOf(0, 2, 4, 10, 12)
            val width = 3
            val height = 4
            val rest = 1
            val length = width * height + rest
            val expectedAbove = listOf(null, null, 1, 7, 9)
            val expectedBelow = listOf(3, 5, 7, null, null)
            val expectedRight = listOf(1, null, 5, 11, null)
            val expectedLeft = listOf(null, 1, 3, 9, null)
            test("that the calculated indices above, right, below and left are correct") {
                indices.indices.toList().forAll {
                    assertSoftly {
                        above(indices[it], width) shouldBe expectedAbove[it]
                        below(indices[it], width, length) shouldBe expectedBelow[it]
                        right(indices[it], width, length) shouldBe expectedRight[it]
                        left(indices[it], width) shouldBe expectedLeft[it]
                    }
                }
            }
        }
    }

}