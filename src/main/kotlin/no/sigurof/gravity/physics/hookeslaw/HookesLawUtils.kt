package no.sigurof.gravity.physics.hookeslaw

import no.sigurof.gravity.physics.ForcePair


fun singleStrandForcePairs(numberOfObjects: Int): Array<ForcePair> {
    return Array(numberOfObjects) { i ->
        if (i == numberOfObjects - 1) ForcePair(i, 0) else ForcePair(i, i + 1)

    }
}