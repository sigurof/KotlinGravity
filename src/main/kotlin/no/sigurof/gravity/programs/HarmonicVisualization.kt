package no.sigurof.gravity.programs

import no.sigurof.grajuny.utils.randomDirection
import no.sigurof.grajuny.utils.randomFloatBetween
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.physics.gravity.newtonian.utils.newtonianForcePairs
import no.sigurof.gravity.physics.hookeslaw.DampedHarmonic
import no.sigurof.gravity.physics.hookeslaw.HarmonicForceLaw
import no.sigurof.gravity.physics.uniformforce.UniformForceLaw
import no.sigurof.gravity.physics.utils.ForcePair
import no.sigurof.gravity.simulation.Simulation
import no.sigurof.gravity.simulation.integration.euler.EulerIntegrator
import no.sigurof.gravity.utils.operators.times
import org.joml.Vector3f

fun triangularPyramid() {

    val stepsPerFrame = 1
    val numberOfFrames = 5000
    val dt = 0.01f
    val originVel = Vector3f(0f, 0f, 0f)
    val equilibriumDistance = 3.75f
    val springConstant = 200f
    val dampingTerm = 2f
    val objects = listOf(
        PointMass(
            1f, randomDirection() * (equilibriumDistance+1f), originVel
        ),
        PointMass(
            1f, randomDirection() * 0f, originVel
        ),
        PointMass(
            1f, randomDirection() * 1f, originVel
        ),
        PointMass(
            1f, randomDirection() * 1f, originVel
        )
    )
    val harmonicPotential = HarmonicForceLaw(
        harmonicOscillation = DampedHarmonic(
            equilibriumDistance, springConstant, dampingTerm
        ), forcePairs = listOf(ForcePair(0, 1), ForcePair(1,2), ForcePair(2,0), ForcePair(0,3), ForcePair(1,3), ForcePair(2,3)).toTypedArray()
    )
    val positions = Simulation(
        integrator = EulerIntegrator(
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            m = objects.map { it.m }.toTypedArray(),
            forceLaws = listOf(harmonicPotential),
            dt = dt
        ),
        stepsPerFrame = stepsPerFrame,
        numFrames = numberOfFrames
    ).iterate {
        it.pos
    }
    visualize(objects, positions)
}


fun harmonic() {
    val stepsPerFrame = 1
    val numberOfFrames = 5000
    val dt = 0.01f
    val originVel = Vector3f(0f, 0f, 0f)
    val equilibriumDistance = 3.75f
    val springConstant = 200f
    val dampingTerm = 2f
    val w = 10
    val h = 10
    val objects = (0 until w * h).map {
        val i = it % w
        val j = (it - i) / w
        PointMass(
            1f, randomDirection() * randomFloatBetween(3.5f, 4f), originVel
        )
    }
    val constForce = UniformForceLaw(Vector3f(0f, -1f, 0f))
    val harmonicPotential = HarmonicForceLaw(
        harmonicOscillation = DampedHarmonic(
            equilibriumDistance, springConstant, dampingTerm
        ), forcePairs = newtonianForcePairs(objects.size)
    )
    val positions = Simulation(
        integrator = EulerIntegrator(
            initialPositions = objects.map { it.r }.toTypedArray(),
            initialVelocities = objects.map { it.v }.toTypedArray(),
            m = objects.map { it.m }.toTypedArray(),
            forceLaws = listOf(harmonicPotential, constForce),
            dt = dt
        ),
        stepsPerFrame = stepsPerFrame,
        numFrames = numberOfFrames
    ).iterate {
        it.pos
    }
    visualize(objects, positions)
}

