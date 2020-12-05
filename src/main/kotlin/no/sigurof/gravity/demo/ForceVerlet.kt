package no.sigurof.gravity.demo

import org.joml.Vector3f

interface ForceVerlet<T> {
    fun calculateVerlet(state: List<T>): Array<Vector3f>
}