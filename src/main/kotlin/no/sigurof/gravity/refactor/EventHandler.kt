package no.sigurof.gravity.demo


object EventHandler {

    fun handle(it: Event, state: SimulatorState) {
        when (it) {
            is Collision -> CollisionHandler.handle(it, state)
            is Deletion -> carryOut(it, state)
        }
    }

    private fun carryOut(it: Deletion, state: SimulatorState) {

    }


}