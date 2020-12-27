package no.sigurof.phyzigur.refactor


class SimulationEngine(
    private val integrator: Integrator,
    private val eventFinder: EventFinder
) {

    private fun step() {
        val stopTime = integrator.time + integrator.dt // delete
        integrator.updateVelocity()
        do {
            val nextEvent = eventFinder.findNextEvent(integrator)
                ?.takeIf { it.time <= stopTime } // takeIf it.time <= integrator.timestepStop
                ?: EndTimestep(stopTime) // EndTimestep(integrator.timestepStop)
            integrator.handle(nextEvent)
        } while (integrator.time < stopTime) // while integrator.isBetweenTimesteps()
    }

    fun getNextState(): List<MassPos> {
        step()
        return getState()
    }

    private fun getState(): List<MassPos> {
        return integrator.getState()
    }
}

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
