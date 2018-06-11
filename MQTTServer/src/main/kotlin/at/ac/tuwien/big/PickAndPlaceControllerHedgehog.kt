package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.RoboticArmState
import at.ac.tuwien.big.entity.state.StateEvent
import at.ac.tuwien.big.entity.transition.*

/**
 * This controller holds the core logic of the production steps during a simulation by searching the defined
 * successor state for a given input state.
 */
object PickAndPlaceControllerHedgehog {

    var stateMachine = StateMachineHedgehog(listOf(RoboticArmState()))

    var snapshot: RoboticArmState = RoboticArmState()
        private set

    private var roboticArmState: RoboticArmState = RoboticArmState()

    private var latestMatch: RoboticArmState = RoboticArmState()

    private var targetState: StateEvent = RoboticArmState()

    /**
     * Return latest matching state
     */
    fun latest() = latestMatch

    fun target() = targetState

    /**
     * Update the unit with new sensor information. This includes matching the updated state to the set of defined states.
     */
    fun update(e: StateEvent) {
        var change = false
        when (e) {
            is RoboticArmState -> {
                snapshot = e
                val match = matchState(snapshot)
                if (match != null && snapshot != match) {
                    roboticArmState = match
                    change = true
                }
            }
        }
        if (change) {
            latestMatch = roboticArmState
        }
    }

    /**
     * Return the defined successor state of the latest matching state, according to the state machine
     */
    fun next(): Transition? {
        val transition = stateMachine.successor(latestMatch)
        if (transition != null) {
            targetState = transition.targetState
        }
        return transition
    }

    fun hasFinished() = stateMachine.isEndState(latestMatch)

    fun first() = stateMachine.states.first()
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
                        "second-arm-roto ${target.secondArmRotation} ${transition.secondArmSpeed}",
                        "wrist-goto ${target.wristPosition} 1.0",
                        "wrist-roto ${target.wristRotation} 1.0",
                        "gripper-goto ${target.gripperPosition} 1.0"
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
        return stateMachine.states.firstOrNull { roboticArmState.match(it, doubleAccuracy) }
    }
}
