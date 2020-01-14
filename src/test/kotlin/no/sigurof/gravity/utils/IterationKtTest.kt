package no.sigurof.gravity.utils

import io.kotlintest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotlintest.specs.StringSpec

internal class NewtonianModelKtTest : StringSpec({
    "The unique index pairs up to 2 are (0, 1), (0, 2), (1, 2)"{
        val expected = listOf(IndexPair(0, 1), IndexPair(0, 2), IndexPair(1, 2))
        for ((index, pair) in combinationsOfTwoUniqueUntil(2).withIndex()) {
            pair.shouldBeEqualToIgnoringFields(expected[index])
        }
    }
})


