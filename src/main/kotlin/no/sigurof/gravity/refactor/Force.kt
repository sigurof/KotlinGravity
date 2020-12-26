package no.sigurof.gravity.demo

import org.joml.Vector3f

interface Force<T> {
    fun calculate(state: List<T>): Array<Vector3f>
}