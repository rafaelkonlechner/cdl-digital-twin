package at.ac.tuwien.big

import at.ac.tuwien.big.MQTT.detectionCameraTopic
import at.ac.tuwien.big.MQTT.pickupCameraTopic
import at.ac.tuwien.big.MQTT.sendMQTTTransitionCommand
import at.ac.tuwien.big.MQTT.sensorTopic
import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.rest.WebServiceController
import com.google.gson.Gson
import java.io.File
import java.util.*
import kotlin.concurrent.schedule

object MessageController {

    private val gson = Gson()
    private val random = Random()
    private val timer = Timer()
    private var inTransition = false
    var messageRate: Int = 10
    var autoPlay: Boolean = true
    var recording: Boolean = false

    init {
        MQTT.subscribe(listOf(sensorTopic, detectionCameraTopic, pickupCameraTopic), MessageController::onMessage)
    }

    fun start() {
        timer.schedule(0, 200) {
            issueTransition()
        }
    }

    private fun onMessage(topic: String, message: String) {
        when (topic) {
            sensorTopic -> {
                val state = parse(message)
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
                    sendWebSocketMessageSensor(message)
                }

            }
            detectionCameraTopic -> {
                val code = QRCode.read(message)
                if (code != null) {
                    sendWebSocketMessageQRCodeScanner(gson.toJson(code).toString())
                }
                val color = if (code == null) ObjectCategory.NONE else if (code.color == "red") ObjectCategory.RED else ObjectCategory.GREEN
                PickAndPlaceController.update(TestingRigState(objectCategory = color))

                val detection = File.createTempFile("detection", ".png")
                detection.writeBytes(CameraSignal.fromBase64(message))
                println(detection.totalSpace)
                CameraSignal.analyzeImage(detection, {
                    sendWebSocketMessageDetectionCamera("{\"image\": \"message\", \"tracking\": ${gson.toJson(it)}}")
                    detection.delete()
                })
            }
            pickupCameraTopic -> {
                val pickup = File.createTempFile("pickup", ".png")
                pickup.writeBytes(CameraSignal.fromBase64(message))
                println(pickup.totalSpace)
                CameraSignal.analyzeImage(pickup, {
                    val tracking = it.firstOrNull()
                    val detected = tracking != null
                    val inPickupWindow = tracking != null && 36 < tracking.x && tracking.x < 125 && 60 < tracking.y && tracking.y < 105
                    PickAndPlaceController.update(ConveyorState(detected = detected, inPickupWindow = inPickupWindow))
                    sendWebSocketMessagePickupCamera("{\"image\": \"message\", \"tracking\": ${gson.toJson(it)}}")
                    pickup.delete()
                })
            }
        }

    }

    private fun issueTransition() {
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

    private fun parse(payload: String): StateEvent {
        val basicState = gson.fromJson(payload, BasicStateEvent::class.java)
        return when (basicState.entity) {
            "RoboticArm" -> gson.fromJson(payload, RoboticArmState::class.java)
            "Slider" -> gson.fromJson(payload, SliderState::class.java)
            "Conveyor" -> gson.fromJson(payload, ConveyorState::class.java)
            "TestingRig" -> gson.fromJson(payload, TestingRigState::class.java)
            "Gate" -> gson.fromJson(payload, GatePassed::class.java)
            else -> BasicStateEvent()
        }
    }

    private fun sendWebSocketMessageSensor(message: String) {
        sendWebSocketMessage("sensor", message)
    }

    private fun sendWebSocketMessagePickupCamera(message: String) {
        sendWebSocketMessage("pickupCamera", message)
    }

    private fun sendWebSocketMessageDetectionCamera(message: String) {
        sendWebSocketMessage("detectionCamera", message)
    }

    private fun sendWebSocketMessageQRCodeScanner(message: String) {
        sendWebSocketMessage("qrCode", message)
    }

    private fun sendWebSocketMessage(topic: String, message: String) {
        WebServiceController.push(topic, message)
    }
}
