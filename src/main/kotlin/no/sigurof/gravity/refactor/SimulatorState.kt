package no.sigurof.gravity.demo

import no.sigurof.gravity.utils.maths.combinatorics.allCombinationsOfTwoUniqueUntil
import org.joml.Vector3f

class LocatedGeometry(
    val pos: Vector3f,
    val geometry: PerfectSphere
) {
    fun intersects(other: LocatedGeometry): Boolean {
        return this.geometry.intersects(other.geometry, this.pos, other.pos)
    }
}

class SimulatorState(
    entities: List<SimulationEntity>,
    dt: Float,
    forces: List<ForceVerlet<VerletSingleBody>>
) {

    val geometries = entities.map {
        it.geometry
    }


    val locatedGeometries: List<LocatedGeometry>
        get() = geometries.zip(simulator.getPositions())
            .map {
                LocatedGeometry(
                    pos = it.second,
                    geometry = it.first
                )
            }

    private val simulator: VerletSimulator = VerletSimulator(
        initialStates = entities.map {
            VerletInitialState(
                pos = it.pos,
                vel = it.vel,
                m = it.m
            )
        },
        dt = dt,
        forces = forces,
        stepsPerFrame = 1
    )

    val dt: Float
        get() = simulator.dt

    val collisionIndexPairs: Array<Pair<Int, Int>> = allCombinationsOfTwoUniqueUntil(entities.size)

    fun simulatorStep() {
        simulator.step()
    }

    fun getCollider(id: Int): GeometryMomentum {
        return GeometryMomentum(
            geometry = geometries[id],
            m = simulator.m[id],
            vel = simulator.getVelocity(id),
            pos = simulator.getPositions()[id]
        )
    }

    fun setVelocity(velocities: List<Vector3f>) {
        simulator.setVelocity(velocities)
    }

    fun setVelocity(id: Int, vel: Vector3f) {
        simulator.setVelocity(id, vel)
    }

    fun getPositions(): List<Vector3f> {
        return simulator.getPositions().copyOf().toList()
    }

    fun getState(): List<MassPos> {
        return simulator.getPositions().zip(simulator.m).map { MassPos(m = it.second, r = it.first) }
    }

    fun setPositions(newPositions: List<Vector3f>) {
        simulator.setPositions(newPositions)
    }

}
