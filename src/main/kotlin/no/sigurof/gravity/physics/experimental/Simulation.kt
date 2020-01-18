package no.sigurof.gravity.physics.experimental

class Simulation<T>(
    private val integrator: Integrator<T>,
    private val stepsPerFrame: Int,
    private val numFrames: Int
) {

    fun <I> iterate(transform: (integrator: T) -> I): List<I> {
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