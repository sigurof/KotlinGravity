package no.sigurof.gravity.physics.gravity.newtonian

import io.kotlintest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import no.sigurof.gravity.physics.gravity.newtonian.utils.newtonianIndexPairs
import no.sigurof.gravity.utils.maths.combinatorics.IndexPair


internal class NewtonianGravityUtilsKtTest : StringSpec() {

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
            for ((index, pair) in newtonianIndexPairs(number).withIndex()) {
                pair.shouldBeEqualToIgnoringFields(expected[index])
            }
        }
        "The newtonian force pairs for a list: [0, 1, 2, 3, 4] are (0, 1), (0, 2), (0, 3), (0,4), (1, 2), (1, 3), (1, 4), (2, 3), (2, 4), (3, 4)"{
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
            val a = newtonianIndexPairs(listOf(0, 1, 2, 3, 4))
            for ((index, pair) in a.withIndex()) {
                pair.shouldBeEqualToIgnoringFields(expected[index])
            }
        }



        "The newtonian force pairs for a list: [0, 3, 2, 6, 7] are 03, 02, 06, 07, 32, 36, 37, 26, 27, 67 "{
            val expected = listOf(
                IndexPair(0, 3),
                IndexPair(0, 2),
                IndexPair(0, 6),
                IndexPair(0, 7),
                IndexPair(3, 2),
                IndexPair(3, 6),
                IndexPair(3, 7),
                IndexPair(2, 6),
                IndexPair(2, 7),
                IndexPair(6, 7)
            )
            val a = newtonianIndexPairs(listOf(0, 3, 2, 6, 7))
            for ((index, pair) in a.withIndex()) {
                pair.shouldBeEqualToIgnoringFields(expected[index])
            }
        }



        "The newtonian force pairs for 4 planets are: (0, 1), (0, 2), (0, 3), (1, 2), (1, 3), (2, 3)"
        {
            val number = 4
            val expected = listOf(
                IndexPair(0, 1),
                IndexPair(0, 2),
                IndexPair(0, 3),
                IndexPair(1, 2),
                IndexPair(1, 3),
                IndexPair(2, 3)
            )
            newtonianIndexPairs(number) shouldBe expected
        }
    }

}