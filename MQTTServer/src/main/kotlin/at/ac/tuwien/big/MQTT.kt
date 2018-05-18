package at.ac.tuwien.big

import org.eclipse.paho.client.mqttv3.*

/**
 * Send and receive MQTT messages via the specified topics
 */
class MQTT(host: String, receivingTopics: List<String>, private val sendingTopics: List<String>) : MqttCallback {

    private data class Subscription(val topics: List<String>, val callback: (String, String) -> Unit)

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
        println("Subscribing to topics: $receivingTopics")
        client.setCallback(this)
        for (topic in receivingTopics) {
            client.subscribe(topic)
        }
    }

    /**
     * Disconnect from MQTT queue
     */
    fun cleanup() {
        client.disconnect()
        client.close()
    }

    /**
     * Subscribe to the specified list of topics
     */
    fun subscribe(topics: List<String>, callback: (String, String) -> Unit) = subscribers.add(Subscription(topics, callback))

    /**
     * Send a message via all sending topics
     */
    fun send(message: String) {
        val tmp = MqttMessage(message.toByteArray())
        tmp.qos = qos
        synchronized(lock) {
            if (client.isConnected) {
                for (topic in sendingTopics) {
                    client.publish(topic, tmp)
                }
            }
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        subscribers.forEach { if (topic in it.topics) it.callback(topic!!, message.toString()) }
    }

    override fun connectionLost(cause: Throwable?) {
        throw cause ?: Exception("Connection lost.")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}
}
