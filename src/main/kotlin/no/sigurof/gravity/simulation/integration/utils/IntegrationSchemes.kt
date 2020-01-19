package no.sigurof.gravity.simulation.integration.utils

import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f


fun verletStepR(lastPosition: Vector3f, positionBeforeLast: Vector3f, lastAcceleration: Vector3f, dt: Float): Vector3f {
    return lastPosition * 2f - positionBeforeLast + lastAcceleration * dt * dt
}

fun eulerStepR(lastPosition: Vector3f, lastVelocity: Vector3f, lastAcceleration: Vector3f, dt: Float): Vector3f {
    return lastPosition + lastVelocity * dt + 0.5f * lastAcceleration * dt * dt // 6
}

fun eulerStepRV(
    lastPosition: Vector3f,
    lastVelocity: Vector3f,
    lastAcceleration: Vector3f,
    dt: Float
): Pair<Vector3f, Vector3f> {
    val newVelocity = lastVelocity + lastAcceleration * dt // 2
    return Pair(lastPosition + 0.5f * (newVelocity + lastVelocity) * dt, newVelocity) // 4
}

