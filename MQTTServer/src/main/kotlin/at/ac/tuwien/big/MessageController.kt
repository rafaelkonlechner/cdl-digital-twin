package at.ac.tuwien.big

import at.ac.tuwien.big.entity.SensorLogEntry
import at.ac.tuwien.big.repository.SensorLogEntryRepository
import org.eclipse.paho.client.mqttv3.*
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Controller
import java.time.LocalDateTime
import java.util.*

@Controller
final class MessageController(val webSocket: SimpMessagingTemplate, val repository: SensorLogEntryRepository) : MqttCallback {

    final val qos = 0
    final val sensorTopic = "Sensor"
    final val actuatorTopic = "Actuator"
    final val gateGreenTopic = "GateGreen"
    final val gateRedTopic = "GateRed"
    final val detectionCameraTopic = "DetectionCamera"
    final val pickupCameraTopic = "PickupCamera"
    final val client: MqttClient

    init {
        println("Connecting to MQTT endpoint.")
        client = MqttClient("tcp://localhost:1883", "Controller")
        val connOpts = MqttConnectOptions()
        connOpts.isCleanSession = true
        client.connect(connOpts)
        println("Established connection.")

        println("Subscribing to topic: $sensorTopic")
        client.setCallback(this)
        client.subscribe(sensorTopic)
        client.subscribe(detectionCameraTopic)
        client.subscribe(pickupCameraTopic)
        client.subscribe(gateGreenTopic)
        client.subscribe(gateRedTopic)
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        when (topic) {
            sensorTopic -> {
                repository.save(SensorLogEntry(UUID.randomUUID().toString(), LocalDateTime.now(), String(message!!.payload)))
                sendWebSocketMessageSensor(String(message.payload))
            }
            detectionCameraTopic -> {
                sendWebSocketMessageDetectionCamera(String(message!!.payload))
            }
            pickupCameraTopic -> {
                sendWebSocketMessagePickupCamera(String(message!!.payload))
            }
            gateGreenTopic -> {
                println("($topic) Received < ${String(message!!.payload)} >")
            }
            gateRedTopic -> {
                println("($topic) Received < ${String(message!!.payload)} >")
            }
        }
    }

    override fun connectionLost(cause: Throwable?) {
        throw cause ?: Exception("Connection lost.")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        println("Delivery complete.")
    }

    fun sendMQTTMessageActuator(message: String) {
        val tmp = MqttMessage(message.toByteArray())
        tmp.qos = qos
        println("Sending via MQTT: $message")
        client.publish(actuatorTopic, tmp)
    }

    @MessageMapping("/actuator")
    fun receiveActuatorWebSocketMessage(command: String) {
        println("Actuator command: " + command)
        sendMQTTMessageActuator(command)
    }

    private fun sendWebSocketMessageSensor(message: String) {
        sendWebSocketMessage("/topic/sensor", message)
    }

    private fun sendWebSocketMessagePickupCamera(message: String) {
        sendWebSocketMessage("/topic/pickupCamera", message)
    }

    private fun sendWebSocketMessageDetectionCamera(message: String) {
        sendWebSocketMessage("/topic/detectionCamera", message)
    }

    private fun sendWebSocketMessage(topic: String, message: String) {
        webSocket.send(topic, MessageBuilder.withPayload(message.toByteArray()).build())
    }
}
