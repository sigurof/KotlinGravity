package no.sigurof.gravity.physics.experimental

interface Integrator<T> {
    fun step()
    fun updateAcceleration()
    fun getState() : T
}