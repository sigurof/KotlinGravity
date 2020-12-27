package no.sigurof.gravity.refactor2

import no.sigurof.gravity.demo.VerletInitialState
import no.sigurof.gravity.demo.VerletSimulator
import org.joml.Vector3f

class VerletIntegrator(
    override val dt: Float,
    entities: List<SimulationEntity>,
    forces: List<ForceVerlet<VerletSingleBody>>
) : Integrator {
    override var radii = entities.map { it.geometry.radius }.toMutableList()
    override var m: List<Float> = entities.map { it.m }

    override fun setVel(index: Int, value: Vector3f) {
        val vmut = v.toMutableList()
        vmut[index] = value
        v = vmut.toList()
        simulator.setVelocities(v)
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
    override var time: Float = 0f

    override fun updateVelocity() {
        p = simulator.getPositions().toMutableList()
        simulator.step()
        v = simulator.getPositions()
            .zip(p)
            .map { (newPos, oldPos) -> ((newPos - oldPos).div(dt, Vector3f())) }
            .toMutableList()
    }

    override var v: List<Vector3f> = simulator.initialVelocities.toList()
    override var p: List<Vector3f> = simulator.getPositions().toList()

    override fun getState(): List<MassPos> {
        return simulator
            .getPositions()
            .zip(simulator.m)
            .map { MassPos(m = it.second, r = it.first) }
    }

    override fun handle(event: Event) {
        val dt = event.time - this.time
        val newPositions = p.zip(v)
            .map { (pos, vel) -> pos + vel * dt }
            .toMutableList()
        simulator.setPositions(newPositions)
        this.time = event.time
        event.handle(this)
    }
}