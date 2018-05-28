package at.ac.tuwien.big.api

import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.InetAddress
import at.ac.tuwien.big.StateMachineSimulation.States as s

object HedgehogController {

    private val ip = InetAddress.getLocalHost().hostAddress
    private val gson = Gson()
    private var process: Int = 0

    fun start() {
        if (hasStarted()) {
            return
        }
        println("Starting Hedgehog")

        val data = "{\"data\":{\"type\":\"process\",\"attributes\":{\"programId\":\"QXJt\",\"fileId\":\"Li9tYWluLnB5\",\"args\":[\"$ip\"]}}}"
        Fuel.post("http://raspberrypi.local/api/processes")
                .header(Pair("content-type", "application/json"),
                        Pair("accept", "application/json, text/plain"))
                .body(data).response { req, resp, result ->
                    if (result.component2() == null) {
                        val json = gson.fromJson(String(resp.data), JsonObject::class.java)
                        process = json["data"].asJsonObject["id"].asInt
                        println("Started Hedgehog PID $process")
                    } else {
                        println("Error: Starting Hedgehog")
                    }
                }
    }

    fun hasStarted() = process != 0

    fun stop() {
        println("Stopping Hedgehog PID $process")
        Fuel.delete("http://raspberrypi.local/api/processes/$process").response { req, resp, result ->
            println("Stopped Hedgehog PID $process")
        }
    }
}
