package no.sigurof.gravity.refactor2

import no.sigurof.gravity.demo.PerfectSphere
import org.joml.Vector3f

class SimulationEntity(
    val pos: Vector3f,
    val vel: Vector3f,
    val m: Float,
    val geometry: PerfectSphere
)