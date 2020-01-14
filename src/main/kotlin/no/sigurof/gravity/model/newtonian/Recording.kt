package no.sigurof.gravity.model.newtonian


class Recording<T>(
    val positionImages: List<T>
) {
    companion object {
        internal fun <T> of(
            model: NewtonianModel,
            stepsPerFrame: Int,
            numberOfFrames: Int,
            stateToImage: (bodies: List<MassPosVelAcc>) -> T
        ): Recording<T> {
            val positionImages = mutableListOf<T>()
            for (frame in 0 until numberOfFrames) {
                model.doSteps(stepsPerFrame)
                model.exposingInternalState { bodies ->
                    positionImages.add(stateToImage(bodies))
                }
            }
            return Recording(positionImages)
        }
    }
}