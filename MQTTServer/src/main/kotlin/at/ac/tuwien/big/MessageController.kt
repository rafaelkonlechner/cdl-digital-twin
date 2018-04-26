package at.ac.tuwien.big

import at.ac.tuwien.big.entity.message.ItemPosition
import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.entity.transition.*
import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.*
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.support.MessageBuilder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import javax.annotation.PreDestroy

/**
 * Coordinates the flow of messages between the simulation and the client
 */
@Controller
final class MessageController(private val webSocket: SimpMessagingTemplate) : MqttCallback {

    private val qos = 0
    private val sensorTopic = "Sensor-Simulation"
    private val actuatorTopic = "Actuator-Simulation"
    private val sensorTopicHedgehog = "Sensor"
    private val actuatorTopicHedgehog = "Actuator"
    private val detectionCameraTopic = "DetectionCamera"
    private val pickupCameraTopic = "PickupCamera"
    private val gson = Gson()
    private val client: MqttClient
    var autoPlay: Boolean = false
    var recording: Boolean = false
    var lock = Any()

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
    }

    @PreDestroy
    fun cleanup() {
        client.disconnect()
        client.close()
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        when (topic) {
            sensorTopic -> {
                PickAndPlaceController.update(parse(String(message!!.payload)))
                sendWebSocketMessageSensor(String(message.payload))
            }
            detectionCameraTopic -> {
                val code = QRCode.read(String(message!!.payload))
                if (code != null) {
                    sendWebSocketMessageQRCodeScanner(gson.toJson(code).toString())
                }
                val color = if (code == null) ObjectCategory.NONE else if (code.color == "red") ObjectCategory.RED else ObjectCategory.GREEN
                PickAndPlaceController.update(TestingRigState(objectCategory = color))
                sendWebSocketMessageDetectionCamera(String(message.payload))
            }
            pickupCameraTopic -> {
                sendWebSocketMessagePickupCamera(String(message!!.payload))
            }
        }
    }

    @MessageMapping("/actuator")
    fun receiveActuatorWebSocketMessage(command: String) {
        println("Actuator command: " + command)
        if (command.startsWith("goto:")) {
            val stateName = command.split(": ")[1]
            val state = StateMachine.all.filter { it.name == stateName }.first()
            val transition: Transition = when (state) {
                is RoboticArmState -> RoboticArmTransition(state, state)
                is SliderState -> SliderTransition(state, state)
                is ConveyorState -> ConveyorTransition(state, state)
                else -> BasicTransition(state, state)
            }
            sendMQTTTransitionCommand(transition)
        } else {
            sendMQTTDirectCommand(command)
        }
    }

    @MessageMapping("/tracking")
    fun receiveTrackingWebSocketMessage(message: String) {
        val tracking = gson.fromJson(message, ItemPosition::class.java)
        val detected = !(tracking.x == 0.0 && tracking.y == 0.0)
        val inPickupWindow = 36 < tracking.x && tracking.x < 125 && 60 < tracking.y && tracking.y < 105
        PickAndPlaceController.update(ConveyorState(detected = detected, inPickupWindow = inPickupWindow))
    }

    @Scheduled(fixedDelay = 200)
    fun issueTransition() {
        if (autoPlay) {
            val latest = PickAndPlaceController.latest()
            val transition = PickAndPlaceController.next()
            println("Next: ${latest.roboticArmState?.name}, ${latest.sliderState?.name}, ${latest.conveyorState?.name}, ${latest.testingRigState?.name} -> ${transition?.targetState?.name}")
            sendMQTTTransitionCommand(transition)
        }
    }

    override fun connectionLost(cause: Throwable?) {
        throw cause ?: Exception("Connection lost.")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        println("Delivery complete.")
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
        println("Sending via MQTT: $message")
        synchronized(lock) {
            if (client.isConnected) {
                client.publish(actuatorTopic, tmp)
                client.publish(actuatorTopicHedgehog, tmp)
            }
        }
    }

    private fun parse(payload: String): StateEvent {
        try {
            val basicState = gson.fromJson(payload, BasicStateEvent::class.java)
            return when (basicState.entity) {
                "RoboticArm" -> gson.fromJson(payload, RoboticArmState::class.java)
                "Slider" -> gson.fromJson(payload, SliderState::class.java)
                "Conveyor" -> gson.fromJson(payload, ConveyorState::class.java)
                "TestingRig" -> gson.fromJson(payload, TestingRigState::class.java)
                "Gate" -> gson.fromJson(payload, GatePassed::class.java)
                else -> BasicStateEvent()
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return BasicStateEvent()
    }

    private fun sendWebSocketMessageSensor(message: String) {
        sendWebSocketMessage("/topic/sensor", message)
    }

    private fun sendWebSocketMessageContext(message: String) {
        sendWebSocketMessage("/topic/context", message)
    }

    private fun sendWebSocketMessagePickupCamera(message: String) {
        sendWebSocketMessage("/topic/pickupCamera", message)
    }

    private fun sendWebSocketMessageDetectionCamera(message: String) {
        sendWebSocketMessage("/topic/detectionCamera", message)
    }

    private fun sendWebSocketMessageQRCodeScanner(message: String) {
        sendWebSocketMessage("/topic/qrCode", message)
    }

    private fun sendWebSocketMessage(topic: String, message: String) {
        webSocket.send(topic, MessageBuilder.withPayload(message.toByteArray()).build())
    }
}
