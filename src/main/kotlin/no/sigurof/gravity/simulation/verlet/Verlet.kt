package no.sigurof.gravity.simulation.verlet

import no.sigurof.gravity.physics.ConservativeForceModel
import no.sigurof.gravity.physics.ForcePair
import no.sigurof.gravity.simulation.numerics.eulerStepR
import no.sigurof.gravity.simulation.numerics.verletStepR
import no.sigurof.gravity.simulation.settings.SimulationSettings
import no.sigurof.gravity.simulation.settings.StepsPerFrame
import org.joml.Vector3f

object Verlet {
    fun simulationOf(
        model: ConservativeForceModel,
        forcePairs: Array<ForcePair>,
        masses: Array<Float>,
        initialPositions: Array<Vector3f>,
        initialVelocities: Array<Vector3f>,
        settings: SimulationSettings
    ): VerletSimulation {
        return when (settings) {
            is StepsPerFrame -> Default(
                conservativeForceModel = model,
                forcePairs = forcePairs,
                masses = masses,
                initialPositions = initialPositions,
                initialVelocities = initialVelocities,
                numFrames = settings.numFrames,
                stepsPerFrame = settings.numStepsPerFrame,
                dt = settings.dt
            )
            else -> TODO("Hasn't been implemented for time and delta time yet")
        }
    }
}


interface VerletSimulation {
    fun <I> iterate(
        transform: (r: List<Vector3f>, a: List<Vector3f>, t: Float) -> I
    ): List<I>
}


// TODO Add inline constructor with reified type to get the benefit of preallocation
class Default(
    private val conservativeForceModel: ConservativeForceModel,
    private val forcePairs: Array<ForcePair>,
    private val masses: Array<Float>,
    initialPositions: Array<Vector3f>,
    initialVelocities: Array<Vector3f>,
    private val numFrames: Int,
    private val stepsPerFrame: Int,
    private val dt: Float
) : VerletSimulation {

    private var newPosIndex = 1
    private var lastPosIndex = 0
    private val r: List<Array<Vector3f>> = listOf(
        initialPositions.copyOf(),
        Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    )
    private val initialVelocities = initialVelocities.copyOf()
    private val a = Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    private var t = 0.0f
    private var temp: Int = 0

    override fun <I> iterate(transform: (r: List<Vector3f>, a: List<Vector3f>, t: Float) -> I): List<I> {
        val images = mutableListOf<I>()

        updateAcceleration()

        images.add(transform.invoke(r[lastPosIndex].toList(), a.toList(), t))

        iterateBy(::euler)
        var step = 1
        var frame = 1
        while (frame < numFrames) {
            while (step < stepsPerFrame) {
                iterateBy(::verlet)
                step += 1
            }
            images.add(transform.invoke(r[lastPosIndex].toList(), a.toList(), t))
            step = 0
            frame += 1
        }
        return images
    }

    private inline fun iterateBy(method: (i: Int) -> Unit) {
        for (i in r[0].indices) {
            method.invoke(i)
        }
        temp = newPosIndex
        newPosIndex = lastPosIndex
        lastPosIndex = temp
        t += dt
        updateAcceleration()
    }


    private fun updateAcceleration() {
        for (i in a.indices) {
            a[i] = Vector3f(0f, 0f, 0f)
        }
        conservativeForceModel.addAccelerationContribution(a, r[lastPosIndex], masses, forcePairs)
    }

    private fun verlet(i: Int) {
        r[newPosIndex][i] = verletStepR(
            r[lastPosIndex][i],
            r[newPosIndex][i],
            a[i],
            dt
        )
    }

    private fun euler(i: Int) {
        r[newPosIndex][i] = eulerStepR(
            r[lastPosIndex][i],
            initialVelocities[i],
            a[i],
            dt
        )
    }
}

