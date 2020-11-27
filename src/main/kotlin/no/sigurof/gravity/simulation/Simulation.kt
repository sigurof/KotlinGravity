package no.sigurof.gravity.simulation

import no.sigurof.gravity.simulation.integration.Integrator

class Simulation<S>(
    private val integrator: Integrator<S>,
    private val stepsPerFrame: Int,
    private val numFrames: Int
) {

    fun <I> record(transform: (state: S) -> I): List<I> {
        val images = mutableListOf<I>()

        var step = stepsPerFrame
        var frame = 0
        while (frame < numFrames) {
            while (step < stepsPerFrame) {
                integrator.step()
                step += 1
            }
            images.add(transform.invoke(integrator.getState()))
            step = 0
            frame += 1
        }
        return images
    }

    fun getState(): S = integrator.getState()

    fun step() {
        var step =0
        while (step < stepsPerFrame) {
            integrator.step()
            step += 1
        }
    }


}