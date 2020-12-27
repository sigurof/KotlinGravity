package no.sigurof.gravity.refactor2

import org.joml.Vector3f

interface ForceVerlet<T> {
    fun calculateVerlet(state: List<T>): Array<Vector3f>
}