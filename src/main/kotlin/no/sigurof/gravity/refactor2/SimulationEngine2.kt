package no.sigurof.gravity.refactor2


import no.sigurof.gravity.demo.*
import no.sigurof.gravity.utils.operators.minus
import no.sigurof.gravity.utils.operators.plus
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f

class SimulationEntity2(
    val pos: Vector3f,
    val vel: Vector3f,
    val m: Float,
    val geometry: PerfectSphere
)

interface Event {
    val time: Float

    fun handle(integrator: Integrator)
}

class EndTimestep(override val time: Float) : Event {
    override fun handle(integrator: Integrator) {}
}

//class SphereSphereCollision(override  val time: Float) : Event{
//
//    override fun handle(integrator: Integrator) {
//
//        val (v1, v2) = Collisions.Elastic.sphereOnSphere(
//            Collisions.Elastic.Particle(
//                m = integrator.masses[collision.i1],
//                r = newPositions[collision.i1],
//                v = velocities[collision.i1]
//            ),
//            Collisions.Elastic.Particle(
//                m = masses[collision.i2],
//                r = newPositions[collision.i2],
//                v = velocities[collision.i2]
//            )
//        )
//
//    }
//
//}

interface Integrator {
    val time: Float
    val dt: Float

    fun findV()
    fun handle(event: Event)
    fun getState(): List<MassPos2>
}

class VerletIntegrator(
    override val dt: Float,
    entities: List<SimulationEntity2>,
    forces: List<ForceVerlet<VerletSingleBody>>
) : Integrator {
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
    override var time: Float = 0f

    override fun findV() {
        p = simulator.getPositions().toMutableList()
        simulator.step()
        v = simulator.getPositions()
            .zip(p!!)
            .map { (newPos, oldPos) -> ((newPos - oldPos).div(dt, Vector3f())) }
            .toMutableList()
    }

    private var v: MutableList<Vector3f>? = null
    private var p: MutableList<Vector3f>? = null

    override fun getState(): List<MassPos2> {
        return simulator
            .getPositions()
            .zip(simulator.m)
            .map { MassPos2(m = it.second, r = it.first) }
    }

    override fun handle(event: Event) {
        val dt = event.time - this.time
        val newPositions = p!!.zip(v!!)
            .map { (pos, vel) -> pos + vel * dt }
            .toMutableList()
        simulator.setPositions(newPositions)
        this.time = event.time
        // handle event here
        event.handle(this)
    }
}


class SimulationEngine2(
    private val integrator: Integrator
) {

    private fun step() {
        var i = 0
        val stopTime = integrator.time + integrator.dt // delete
        integrator.findV()
        do {
            i++
            val nextEvent = findNextEvent(integrator)
                ?.takeIf { it.time <= stopTime } // takeIf integrator.time + it.time <= integrator.timestepStop
                ?: EndTimestep(stopTime) // EndTimestep(integrator.timestepStop)
            integrator.handle(nextEvent)// kan kombineres med ↑
            println("$i    integrator.time = ${integrator.time}, stopTime = ${stopTime}")
        } while (integrator.time < stopTime) // while integrator.isBetweenTimesteps()
    }

    private fun findNextEvent(integrator: Integrator): Event? {
        return null
    }

    fun getNextState(): List<MassPos2> {
        step()
        return getState()
    }

    private fun getState(): List<MassPos2> {
        return integrator.getState()
    }
}

class MassPos2(
    val m: Float,
    val r: Vector3f
)

/*
* state[v = findV()]
* while next event not is past end of current timestep
*   next event = findNextEvent(objects(p, v, omega, geometry), dt)
*       findNextEvent[find potential events. Return the one with least time]
*           custom event finding code which can be composed of several different codes.
*           if (state.t > 10s) add(AddImpulseToAll(10f))
*           add(findAllCollisions()) (impulseTransferSphereSphere or Merge(a, b))
*           for (particle in state) if(particle.pos.length > 10.000) add(deletion(particle))
*
*   state[step to event time]
*   modify state according to what event was found (with event handler)
*       impulseTransferSpheresphere - modify velocities of those two according to them being spheres (regardles of whether they are or not)
*       addImpulseToAll - all particles receive extra speed
*       merge - merge a and b into one. Shortens velocity, position, geometry lists.
* */
