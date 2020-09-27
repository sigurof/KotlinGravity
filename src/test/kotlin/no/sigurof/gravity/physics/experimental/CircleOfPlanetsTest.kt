package no.sigurof.gravity.physics.experimental

import io.kotlintest.matchers.beLessThan
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import no.sigurof.gravity.physics.gravity.newtonian.utils.totalEnergy
import no.sigurof.gravity.programs.circleOfPlanets


internal class CircleOfPlanetsTest : StringSpec() {
    init {
        "The energy of a circle of planets" should {
            "be less than 0" {
                val g = 0.981f
                circleOfPlanets(g = g, numberOfPlanetPairs = 20).totalEnergy(g = g) shouldBe beLessThan(0f)

            }
        }
    }
}
