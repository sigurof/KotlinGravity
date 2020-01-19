package no.sigurof.gravity.physics.experimental

class Simulation<T>(
    private val integrator: Integrator<T>,
    private val stepsPerFrame: Int,
    private val numFrames: Int,
    private val potential: Potential
) {

    fun <I> iterate(transform: (integrator: T) -> I): List<I> {
        val images = mutableListOf<I>()
        updateAcceleration()

        var step = stepsPerFrame
        var frame = 0
        while (frame < numFrames) {
            while (step < stepsPerFrame) {
                integrator.step()
                updateAcceleration()
                step += 1
            }
            images.add(transform.invoke(integrator.getState()))
            step = 0
            frame += 1
        }
        return images
    }

    private fun updateAcceleration(){
        integrator.zeroOutAcceleration()
        potential.updateAcc(integrator)
    }

}