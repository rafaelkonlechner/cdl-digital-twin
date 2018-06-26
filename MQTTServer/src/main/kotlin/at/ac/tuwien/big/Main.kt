@file:JvmName("MainKt")

package at.ac.tuwien.big

import at.ac.tuwien.big.api.HedgehogController
import at.ac.tuwien.big.api.JobController
import at.ac.tuwien.big.api.WebController

/**
 * Set of hosts required for all services
 */
data class HostConfig(
        val influx: String,
        val mqtt: String,
        val objectTracker: String)

val default = HostConfig("192.168.99.100", "192.168.99.100", "192.168.99.100")
val docker = HostConfig("influx", "mqtt", "object-tracker")

const val simSensor = "Sensor-Simulation"
const val sensor = "Sensor"
const val detectionCamera = "DetectionCamera"
const val pickupCamera = "PickupCamera"
const val simActuator = "Actuator-Simulation"
const val actuator = "Actuator"

fun main(args: Array<String>) {
    Runtime.getRuntime().addShutdownHook(Thread {
        if (HedgehogController.hasStarted()) {
            HedgehogController.stop()
        }
    })
    HedgehogController.start()
    val hosts = if (args.firstOrNull() == "--docker") {
        docker
    } else {
        default

    }
    val sensors = listOf(simSensor, sensor, detectionCamera, pickupCamera)
    val actuators = listOf(simActuator, actuator)
    val objectTracker = ObjectTracker(hosts.objectTracker)
    val influx = TimeSeriesDatabase(hosts.influx)
    val mqtt = MQTT(hosts.mqtt, sensors, actuators)
    val controller = MessageController(mqtt, objectTracker, influx)
    val jobs = JobController()
    val web = WebController(mqtt, controller, jobs, influx)

    StateObserver.stateMachine = StateMachineHedgehog(jobs.getJobs().first().states)

    controller.start()
    web.start()
}
