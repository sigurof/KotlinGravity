package no.sigurof.phyzigur.refactor

import org.joml.Vector3f

interface ForceVerlet<T> {
    fun calculateVerlet(state: List<T>): Array<Vector3f>
}