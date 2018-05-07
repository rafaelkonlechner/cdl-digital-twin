@file:JvmName("MainKt")

package at.ac.tuwien.big

import at.ac.tuwien.big.rest.WebServiceController

fun main(args: Array<String>) {

    data class HostConfig(
            val influx: String,
            val mqtt: String,
            val objectTracker: String)

    val default = HostConfig("127.0.0.1", "localhost", "localhost")
    val docker = HostConfig("influx", "mqtt", "object-tracker")

    val hosts = if (args.firstOrNull() == "--docker") {
        println("docker")
        docker
    } else {
        println("default")
        default
    }

    val objectTracker = CameraSignal(hosts.objectTracker)
    val influx = TimeSeriesCollectionService(hosts.influx)
    val mqtt = MQTT(hosts.mqtt)
    val controller = MessageController(mqtt, objectTracker, influx)
    val web = WebServiceController(mqtt, controller, influx)
    controller.start()
    web.start()
    readLine()
}
