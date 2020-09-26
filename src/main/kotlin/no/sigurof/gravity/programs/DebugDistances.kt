package no.sigurof.gravity.programs

import no.sigurof.gravity.utils.operators.minus
import org.joml.Vector3f


fun debugDistances(positions: List<Vector3f>) {
    val distances = mutableListOf<Float>()
    for (i in 0 until positions.size - 2) {
        distances.add((positions[i] - positions[i + 1]).length())
    }
    println("$distances")
}
