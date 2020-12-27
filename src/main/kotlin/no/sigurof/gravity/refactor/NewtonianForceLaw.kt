package no.sigurof.gravity.refactor2

import org.joml.Vector3f


class NewtonianForceLaw(
    private val g: Float,
    affects: Set<Int>
) : ForceVerlet<VerletSingleBody> {

    private val indexPairs = newtonianIndexPairs(affects)


    override fun calculateVerlet(state: List<VerletSingleBody>): Array<Vector3f> {
        val forces: Array<Vector3f> = Array(state.size) { Vector3f(0f, 0f, 0f) }
        for (indexPair in indexPairs) {
            val i = indexPair.first
            val j = indexPair.second
            val f = forceBetween(
                state[i].r, state[j].r, state[i].m, state[j].m, g
            )
            forces[indexPair.first] += f
            forces[indexPair.second] -= f
        }
        return forces
    }
}