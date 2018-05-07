package at.ac.tuwien.big

import at.ac.tuwien.big.entity.transition.Transition
import org.eclipse.paho.client.mqttv3.*

class MQTT(val host: String) : MqttCallback {

    data class Subscription(val topics: List<String>, val callback: (String, String) -> Unit)

    val sensorTopic = "Sensor-Simulation"
    val actuatorTopic = "Actuator-Simulation"
    val sensorTopicHedgehog = "Sensor"
    val actuatorTopicHedgehog = "Actuator"
    val detectionCameraTopic = "DetectionCamera"
    val pickupCameraTopic = "PickupCamera"

    private val qos = 0

    private val client = MqttClient("tcp://$host:1883", "Controller")
    private val subscribers = mutableListOf<Subscription>()
    private val lock = Any()

    init {
        println("Connecting to MQTT endpoint.")
        val connOpts = MqttConnectOptions()
        connOpts.isCleanSession = true
        client.connect(connOpts)
        println("Established connection.")

        println("Subscribing to topic: $sensorTopic")
        client.setCallback(this)
        client.subscribe(sensorTopic)
        client.subscribe(detectionCameraTopic)
        client.subscribe(pickupCameraTopic)
    }

    fun cleanup() {
        client.disconnect()
        client.close()
    }

    fun subscribe(topics: List<String>, callback: (String, String) -> Unit) = subscribers.add(Subscription(topics, callback))

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        subscribers.forEach { if (topic in it.topics) it.callback(topic!!, message.toString()) }
    }

    override fun connectionLost(cause: Throwable?) {
        throw cause ?: Exception("Connection lost.")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        println("Delivery complete.")
    }

    fun send(topic: String, message: String) {
        val tmp = MqttMessage(message.toByteArray())
        tmp.qos = qos
        println("Sending via MQTT: $message")
        client.publish(topic, tmp)
    }

    fun sendMQTTTransitionCommand(transition: Transition?) {
        if (transition != null) {
            val commands = StateMachine.transform(transition)
            for (c in commands) {
                sendMQTTDirectCommand(c)
            }
        }
    }

    private fun sendMQTTDirectCommand(message: String) {
        val tmp = MqttMessage(message.toByteArray())
        tmp.qos = qos
        synchronized(lock) {
            if (client.isConnected) {
                client.publish(actuatorTopic, tmp)
                client.publish(actuatorTopicHedgehog, tmp)
            }
        }
    }
}
