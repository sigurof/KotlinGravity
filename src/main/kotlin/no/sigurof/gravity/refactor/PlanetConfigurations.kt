package no.sigurof.gravity.refactor2

import no.sigurof.grajuny.utils.randomFloatBetween
import org.joml.Vector3f
import kotlin.math.*

fun getSunEarthMoon(g: Float): List<MassPosVel> {
    val m1 = 50f
    val m21 = 50f
    val m22 = 5f
    val t1 = 5f
    val t2 = 0.5f
    val baryPos = Vector3f(0f, 0f, 0f)
    val baryVel = Vector3f(0f, 0f, 0f)

    val m2 = m21 + m22
    val (b1, b2) = twoBodySystem(
        baryPos,
        baryVel,
        m1,
        m2,
        g,
        t1,
        0f
    )

    val (b21, b22) = twoBodySystem(
        b2.r,
        b2.v,
        m21,
        m22,
        g,
        t2,
        0f
    )
    return listOf(b1, b21, b22)
}


fun simulateASolarSystem(
    g: Float = 0.981f,
    numObjects: Int = 10,
    mass: Pair<Float, Float> = Pair(1f, 20f),
    time: Pair<Float, Float> = Pair(1f, 20f),
    ecc: Pair<Float, Float> = Pair(0f, 0.8f),
    msun: Float = 200f,
    originPos: Vector3f = Vector3f(0f, 0f, 0f),
    originVel: Vector3f = Vector3f(0f, 0f, 0f)
): List<MassPosVel> {
    return buildASolarSystem(
        g = g,
        msun = msun,
        ms = (0 until numObjects).map { randomFloatBetween(mass.first, mass.second) }.toTypedArray(),
        ts = (0 until numObjects).map { randomFloatBetween(time.first, time.second) }.toTypedArray(),
        es = (0 until numObjects).map { randomFloatBetween(ecc.first, ecc.second) }.toTypedArray(),
        baryPos = originPos,
        baryVel = originVel
    )
}

fun buildASolarSystem(
    g: Float,
    msun: Float,
    ms: Array<Float>,
    ts: Array<Float>,
    es: Array<Float>,
    baryPos: Vector3f,
    baryVel: Vector3f
): List<MassPosVel> {
    val fictSuns = mutableListOf<MassPosVel>()
    val planets = mutableListOf<MassPosVel>()
    val sun =
        PointMass(msun, Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, 0f))
    for (i in ms.indices) {
        val (fictSun, planet) = restingTwoBodySystem(
            g = g,
            m1 = msun,
            m2 = ms[i],
            t = ts[i],
            e = es[i]
        )
        fictSuns.add(fictSun)
        planets.add(planet)
        sun.r += fictSun.r
        sun.v += fictSun.v
    }
    for (planet in planets) {
        planet.r += baryPos
        planet.v += baryVel
    }
    planets.add(sun)
    return planets
}



fun demoSolarSystemWithMoons1(
    g: Float = 0.981f,
    originPos: Vector3f = Vector3f(0f, 0f, 0f),
    originVel: Vector3f = Vector3f(0f, 0f, 0f)
): List<MassPosVel> {
    return buildSunEarthMoonGravityNode().buildPlanets(
        g = g,
        baryPos = originPos,
        baryVel = originVel
    )
}


fun generateCircleOfPlanets(g: Float = 0.981f, numberOfPlanetPairs: Int = 1, radius: Float = 5f): List<MassPosVel> {
    val m = 0.1f;
    val normal = Vector3f(0f, 0f, 1f)
    val positions: List<Vector3f> = (0 until numberOfPlanetPairs)
        .map { i ->
            val angle = i * 2f * PI / numberOfPlanetPairs
            Vector3f(cos(angle).toFloat(), sin(angle).toFloat(), 0f).times(radius)
        }
    val v = sqrt(g * m *
            positions
                .last()
                .let { referencePos ->
                    positions
                        .minus(referencePos)
                        .map { pos -> (referencePos - pos).let { d -> d / d.length().pow(3) } }
                        .reduce { subTotal, next -> subTotal + next }
                        .dot(referencePos)
                }

    )
    return positions
        .map { pos ->
            PointMass(
                m,
                pos,
                normal.cross(pos, Vector3f()).normalized().times(v)
            )
        }

}

fun demoSolarSystemWithMoons2(
    g: Float = 0.981f,
    originPos: Vector3f = Vector3f(0f, 0f, 0f),
    originVel: Vector3f = Vector3f(0f, 0f, 0f)
): List<MassPosVel> {
    return buildRandomGravityNode(
        mass = 1000f,
        orbitalPeriod = 500f,
        eccentricityBetween = Pair(0.0f, 0.5f),
        numPlanets = Pair(2, 5),
        remainingDepth = 2
    ).buildPlanets(
        g = g,
        baryVel = originVel,
        baryPos = originPos
    )
}

