package no.sigurof.phyzigur.refactor

typealias IndexPair = Pair<Int, Int>

/**
 * This class lists the combinatoric number of (!)combinations of (!)two numbers (!)without repetition
 *
 * Good explanation of combinatorics: https://www.mathsisfun.com/combinatorics/combinations-permutations.html
 *
 * Mathematically, a (!)combination(!) is a collection of elements where order doesn't matter.
 * Thus (0, 1) and (1, 0) are thought to be the same.
 *
 * Mathematically, a (!)permutation(!) is a collection of elements where order DOES matter.
 * Thus, (0, 1) and (1, 0) are NOT thought to be the same.
 *
 * There are fewer
 * combinations of two out of [1, 2, 3] (they are 12, 13, 23) than
 * permutations of two out of [1, 2, 3] (they are 12, 13, 21, 23, )
 * */
internal class UniqueCombinationsOfTwoUniqueIterator(
    private var max: Int
) : Iterator<IndexPair> {
    private var i: Int = 0
    private var j: Int = 1

    override fun hasNext(): Boolean {
        if (this.i == this.max) {
            return false
        }
        return true
    }

    override fun next(): IndexPair {
        val i = this.i
        val j = this.j

        if (this.i == this.max) {
            throw IllegalStateException("Skulle ikke kunne komme hit")
        }
        if (this.j == this.max) {
            this.i += 1
            this.j = this.i + 1
        } else {
            this.j += 1
        }
        return IndexPair(i, j)
    }
}


internal class UniqueCombinationsOfTwoUniqueUntil(private val max: Int) : Iterable<IndexPair> {
    override fun iterator(): Iterator<IndexPair> {
        return UniqueCombinationsOfTwoUniqueIterator(max - 1)
    }
}

internal fun combinationsOfTwoUniqueUntil(max: Int): Iterator<IndexPair> {
    return UniqueCombinationsOfTwoUniqueIterator(max - 1)
}

internal fun allCombinationsOfTwoUniqueUntil(max: Int): Array<Pair<Int, Int>> {
    val iterator = UniqueCombinationsOfTwoUniqueIterator(max - 1)
    val allCombinations = Array((max - 1) * max / 2) { Pair(0, 0) }
    var i = 0
    for (pair in iterator) {
        allCombinations[i] = pair
        i++
    }
    return allCombinations
}

internal fun combinationsOf(numbers: Set<Int>): List<Pair<Int, Int>> {
    val numbersList = numbers.toList()
    return allCombinationsOfTwoUniqueUntil(numbers.size)
        .map { pair -> Pair(numbersList[pair.first], numbersList[pair.second]) }

}

