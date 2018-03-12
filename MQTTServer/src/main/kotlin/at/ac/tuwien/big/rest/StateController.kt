package at.ac.tuwien.big.rest

import at.ac.tuwien.big.MessageController
import at.ac.tuwien.big.StateMachine
import at.ac.tuwien.big.entity.state.*
import org.springframework.web.bind.annotation.*

/**
 * Get and set environment states via REST
 */
@RestController
@CrossOrigin
class StateController(val messageController: MessageController) {

    @GetMapping("/all")
    fun findAll() = StateMachine.all

    /**
     * @return the current state of the robotic arm
     */
    @GetMapping("/roboticArmState")
    fun getRoboticArmState() = messageController.roboticArmState

    /**
     * @return the current state of the slider
     */
    @GetMapping("/sliderState")
    fun getSliderState() = messageController.sliderState

    /**
     * @return the current state of the conveyor
     */
    @GetMapping("/conveyorState")
    fun getConveyorState() = messageController.conveyorState

    /**
     * @return the current state of the testing rig
     */
    @GetMapping("/testingRigState")
    fun getTestingRigState() = messageController.testingRigState

    /**
     * Set a new state for the robotic arm. The environment will adapt to it.
     */
    @RequestMapping(value = ["/roboticArmState"], method = arrayOf(RequestMethod.PUT))
    fun setRoboticArmState(@RequestBody state: String) {
        val match = StateMachine.roboticArm[state]
        if (match != null) {
            messageController.sendMQTTTransitionCommand(RoboticArmTransition(RoboticArmState(), match))
        }
    }

    /**
     * Set a new state for the slider. The environment will adapt to it.
     */
    @RequestMapping(value = ["/sliderState"], method = arrayOf(RequestMethod.PUT))
    fun setSliderState(@RequestBody state: String) {
        val match = StateMachine.slider[state]
        if (match != null) {
            messageController.sendMQTTTransitionCommand(SliderTransition(SliderState(), match))
        }
    }

    /**
     * Set a new state for the conveyor. The environment will adapt to it.
     */
    @RequestMapping(value = ["/conveyorState"], method = arrayOf(RequestMethod.PUT))
    fun setConveyorState(@RequestBody state: String) {
        val match = StateMachine.conveyor[state]
        if (match != null) {
            messageController.sendMQTTTransitionCommand(ConveyorTransition(ConveyorState(), match))
        }
    }

    /**
     * Set a new state for the testing rig. The environment will adapt to it.
     */
    @RequestMapping(value = ["/testingRigState"], method = arrayOf(RequestMethod.PUT))
    fun setTestingRigState(@RequestBody state: String) {
        val match = StateMachine.testingRig[state]
        if (match != null) {
            messageController.sendMQTTTransitionCommand(TestingRigTransition(TestingRigState(), match))
        }
    }
}
