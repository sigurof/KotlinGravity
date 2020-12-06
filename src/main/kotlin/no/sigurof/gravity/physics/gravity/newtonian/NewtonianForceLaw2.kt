package no.sigurof.gravity.physics.gravity.newtonian

import no.sigurof.gravity.demo.ForceVerlet
import no.sigurof.gravity.demo.VerletSingleBody
import no.sigurof.gravity.physics.gravity.newtonian.utils.forceBetweenn
import no.sigurof.gravity.physics.gravity.newtonian.utils.newtonianIndexPairs
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f


class NewtonianForceLaw2(
    private val g: Float,
    affects: Set<Int>
) : ForceVerlet<VerletSingleBody> {

    private val indexPairs = newtonianIndexPairs(affects)


    override fun calculateVerlet(state: List<VerletSingleBody>): Array<Vector3f> {
        val forces: Array<Vector3f> = Array(state.size) { Vector3f(0f, 0f, 0f) }
        for (indexPair in indexPairs) {
            val i = indexPair.first
            val j = indexPair.second
            val f = forceBetweenn(
                state[i].r, state[j].r, state[i].m, state[j].m, g

            )
            forces[indexPair.first] += f
            forces[indexPair.second] -= f
        }
        return forces
    }
}