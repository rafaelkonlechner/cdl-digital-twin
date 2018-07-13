package at.ac.tuwien.big.api

import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.InetAddress

/**
 * Start and stop Hedgehog programs via its REST API
 */
object HedgehogController {

    private val ip = InetAddress.getLocalHost().hostAddress
    private val gson = Gson()
    private var process: Int = 0

    /**
     * Start Hedgehog control program 'Arm/main.py', if it has not yet started
     */
    fun start() {
        if (hasStarted()) {
            return
        }
        println("Starting Hedgehog")

        val data = "{\"data\":{\"type\":\"process\",\"attributes\":{\"programId\":\"QXJt\",\"fileId\":\"Li9tYWluLnB5\",\"args\":[\"$ip\"]}}}"
        Fuel.post("http://raspberrypi.local/api/processes")
                .header(Pair("content-type", "application/json"),
                        Pair("accept", "application/json, text/plain"))
                .body(data).response { _, resp, result ->
                    if (result.component2() == null) {
                        val json = gson.fromJson(String(resp.data), JsonObject::class.java)
                        process = json["data"].asJsonObject["id"].asInt
                        println("Started Hedgehog PID $process")
                    } else {
                        println("Error: Starting Hedgehog")
                    }
                }
    }

    /**
     * Indicate, whether the Hedgehog program has been started.
     */
    fun hasStarted() = process != 0

    /**
     * Stop the Hedgehog program
     */
    fun stop() {
        println("Stopping Hedgehog PID $process")
        Fuel.delete("http://raspberrypi.local/api/processes/$process").response { _, _, _ ->
            println("Stopped Hedgehog PID $process")
        }
    }
}
