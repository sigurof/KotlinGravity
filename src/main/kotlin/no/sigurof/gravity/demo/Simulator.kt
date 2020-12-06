package no.sigurof.gravity.demo

import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.utils.maths.combinatorics.allCombinationsOfTwoUniqueUntil
import no.sigurof.gravity.utils.operators.minus
import org.joml.Vector3f


sealed class SimObj

data class DynObj(
    val physicalParams: MassPosVel,
    val shape: Shape?
) : SimObj()

class StatObj(
    val pos: Vector3f,
    val shape: Shape
) : SimObj()


sealed class Entity(
    open var pos: Vector3f
)

class Geometry(override var pos: Vector3f, private val shape: Shape) : Entity(pos) {
    fun collides(other: Geometry): Collision.Builder? {
        val distance = (other.pos - this.pos).length()
        return this.shape.collides(shape, distance)
    }
}

class Point(override var pos: Vector3f) : Entity(pos)

class Simulator(
    dynamicObjects: List<DynObj>,
    staticObjects: List<StatObj>,
    dt: Float,
    forces: List<ForceVerlet<VerletSingleBody>>,
    private val stepsPerFrame: Int
) {

    private val dynamicObjectMasses = dynamicObjects.map { it.physicalParams.m }
    private val entitiesNotInIntegration: List<Geometry> =
        staticObjects.map { Geometry(it.pos, it.shape) }
    private val entitiesInIntegration: List<Entity> = dynamicObjects.map { obj ->
        obj.shape?.let {
            Geometry(
                obj.physicalParams.r,
                it
            )
        } ?: Point(
            obj.physicalParams.r
        )
    }
    private val collidableEntities = entitiesInIntegration.filterIsInstance<Geometry>().plus(entitiesNotInIntegration)
    private val indexPairsOfCollidables = allCombinationsOfTwoUniqueUntil(collidableEntities.size)


    private val verletSimulator: VerletSimulator = VerletSimulator(
        initialStates = dynamicObjects,
        dt = dt,
        forces = forces,
        stepsPerFrame = 1
    )

    private fun step() {
        // define a physical situation: m, v, r, forces
        // define the geometry
        updatePositions(verletSimulator.getNextPositions())
        findCollisions()
            .forEach { println("Collision between ${it.idOne} and ${it.idOther}") }


        // progress positions by verlet integration
        // evaluate collisions by geometry
        // on collision: modify physical situation and geometry (like calculating elastic collision)
    }

    private fun findCollisions(): List<Collision> {
        return indexPairsOfCollidables.mapNotNull {
            val i = it.first
            val j = it.second
            collidableEntities[i]
                .collides(collidableEntities[j])
                ?.apply { idOne = i }
                ?.apply { idOther = j }
                ?.build()
        }
    }

    private fun updatePositions(nextPositions: Array<Vector3f>) {
        nextPositions.forEachIndexed { index, it -> entitiesInIntegration[index].pos = it }
    }


    fun getNextStateOfDynamicObjects(): List<MassPos> {
        for (i in 0 until stepsPerFrame) {
            step()
        }
        return getStateOfDynamicObjects()
    }

    private fun getStateOfDynamicObjects(): List<MassPos> {
        return entitiesInIntegration.mapIndexed { index, it ->
            MassPos(
                m = dynamicObjectMasses[index],
                r = it.pos
            )
        }
    }


}

class MassPos(
    val m: Float,
    val r: Vector3f
)
