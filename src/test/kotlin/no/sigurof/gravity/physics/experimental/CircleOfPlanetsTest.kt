package no.sigurof.gravity.physics.experimental

import io.kotlintest.matchers.beLessThan
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import no.sigurof.gravity.physics.gravity.newtonian.utils.generateCircleOfPlanets
import no.sigurof.gravity.physics.gravity.newtonian.utils.totalEnergy


internal class CircleOfPlanetsTest : StringSpec() {
    init {
        "The energy of a circle of planets" should {
            "be less than 0" {
                val g = 0.981f
                generateCircleOfPlanets(g = g, numberOfPlanetPairs = 20).totalEnergy(g = g) shouldBe beLessThan(0f)

            }
        }
    }
}
