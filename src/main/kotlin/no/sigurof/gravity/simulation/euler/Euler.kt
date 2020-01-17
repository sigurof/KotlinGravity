package no.sigurof.gravity.simulation.euler


import no.sigurof.gravity.physics.Model
import no.sigurof.gravity.simulation.numerics.eulerStepRV
import no.sigurof.gravity.simulation.settings.SimulationSettings
import no.sigurof.gravity.simulation.settings.StepsPerFrame
import org.joml.Vector3f

object Euler {
    fun simulationOf(
        model: Model,
        masses: Array<Float>,
        initialPositions: Array<Vector3f>,
        initialVelocities: Array<Vector3f>,
        settings: SimulationSettings
    ): EulerSimulation {
        return when (settings) {
            is StepsPerFrame -> Default(
                model = model,
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

interface EulerSimulation {
    fun <I> iterate(
        transform: (r: List<Vector3f>, v: List<Vector3f>, a: List<Vector3f>, t: Float)-> I
    ): List<I>
}


// TODO Add inline constructor with reified type to get the benefit of preallocation
class Default(
    private val model: Model,
    private val masses: Array<Float>,
    initialPositions: Array<Vector3f>,
    initialVelocities: Array<Vector3f>,
    private val numFrames: Int,
    private val stepsPerFrame: Int,
    private val dt: Float
) : EulerSimulation {

    private val r = initialPositions.copyOf()
    private val v = initialVelocities.copyOf()
    private val a = Array(initialPositions.size) { Vector3f(0f, 0f, 0f) }
    private var t = 0.0f

    override fun <I> iterate(transform: (r: List<Vector3f>, v: List<Vector3f>, a: List<Vector3f>, t: Float) -> I): List<I> {
        model.writeAccelerations(a, r, masses)

        val images = mutableListOf<I>()
        var step = stepsPerFrame
        var frame = 0
        while (frame < numFrames) {
            while (step < stepsPerFrame) {
                iterateBy(::euler)
                step += 1
            }
            images.add(transform.invoke(r.toList(), v.toList(), a.toList(), t))
            step = 0
            frame += 1
        }
        return images
    }

    private inline fun iterateBy(method: (i: Int) -> Unit) {
        for (i in r.indices) {
            method.invoke(i)
        }
        t += dt
        model.writeAccelerations(a, r, masses)
    }


    private fun euler(i: Int) {
        val posVel = eulerStepRV(r[i], v[i], a[i], dt)
        r[i] = posVel.first
        v[i] = posVel.second
    }
}

