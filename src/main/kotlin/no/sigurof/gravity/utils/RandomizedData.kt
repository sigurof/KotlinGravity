package no.sigurof.gravity.utils

import org.joml.Vector3f
import kotlin.math.PI
import kotlin.random.Random


fun randomDirection(): Vector3f {
    return randomVector3f().normalize()
}

fun randomVector3f(): Vector3f {
    return Vector3f(
        Random.nextFloat() * 2f - 1f,
        Random.nextFloat() * 2f - 1f,
        Random.nextFloat() * 2f - 1f
    )
}

fun randomFloatBetween(min: Double, max: Double): Float {
    return Random.nextDouble(min, max).toFloat()
}


fun randomFloatBetween(min: Float, max: Float): Float {
    return Random.nextDouble(min.toDouble(), max.toDouble()).toFloat()
}

fun randomAngle(): Float {
    val between0And1: Float = Random.nextFloat()
    return between0And1 * 2f * PI.toFloat()
}
