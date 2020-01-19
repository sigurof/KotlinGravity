package no.sigurof.gravity.simulation

import no.sigurof.gravity.simulation.integration.Integrator

class Simulation<S>(
    private val integrator: Integrator<S>,
    private val stepsPerFrame: Int,
    private val numFrames: Int
) {

    fun <I> iterate(transform: (state: S) -> I): List<I> {
        val images = mutableListOf<I>()
        integrator.updateAcceleration()

        var step = stepsPerFrame
        var frame = 0
        while (frame < numFrames) {
            while (step < stepsPerFrame) {
                integrator.step()
                integrator.updateAcceleration()
                step += 1
            }
            images.add(transform.invoke(integrator.getState()))
            step = 0
            frame += 1
        }
        return images
    }


}