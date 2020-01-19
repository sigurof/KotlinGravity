package no.sigurof.gravity.physics.experimental

interface Potential {
    fun <T> updateAcc(integrator: Integrator<T>)
}


