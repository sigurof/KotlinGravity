package no.sigurof.gravity

import no.sigurof.grajuny.engine.CoreEngine
import no.sigurof.gravity.demo.PlanetCircleGame

/* Necessary features
- Normalized coordinates to verlet simulation
- Add extra force laws for particle pairs that are within a certain radius of each other.
*/
fun main() {
    CoreEngine.play { window ->
        PlanetCircleGame(window)
    }
}

