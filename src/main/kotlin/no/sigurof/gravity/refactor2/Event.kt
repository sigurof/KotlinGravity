package no.sigurof.gravity.refactor2

import no.sigurof.gravity.demo.Collisions

interface Event {
    val time: Float

    fun handle(integrator: Integrator)
}

class EndTimestep(override val time: Float) : Event {
    override fun handle(integrator: Integrator) {

    }
}

class SphereSphereCollision(override val time: Float, val i1: Int, val i2: Int) : Event {

    override fun handle(integrator: Integrator) {

        val (v1, v2) = Collisions.Elastic.sphereOnSphere(
            Collisions.Elastic.Particle(
                m = integrator.m[i1],
                r = integrator.p[i1],
                v = integrator.v[i1]
            ),
            Collisions.Elastic.Particle(
                m = integrator.m[i2],
                r = integrator.p[i2],
                v = integrator.v[i2]
            )
        )
        integrator.setVel(i1, v1)
        integrator.setVel(i2, v2)
    }

}
