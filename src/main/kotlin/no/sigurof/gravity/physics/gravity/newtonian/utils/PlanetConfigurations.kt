package no.sigurof.gravity.physics.gravity.newtonian.utils

import no.sigurof.gravity.physics.data.MassPosVel
import no.sigurof.gravity.physics.data.PointMass
import no.sigurof.gravity.utils.operators.plus
import org.joml.Vector3f

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
