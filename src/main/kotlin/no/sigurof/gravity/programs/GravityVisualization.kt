package no.sigurof.gravity.programs

import no.sigurof.gravity.physics.ConservativeForceLaw
import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.gravity.newtonian.utils.simulateASolarSystem
import no.sigurof.gravity.simulation.Simulation
import no.sigurof.gravity.simulation.integration.verlet.VerletIntegrator
import no.sigurof.gravity.utils.operators.minus
import org.joml.Vector3f

class ImagePosVel(
    val pos: List<Vector3f>,
    val vel: List<Vector3f>
)

fun simulateVerletAndVisualize(
    g: Float = 0.981f,
    stepsPerFrame: Int = 5,
    numberOfFrames: Int = 5000,
    dt: Float = 0.005f,
    objects: List<MassPosVel> = simulateASolarSystem(g = g),
    forceLaws: List<ConservativeForceLaw>
) {
    val positions = Simulation(
        integrator = VerletIntegrator(
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            m = objects.map { it.m }.toTypedArray(),
            dt = dt,
            forceLaws = forceLaws
        ),
        stepsPerFrame = stepsPerFrame,
        numFrames = numberOfFrames
    ).record {
        ImagePosVel(it.pos, it.pos.zip(it.posOld).map { p -> (p.first - p.second) / dt })
    }
}

