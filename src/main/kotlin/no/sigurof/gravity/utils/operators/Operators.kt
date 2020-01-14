package no.sigurof.gravity.utils.operators

import org.joml.Vector3f

operator fun Vector3f.minus(vector3f: Vector3f): Vector3f {
    return this.sub(vector3f, Vector3f())
}

operator fun Vector3f.plus(vector3f: Vector3f): Vector3f {
    return this.add(vector3f, Vector3f())
}

operator fun Vector3f.unaryMinus(): Vector3f {
    return this.negate(Vector3f())
}

operator fun Vector3f.unaryPlus(): Vector3f {
    return this
}

operator fun Vector3f.times(vector3f: Vector3f): Float {
    return this.dot(vector3f)
}

operator fun Float.times(vector3f: Vector3f?): Vector3f {
    return vector3f?.mul(this@times, Vector3f()) ?: error("Cannot multiply Float by Vector3f which is null")
}

operator fun Vector3f.times(float: Float?): Vector3f {
    return float?.let { this.mul(it, Vector3f()) } ?: error("Cannot multiply Vector3f by Float which is null")
}

fun Vector3f.normalized(): Vector3f {
    return this.normalize(Vector3f())
}


