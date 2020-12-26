package no.sigurof.gravity.demo

import org.joml.Vector3f

class ForceField<T>(
    private val param: (T) -> Vector3f,
    val affects: List<Int>
) : Force<T>, ForceVerlet<T> {

    override fun calculate(state: List<T>): Array<Vector3f> {
        return affects.map { i -> param.invoke(state[i]) }.toTypedArray()
    }

    override fun calculateVerlet(state: List<T>): Array<Vector3f> {
        return affects.map { i -> param.invoke(state[i]) }.toTypedArray()
    }


}