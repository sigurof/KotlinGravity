package no.sigurof.phyzigur.refactor

import org.joml.Vector3f

fun deltaPositionVerlet(lastPosition: Vector3f, positionBeforeLast: Vector3f, lastAcceleration: Vector3f, dt: Float): Vector3f {
    return lastPosition - positionBeforeLast + lastAcceleration * (dt * dt)
}

fun deltaPositionEuler(lastVelocity: Vector3f, lastAcceleration: Vector3f, dt: Float): Vector3f {
    return lastVelocity * dt + 0.5f * lastAcceleration * (dt * dt) // 6
}

