package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class StateController(val messageController: MessageController) {

    @GetMapping("/all")
    fun findAll() = States.all

    @GetMapping("/autoPlay")
    fun autoPlay() = messageController.autoPlay

    @GetMapping("/recording")
    fun record() = messageController.recording


    @RequestMapping(value = "/autoPlay", method = arrayOf(RequestMethod.PUT))
    fun setAutoPlay(@RequestBody autoPlay: Boolean) {
        messageController.autoPlay = autoPlay
    }

    @RequestMapping(value = "/recording", method = arrayOf(RequestMethod.PUT))
    fun setRecording(@RequestBody recording: Boolean) {
        messageController.recording = recording
    }

    @RequestMapping(value = "/resetData", method = arrayOf(RequestMethod.PUT))
    fun resetData() {
        TimeSeriesCollectionService.resetDatabase()
    }

    @GetMapping("/roboticArmState")
    fun getRoboticArmState() = messageController.roboticArmState

    @GetMapping("/sliderState")
    fun getSliderState() = messageController.sliderState

    @GetMapping("/conveyorState")
    fun getConveyorState() = messageController.conveyorState

    @GetMapping("/testingRigState")
    fun getTestingRigState() = messageController.testingRigState

    @RequestMapping(value = "/roboticArmState", method = arrayOf(RequestMethod.PUT))
    fun setRoboticArmState(@RequestBody state: String) {
        val match = States.roboticArm[state]
        if (match != null) {
            messageController.sendMQTTTransitionCommand(RoboticArmTransition(RoboticArmState(), match))
        }
    }

    @RequestMapping(value = "/sliderState", method = arrayOf(RequestMethod.PUT))
    fun setSliderState (@RequestBody state: String) {
        val match = States.slider[state]
        if (match != null) {
            messageController.sendMQTTTransitionCommand(SliderTransition(SliderState(), match))
        }
    }

    @RequestMapping(value = "/conveyorState", method = arrayOf(RequestMethod.PUT))
    fun setConveyorState(@RequestBody state: String) {
        val match = States.conveyor[state]
        if (match != null) {
            messageController.sendMQTTTransitionCommand(ConveyorTransition(ConveyorState(), match))
        }
    }

    @RequestMapping(value = "/testingRigState", method = arrayOf(RequestMethod.PUT))
    fun setTestingRigState(@RequestBody state: String) {
        val match = States.testingRig[state]
        if (match != null) {
            messageController.sendMQTTTransitionCommand(TestingRigTransition(TestingRigState(), match))
        }
    }
}
