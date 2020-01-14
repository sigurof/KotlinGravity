package no.sigurof.gravity.model.newtonian

import org.joml.Vector3f

class NewtonianRecording private constructor(
    internal val positionImages: List<List<Vector3f>>
)  {
    companion object {
        internal fun of(model: NewtonianModel, stepsPerFrame: Int, numberOfFrames: Int): NewtonianRecording {
            val positionImages = mutableListOf<List<Vector3f>>()
            for (frame in 0 until numberOfFrames) {
                model.doSteps(stepsPerFrame)
                model.exposingInternalState { bodies ->
                    positionImages.add(bodies.map { body -> body.r })
                }
            }
            return NewtonianRecording(positionImages)
        }
    }
}