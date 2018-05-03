package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.entity.transition.*
import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.*
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.support.MessageBuilder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import java.io.File
import java.util.*
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
    private val random = Random()
    private var inTransition = false
    private var lock = Any()
    var autoPlay: Boolean = true
    var recording: Boolean = true


    init {
        println("Connecting to MQTT endpoint.")
        client = MqttClient("tcp://localhost:1883", "Controller", null)
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
        try {
            when (topic) {
                sensorTopic -> {
                    val state = parse(String(message!!.payload))
                    if (recording) {
                        if (state is RoboticArmState) {
                            val ref = PickAndPlaceController.getReference(System.currentTimeMillis())
                            inTransition = !StateMachine.match(state, PickAndPlaceController.targetState)
                            val label = if (inTransition) null else PickAndPlaceController.targetState.name
                            TimeSeriesCollectionService.savePoint(state, ref, label)
                        }
                    }
                    PickAndPlaceController.update(state)
                    /*
                     * For 20 sensor updates per second, on average send one frame per second
                     */
                    if (random.nextDouble() < 0.05) {
                        sendWebSocketMessageSensor(String(message.payload))
                    }

                }
                detectionCameraTopic -> {
                    val code = QRCode.read(String(message!!.payload))
                    if (code != null) {
                        sendWebSocketMessageQRCodeScanner(gson.toJson(code).toString())
                    }
                    val color = if (code == null) ObjectCategory.NONE else if (code.color == "red") ObjectCategory.RED else ObjectCategory.GREEN
                    PickAndPlaceController.update(TestingRigState(objectCategory = color))

                    val detection = File("detection.png")
                    detection.writeBytes(CameraSignal.fromBase64(String(message.payload)))
                    CameraSignal.analyzeImage(detection, {
                        sendWebSocketMessageDetectionCamera("{\"image\": \"${String(message.payload)}\", \"tracking\": ${gson.toJson(it)}}")
                    })
                }
                pickupCameraTopic -> {
                    val pickup = File("pickup.png")
                    pickup.writeBytes(CameraSignal.fromBase64(String(message!!.payload)))
                    CameraSignal.analyzeImage(pickup, {
                        val tracking = it.firstOrNull()
                        val detected = tracking != null
                        val inPickupWindow = tracking != null && 36 < tracking.x && tracking.x < 125 && 60 < tracking.y && tracking.y < 105
                        PickAndPlaceController.update(ConveyorState(detected = detected, inPickupWindow = inPickupWindow))
                        sendWebSocketMessagePickupCamera("{\"image\": \"${String(message.payload)}\", \"tracking\": ${gson.toJson(it)}}")
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    @Scheduled(fixedDelay = 200)
    fun issueTransition() {
        try {
            if (autoPlay && !inTransition) {
                val latest = PickAndPlaceController.latest()
                val transition = PickAndPlaceController.next()
                println("Next: ${latest.roboticArmState?.name}, ${latest.sliderState?.name}, ${latest.conveyorState?.name}, ${latest.testingRigState?.name} -> ${transition?.targetState?.name}")
                PickAndPlaceController.start(transition)
                sendMQTTTransitionCommand(transition)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun connectionLost(cause: Throwable?) {
        throw cause ?: Exception("Connection lost.")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

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
            e.printStackTrace()
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
