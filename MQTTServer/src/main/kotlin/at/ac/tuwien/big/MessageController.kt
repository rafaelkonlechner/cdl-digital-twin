package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.entity.transition.RoboticArmTransition
import com.google.gson.Gson
import java.io.File
import java.util.*
import kotlin.concurrent.schedule
import at.ac.tuwien.big.PickAndPlaceControllerHedgehog as controller


class MessageController(private val mqtt: MQTT,
                        private val objectTracker: ObjectTracker,
                        private val timeSeriesDatabase: TimeSeriesDatabase) {

    private val gson = Gson()
    private val timer = Timer()
    private var inTransition = false
    private val subscribers = mutableListOf<(String, String) -> Unit>()
    var messageRate: Int = 10
    var autoPlay: Boolean = false
    var recording: Boolean = false
    //val webcam: Webcam

    init {
        mqtt.subscribe(listOf(simSensor, sensor, detectionCamera, pickupCamera), this::onMessage)
        //webcam = Webcam.getWebcams()[1]
        //webcam.viewSize = Dimension(640, 480)
    }

    fun subscribe(callback: (String, String) -> Unit) {
        subscribers.add(callback)
    }

    fun start() {
        timer.schedule(0, 1000) {
            observe()
        }
        /*timer.schedule(0, 1000) {
            webcam.open()
            val file = File.createTempFile("webcam", ".png")
            val stream = ByteArrayOutputStream()
            val img = webcam.image.getSubimage(320, 220, 260, 260)
            ImageIO.write(img, "PNG", file)
            ImageIO.write(img, "PNG", stream)
            webcam.close()
            objectTracker.track(file, {
                val base64 = Base64.getEncoder().encodeToString(stream.toByteArray())
                sendWebSocketMessagePickupCamera("{\"image\": \"$base64\", \"tracking\": ${gson.toJson(it)}}")
                file.delete()
            })
        }*/
    }

    private fun onMessage(topic: String, message: String) {
        when (topic) {
            sensor, simSensor -> {
                val state = parse(message)
                if (recording) {
                    if (state is RoboticArmState) {
                        //val ref = PickAndPlaceControllerHedgehog.getReference(System.currentTimeMillis())
                        //val inTransition = state.match(PickAndPlaceControllerHedgehog.targetState, doubleAccuracy)
                        //val label = if (inTransition) null else PickAndPlaceControllerHedgehog.targetState.name
                        //timeSeriesDatabase.savePoint(state, ref, label)
                    }
                }
                controller.update(state)
                sendWebSocketMessageSensor(gson.toJson(state))
            }
            detectionCamera -> {
                val code = QRCode.read(message)
                if (code != null) {
                    sendWebSocketMessageQRCodeScanner(gson.toJson(code).toString())
                }
                val color = if (code == null) ObjectCategory.NONE else if (code.color == "red") ObjectCategory.RED else ObjectCategory.GREEN
                controller.update(TestingRigState(objectCategory = color))

                val detection = File.createTempFile("detection", ".png")
                detection.writeBytes(fromBase64(message))
                objectTracker.track(detection, {
                    sendWebSocketMessageDetectionCamera("{\"image\": \"$message\", \"tracking\": ${gson.toJson(it)}}")
                    detection.delete()
                })
            }
            pickupCamera -> {
                val pickup = File.createTempFile("pickup", ".png")
                pickup.writeBytes(fromBase64(message))
                objectTracker.track(pickup, {
                    val tracking = it.firstOrNull()
                    val detected = tracking != null
                    val inPickupWindow = tracking != null && 36 < tracking.x && tracking.x < 125 && 60 < tracking.y && tracking.y < 105
                    controller.update(ConveyorState(detected = detected, inPickupWindow = inPickupWindow))
                    sendWebSocketMessagePickupCamera("{\"image\": \"$message\", \"tracking\": ${gson.toJson(it)}}")
                    pickup.delete()
                })
            }
        }
    }

    private fun observe() {
        if (autoPlay) {
            val latest = controller.latest()
            val transition = controller.next()
            if (transition != null /*&& !controller.snapshot.match(controller.target(), doubleAccuracy)*/) {
                println("Next: ${latest.name} -> ${transition.targetState.name}")
                val context = controller.latest()
                sendWebSocketMessageContext(gson.toJson(context))
                val commands = controller.transform(transition)
                for (c in commands) {
                    mqtt.send(c)
                }
            } else if (controller.hasFinished()) {
                val transition = RoboticArmTransition(controller.latest(), controller.first())
                println("Next: ${latest.name} -> ${transition.targetState}")
                val context = controller.latest()
                sendWebSocketMessageContext(gson.toJson(context))
                val commands = controller.transform(transition)
                for (c in commands) {
                    mqtt.send(c)
                }
            }
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

    private fun sendWebSocketMessageSensor(data: String) {
        sendWebSocketMessage("sensor", data)
    }

    private fun sendWebSocketMessageContext(data: String) {
        sendWebSocketMessage("context", data)
    }

    private fun sendWebSocketMessagePickupCamera(data: String) {
        sendWebSocketMessage("pickupCamera", data)
    }

    private fun sendWebSocketMessageDetectionCamera(data: String) {
        sendWebSocketMessage("detectionCamera", data)
    }

    private fun sendWebSocketMessageQRCodeScanner(data: String) {
        sendWebSocketMessage("qrCode", data)
    }

    private fun sendWebSocketMessage(topic: String, data: String) {
        subscribers.forEach {
            it(topic, data)
        }
    }
}
