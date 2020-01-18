package no.sigurof.gravity.physics.gravity.newtonian

import io.kotlintest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import no.sigurof.gravity.utils.maths.combinatorics.IndexPair


internal class NewtonianGravityUtilsKtTest: StringSpec(){

    init {
        "The newtonian force pairs for 5 planets are: (0, 1), (0, 2), (0, 3), (0,4), (1, 2), (1, 3), (1, 4), (2, 3), (2, 4), (3, 4)"{
            val number = 5
            val expected = listOf(
                IndexPair(0, 1),
                IndexPair(0, 2),
                IndexPair(0, 3),
                IndexPair(0, 4),
                IndexPair(1, 2),
                IndexPair(1, 3),
                IndexPair(1, 4),
                IndexPair(2, 3),
                IndexPair(2, 4),
                IndexPair(3, 4)
            )
            for ((index, pair) in newtonianForcePairs(number).withIndex()) {
                pair.shouldBeEqualToIgnoringFields(expected[index])
            }
        }

        "The newtonian force pairs for 4 planets are: (0, 1), (0, 2), (0, 3), (1, 2), (1, 3), (2, 3)"{
            val number = 4
            val expected = listOf(
                IndexPair(0, 1),
                IndexPair(0, 2),
                IndexPair(0, 3),
                IndexPair(1, 2),
                IndexPair(1, 3),
                IndexPair(2, 3)
            )
             newtonianForcePairs(number) shouldBe expected
        }
    }

}