package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.entity.transition.*
import at.ac.tuwien.big.StateMachineSimulation.States as s
import at.ac.tuwien.big.StateMachineSimulation.Transitions as t

/**
 * This controller holds the core logic of the production steps during a simulation by searching the defined
 * successor state for a given input state.
 */
object PickAndPlaceControllerSimulation {

    var roboticArmSnapshot: RoboticArmState = RoboticArmState()
        private set
    private var roboticArmState: RoboticArmState = RoboticArmState()

    var sliderSnapshot: SliderState = SliderState()
        private set
    private var sliderState: SliderState = SliderState()

    var conveyorSnapshot: ConveyorState = ConveyorState()
        private set
    private var conveyorState: ConveyorState = ConveyorState()

    var testingRigSnapshot: TestingRigState = TestingRigState()
        private set
    private var testingRigState: TestingRigState = TestingRigState()

    private var latestMatch: Environment = Environment()

    var targetState = s.idle
        private set

    private var transitionStart = System.currentTimeMillis()
    private var timeBase = 1000L
    private var timeMainArm = 1000L
    private var timeSecondArm = 1000L
    private var timeWrist = 1000L
    private var timeGripper = 1000L

    /**
     * Return latest matching state
     */
    fun latest() = latestMatch

    /**
     * Update the unit with new sensor information. This includes matching the updated state to the set of defined states.
     */
    fun update(e: StateEvent) {
        var change = false
        when (e) {
            is RoboticArmState -> {
                roboticArmSnapshot = e
                val match = matchState(roboticArmSnapshot)
                if (match != null && roboticArmSnapshot != match) {
                    roboticArmState = match
                    change = true
                }
            }
            is SliderState -> {
                sliderSnapshot = e
                val match = matchState(sliderSnapshot)
                if (match != null && sliderSnapshot != match) {
                    sliderState = match
                    change = true
                }
            }
            is ConveyorState -> {
                if (e.adjusterPosition != null) {
                    conveyorSnapshot = conveyorSnapshot.copy(adjusterPosition = e.adjusterPosition)
                }
                if (e.detected != null) {
                    conveyorSnapshot = conveyorSnapshot.copy(detected = e.detected)
                }
                if (e.inPickupWindow != null) {
                    conveyorSnapshot = conveyorSnapshot.copy(inPickupWindow = e.inPickupWindow)
                }
                val match = matchState(conveyorSnapshot)
                if (match != null && conveyorSnapshot != match) {
                    conveyorState = match
                    change = true
                }
            }
            is TestingRigState -> {
                if (e.objectCategory != null) {
                    testingRigSnapshot = testingRigSnapshot.copy(objectCategory = e.objectCategory)
                }
                if (e.platformPosition != null) {
                    testingRigSnapshot = testingRigSnapshot.copy(platformPosition = e.platformPosition)
                }
                if (e.heatplateTemperature != null) {
                    testingRigSnapshot = testingRigSnapshot.copy(heatplateTemperature = e.heatplateTemperature)
                }
                val match = matchState(testingRigSnapshot)
                if (match != null && e != match) {
                    testingRigState = match
                    change = true
                }
            }
        }
        if (change) {
            latestMatch = Environment(roboticArmState, sliderState, conveyorState, testingRigState)
        }
    }

    /**
     * Return the defined successor state of the lastest matching state, according to the state machine
     */
    fun next() = StateMachine.successor(latestMatch.roboticArmState ?: RoboticArmState())

    /**
     * Indicate the start of a transition. This sets the starting time for finding reference points.
     */
    fun start(transition: Transition?) {
        if (transition is RoboticArmTransition) {
            targetState = transition.targetState
            transitionStart = System.currentTimeMillis()
            val start = latestMatch.roboticArmState!!
            timeBase = timeToTarget(start.basePosition, targetState.basePosition, 0.000873, transition.baseSpeed)
            timeMainArm = timeToTarget(start.mainArmPosition, targetState.mainArmPosition, 0.000873, transition.mainArmSpeed)
            timeSecondArm = timeToTarget(start.secondArmPosition, targetState.secondArmPosition, 0.000873, transition.secondArmSpeed)
            timeWrist = timeToTarget(start.headMountPosition, targetState.headMountPosition)
            timeGripper = timeToTarget(start.gripperPosition, targetState.gripperPosition)
        }
    }

    /**
     * Return a reference point indicating the desired state during state transitions. Reference points are linear
     * interpolations between the attribute values of the start and the target state.
     */
    fun getReference(time: Long): RoboticArmState? {
        val diff = time - transitionStart
        val startState = latestMatch.roboticArmState
        return if (startState != null) {
            val baseRef = getPosition(startState.basePosition, targetState.basePosition, timeBase, Math.min(timeBase, diff))
            val mainArmRef = getPosition(startState.mainArmPosition, targetState.mainArmPosition, timeMainArm, Math.min(timeMainArm, diff))
            val secondArmRef = getPosition(startState.secondArmPosition, targetState.secondArmPosition, timeSecondArm, Math.min(timeSecondArm, diff))
            val wristRef = getPosition(startState.headMountPosition, targetState.headMountPosition, timeWrist, Math.min(timeWrist, diff))
            val gripperRef = getPosition(startState.gripperPosition, targetState.gripperPosition, timeGripper, Math.min(timeGripper, diff))
            RoboticArmState("Reference", "RoboticArm", baseRef, mainArmRef, secondArmRef, wristRef, gripperRef)
        } else {
            null
        }
    }

    /**
     * Transform successor into 'goto' commands for the MQTT API
     */
    fun transform(transition: Transition): List<String> {
        return when (transition) {
            is RoboticArmTransition -> {
                val target = transition.targetState
                listOf(
                        "base-goto ${target.basePosition} ${transition.baseSpeed}",
                        "main-arm-goto ${target.mainArmPosition} ${transition.mainArmSpeed}",
                        "second-arm-goto ${target.secondArmPosition} ${transition.secondArmSpeed}",
                        "head-goto ${target.headPosition}",
                        "head-mount-goto ${target.headMountPosition}",
                        "gripper-goto ${target.gripperPosition}"
                )
            }
            is SliderTransition -> {
                listOf("slider-goto ${transition.targetState.sliderPosition}")
            }
            is ConveyorTransition -> {
                listOf("adjuster-goto ${transition.targetState.adjusterPosition}")
            }
            is TestingRigTransition -> {
                val t = transition.targetState
                listOf(
                        if (t.platformPosition != null) {
                            "platform-goto ${t.platformPosition}"
                        } else {
                            ""
                        },
                        if (t.heatplateTemperature != null) {
                            "platform-heatup ${t.heatplateTemperature}"
                        } else {
                            ""
                        }
                ).filter { !it.isEmpty() }
            }
            else -> emptyList()
        }
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    private fun matchState(roboticArmState: RoboticArmState): RoboticArmState? {
        return s.roboticArm.values.firstOrNull { roboticArmState.match(it, doubleAccuracy) }
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    private fun matchState(sliderState: SliderState): SliderState? {
        return s.slider.values.firstOrNull { sliderState.match(it, doubleAccuracy) }
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    private fun matchState(conveyorState: ConveyorState): ConveyorState? {
        return s.conveyor.values.firstOrNull { conveyorState.match(it, doubleAccuracy) }
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    private fun matchState(testingRigState: TestingRigState): TestingRigState? {
        return s.testingRig.values
                .find { testingRigState.match(it, singleAccuracy) }
    }
}
