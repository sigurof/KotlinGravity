package no.sigurof.gravity.physics.hookeslaw

import no.sigurof.gravity.physics.ForcePair


fun singleStrandForcePairs(numberOfObjects: Int): Array<ForcePair> {
    return Array(numberOfObjects - 1) { i ->
        ForcePair(i, i + 1)
    }
}

fun ringStrandForcePairs(numberOfObjects: Int): Array<ForcePair> {
    return Array(numberOfObjects) { i ->
        if (i == numberOfObjects - 1) ForcePair(i, 0) else ForcePair(i, i + 1)
    }
}

fun ringStrandForcePairsList(numberOfObjects: Int): MutableList<ForcePair> {
    return MutableList(numberOfObjects) { i ->
        if (i == numberOfObjects - 1) ForcePair(i, 0) else ForcePair(i, i + 1)

    }
}

fun ringStrandOneInMiddle(numberOfObjects: Int): Array<ForcePair> {
    val ring = ringStrandForcePairsList(numberOfObjects - 1)
    for (i in 0 until numberOfObjects - 1) {
        ring.add(Pair(numberOfObjects - 1, i))
    }
    return ring.toTypedArray()
}
