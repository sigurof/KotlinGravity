package no.sigurof.gravity.simulation.settings


sealed class SimulationSettings

class StepsPerFrame(val dt: Float, val numFrames: Int, val numStepsPerFrame: Int) : SimulationSettings()

class TimeDeltaTime(val dt: Float, val T: Float) : SimulationSettings()
