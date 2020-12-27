package no.sigurof.gravity.demo

import no.sigurof.gravity.refactor2.*
import org.joml.Vector3f

class VerletInitialState(
    val pos: Vector3f,
    val vel: Vector3f,
    val m: Float
)

class VerletSimulator(
    initialStates: List<VerletInitialState>,
    val dt: Float,
    val forces: List<ForceVerlet<VerletSingleBody>>,
    private val stepsPerFrame: Int
) {

    private var t = 0f
    private var iterator: () -> Unit = ::eulerIteration
    private var p: MutableList<Array<Vector3f>> = mutableListOf(
        initialStates.map { it.pos }.toTypedArray(),
        Array(initialStates.size) { Vector3f(0f, 0f, 0f) }
    )
    var initialVelocities = initialStates.map { it.vel }.toTypedArray()
    private var a = Array(initialStates.size) { Vector3f(0f, 0f, 0f) }
    var m = initialStates.map { it.m }.toTypedArray()
    private var newPosIndex = 1
    private var lastPosIndex = 0
    private var indexTemp: Int = 0
    private var hasSetVelocity = true


    fun step() {
        prepare()
        for (i in 0 until stepsPerFrame) {
            updateAcceleration()
            updatePosition()
            updateTime()
        }
    }

    private fun prepare() {
        if (hasSetVelocity) {
            iterator = ::eulerIteration
        }
    }


    fun getPositions(): Array<Vector3f> = p[lastPosIndex]


    private fun getState(): List<VerletSingleBody> = m.mapIndexed { i, mass ->
        VerletSingleBody(
            mass,
            p[lastPosIndex][i]
        )
    }

    private fun updateAcceleration() {
        zeroOutAccelerations()
        for (force in forces) {
            addArrays(
                a, force.calculateVerlet(
                    getState()
                )
            )
        }
        divideOnMass()
    }

    private fun divideOnMass() {
        for (i in a.indices) {
            a[i] = a[i] / m[i]
        }
    }

    private fun addArrays(one: Array<Vector3f>, other: Array<Vector3f>) {
        for (i in one.indices) {
            one[i] += other[i]
        }
    }


    private fun zeroOutAccelerations() {
        for (i in a.indices) {
            a[i] = Vector3f(0f, 0f, 0f)
        }
    }

    private fun updatePosition() {
        iterator.invoke()
        posContainerIndexSwitch()
    }

    private fun posContainerIndexSwitch() {
        indexTemp = newPosIndex
        newPosIndex = lastPosIndex
        lastPosIndex = indexTemp
    }

    private fun updateTime() {
        t += dt
    }

    private fun eulerIteration() {
        for (i in a.indices) {
            p[newPosIndex][i] = p[lastPosIndex][i] + deltaPositionEuler(initialVelocities[i], a[i], dt)
        }
        iterator = ::verletIteration
        hasSetVelocity = false
    }

    private fun verletIteration() {
        for (i in a.indices) {
            p[newPosIndex][i] = p[lastPosIndex][i] + deltaPositionVerlet(p[lastPosIndex][i], p[newPosIndex][i], a[i], dt)
        }
    }

    fun setVelocities(velocities: List<Vector3f>) {
        hasSetVelocity = true
        initialVelocities = velocities.toTypedArray()
    }

    fun setPositions(newPositions: List<Vector3f>) {
        p[lastPosIndex] = newPositions.toTypedArray()
    }


}
