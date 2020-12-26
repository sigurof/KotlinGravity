package no.sigurof.gravity.demo

import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class SimulationEntity(
    val pos: Vector3f,
    val vel: Vector3f,
    val m: Float,
    val geometry: PerfectSphere
)

class SimulationEngine(
    entities: List<SimulationEntity>,
    dt: Float,
    forces: List<ForceVerlet<VerletSingleBody>>,
    private val stepsPerFrame: Int
) {

    val state = SimulatorState(
        entities = entities,
        dt = dt,
        forces = forces
    )

    private fun step() {
        // define a physical situation: m, v, r, forces
        // define the geometry

        val currentPositions = state.getPositions()
        val radii = state.geometries.map { it.radius }
        state.simulatorStep()
        val velocities = state
            .getPositions()
            .zip(currentPositions)
            .map { (it.first - it.second).div(state.dt, Vector3f()) }
        val masses = state.getState().map { it.m }
        var intermediateTime = 0f
        while (intermediateTime < state.dt) {
            val collision = findNextCollision(p = currentPositions, v = velocities, r = radii)
                ?.takeIf {
                    it.t <= (state.dt - intermediateTime)
                }
            if (collision != null) {
                intermediateTime += collision.t
                val newPositions =
                    currentPositions.zip(velocities).map { (pos, vel) -> pos + vel * collision.t }
                val newVelocities = velocities.toMutableList()
                val (v1, v2) = Collisions.Elastic.sphereOnSphere(
                    Collisions.Elastic.Particle(
                        m = masses[collision.i1],
                        r = newPositions[collision.i1],
                        v = velocities[collision.i1]
                    ),
                    Collisions.Elastic.Particle(
                        m = masses[collision.i2],
                        r = newPositions[collision.i2],
                        v = velocities[collision.i2]
                    )
                )
                newVelocities[collision.i1] = v1
                newVelocities[collision.i2] = v2
                state.setPositions(newPositions)
                state.setVelocity(newVelocities)
            } else {
                val positions =
                    currentPositions
                        .zip(velocities)
                        .map { (pos, vel) ->
                            val dt = state.dt - intermediateTime
                            pos + vel * (dt)
                        }
                intermediateTime = state.dt
//                currentPositions.zip(positions)
//                    .forEach { (p1, p2) ->
//                        println(p1.min(p2))
//                    }
                state.setPositions(positions)
            }
        }


        // progress positions by verlet integration
        // evaluate collisions by geometry
        // on collision: modify physical situation and geometry (like calculating elastic collision)
    }

    private fun findNextCollision(
        p: List<Vector3f>,
        v: List<Vector3f>,
        r: List<Float>
    ): Coll? {
        return state.collisionIndexPairs.mapNotNull { (i, j) ->
            findTimeOfCollisionH(
                SphereCollider(
                    pos = p[i],
                    vel = v[i],
                    radius = r[i]
                ),
                SphereCollider(
                    pos = p[j],
                    vel = v[j],
                    radius = r[j]
                )
            )?.let {
                Coll(
                    i1 = i,
                    i2 = j,
                    t = it
                )
            }
        }.minBy { it.t }
    }

    class Coll(
        val t: Float,
        val i1: Int,
        val i2: Int
    )


    fun getNextState(): List<MassPos> {
        for (i in 0 until stepsPerFrame) {
            step()
        }
        return getState()
    }


    private fun getState(): List<MassPos> {
        return state.getState()
    }
}

class MassPos(
    val m: Float,
    val r: Vector3f
)


fun findTimeOfCollisionH(one: SphereCollider, other: SphereCollider): Float? {
    val centerToCenter = one.radius + other.radius
    val otherPosMinusOnePos = other.pos - one.pos
    val otherVelMinusOneVel = other.vel - one.vel
    val c = otherPosMinusOnePos.lengthSquared() - centerToCenter.pow(2)
    val b = 2 * otherPosMinusOnePos.dot(otherVelMinusOneVel)
    val a = otherVelMinusOneVel.lengthSquared()
    val bsquaredMinus4ac = b.pow(2) - 4 * a * c
    if (bsquaredMinus4ac > 0) {
        val t1 = (-b + sqrt(bsquaredMinus4ac)) / 2 / a
        val t2 = (-b - sqrt(bsquaredMinus4ac)) / 2 / a
        if (t1 < 0 && t2 < 0) {
            // Time of collision is negative (i.e. may have collided in the past)
            return null;
        }
        if (t1 > 0 && t2 > 0) {
            // Both positive return the least time.
            return min(t1, t2)
        }
        // One of them is negative. Return the positive one
        return max(t1, t2)
    } else {
        // Time of collision is imaginary (i.e. will never collide)
        return null
    }
}
