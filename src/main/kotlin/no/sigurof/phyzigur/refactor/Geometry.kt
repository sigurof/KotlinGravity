package no.sigurof.phyzigur.demo

import org.joml.Vector3f


sealed class Geometry(var center: Vector3f)

class PerfectSphere(var radius: Float, center: Vector3f) : Geometry(center = center)

