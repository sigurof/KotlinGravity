package no.sigurof.gravity.physics.hookeslaw.utils

import no.sigurof.gravity.physics.utils.ForcePair


fun singleStrandForcePairs(numberOfObjects: Int): Array<ForcePair> {
    return Array(numberOfObjects - 1) { i ->
        ForcePair(i, i + 1)
    }
}

fun ringStrandForcePairs(numberOfObjects: Int): Array<ForcePair> {
    return Array(numberOfObjects) { i ->
        if (i == numberOfObjects - 1) ForcePair(
            i,
            0
        ) else ForcePair(i, i + 1)
    }
}

fun ringStrandForcePairsList(numberOfObjects: Int): MutableList<ForcePair> {
    return MutableList(numberOfObjects) { i ->
        if (i == numberOfObjects - 1) ForcePair(
            i,
            0
        ) else ForcePair(i, i + 1)

    }
}

fun ringStrandOneInMiddle(numberOfObjects: Int): Array<ForcePair> {
    val ring =
        ringStrandForcePairsList(numberOfObjects - 1)
    for (i in 0 until numberOfObjects - 1) {
        ring.add(Pair(numberOfObjects - 1, i))
    }
    return ring.toTypedArray()
}

fun above(index: Int, w: Int): Int? {
    return (index - w).takeIf { it >= 0 }
}

fun twoBelow(index: Int, w: Int, length: Int): Int? {
    return (index + 2*w).takeIf { it < length }
}

fun below(index: Int, w: Int, length: Int): Int? {
    return (index + w).takeIf { it < length }
}

fun right(index: Int, w: Int, length: Int): Int? {
    return (index + 1).takeIf { it % w != 0 && it < length }
}

fun twoRight(index: Int, w: Int, length: Int): Int? {
    return (index + 2).takeIf{ it % w != 0 && it < length }
}

fun left(index: Int, w: Int): Int? {
    return (index - 1).takeIf { it >= 0 && it % w != (w - 1) }
}

fun rectangularMesh(w: Int, h: Int, r: Int): Array<ForcePair> {
    val length = w * h + r
    val forcePairs = mutableListOf<ForcePair>()
    for (i in 0 until length) {
        listOfNotNull(
//            above(i, w),// For å unngå double counting
            below(i, w, length),
            right(i, w, length)
//            left(i, w)// For å unngå double counting
        ).forEach {
            forcePairs.add(ForcePair(i, it))
        }
    }
    return forcePairs.toTypedArray()
}

fun rectangularMeshNearestAndNextNearest(w: Int, h: Int, r: Int): Array<ForcePair> {
    val length = w * h + r
    val forcePairs = mutableListOf<ForcePair>()
    for (i in 0 until length) {
        listOfNotNull(
            below(i, w, length),
            right(i, w, length),
            twoBelow(i, w, length),
            twoRight(i, w, length)
        ).forEach {
            forcePairs.add(ForcePair(i, it))
        }
    }
    return forcePairs.toTypedArray()
}

fun meshCyclicInWidth(w: Int, h: Int, r: Int): Array<ForcePair> {
    val length = w * h + r
    val forcePairs = mutableListOf<ForcePair>()
    for (i in 0 until length) {
        listOfNotNull(
            above(i, w),
            below(i, w, length),
            right(i, w, length) ?: (if (i < w * h) i + 1 - w else null),
            left(i, w)
        ).forEach {
            forcePairs.add(ForcePair(i, it))
        }
    }
    return forcePairs.toTypedArray()
}
