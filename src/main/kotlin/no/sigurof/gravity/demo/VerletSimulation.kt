package no.sigurof.gravity.demo

import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.simulation.integration.utils.deltaPositionEuler
import no.sigurof.gravity.simulation.integration.utils.deltaPositionVerlet
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f

class VerletSingleBody(
    var m: Float,
    var r: Vector3f,
    var a: Vector3f
)

class VerletInternal(

)

class VerletSimulation(
    initialStates: List<MassPosVel>,
    val dt: Float,
    val forces: List<ForceVerlet<VerletSingleBody>>,
    private var applyConstraints: (lastPosition: Vector3f, defaultNewPosition: Vector3f) -> Vector3f = { lastPos: Vector3f, defaultNewPos: Vector3f -> defaultNewPos },
    private val stepsPerFrame: Int
) {

    var t = 0f
    private var iterator: () -> Unit = ::firstIteration
    private val p: List<Array<Vector3f>> = listOf(
        initialStates.map { it.r }.toTypedArray(),
        Array(initialStates.size) { Vector3f(0f, 0f, 0f) }
    )
    private val initialVelocities = initialStates.map { it.v }.toTypedArray()
    private val a = Array(initialStates.size) { Vector3f(0f, 0f, 0f) }
    private val m = initialStates.map { it.m }.toTypedArray()
    private var newPosIndex = 1
    private var lastPosIndex = 0
    private var indexTemp: Int = 0


    fun step() {
        var step = 0
        while (step < stepsPerFrame) {
            updateAcceleration()
            updatePosition()
            updateTime()
            step += 1
        }
    }

    fun getState(): List<VerletSingleBody> = m.mapIndexed { i, mass ->
        VerletSingleBody(
            mass,
            p[lastPosIndex][i],
            a[i]
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

    private fun firstIteration() {
        for (i in a.indices) {
            p[newPosIndex][i] = p[lastPosIndex][i] + deltaPositionEuler(initialVelocities[i], a[i], dt)
        }
        iterator = ::iteration
    }

    private fun iteration() {
        for (i in a.indices) {
            val defaultNewPos =
                p[lastPosIndex][i] + deltaPositionVerlet(p[lastPosIndex][i], p[newPosIndex][i], a[i], dt)
            p[newPosIndex][i] = applyConstraints(p[lastPosIndex][i], defaultNewPos)

        }
    }

}