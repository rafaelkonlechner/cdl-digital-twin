package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.Environment
import at.ac.tuwien.big.entity.state.RoboticArmState
import at.ac.tuwien.big.entity.state.StateEvent
import at.ac.tuwien.big.entity.transition.*
import at.ac.tuwien.big.sm.BasicState
import at.ac.tuwien.big.sm.ChoiceState

/**
 * This controller holds the core logic of the production steps during a simulation by searching the defined
 * successor state for a given input state.
 */
object StateObserver : Observable<BasicState>() {

    var stateMachine: StateMachineHedgehog? = null

    /**
     * Most recently observed state
     */
    private var snapshot: Environment = Environment()

    /**
     * Latest matching state, given the current job
     */
    var latestMatch: Pair<BasicState, Boolean> = Pair(BasicState(), true)
        private set

    /**
     * Successor state, given the latest matching state
     */
    var targetState: BasicState = BasicState()
        private set

    /**
     * Update the unit with new sensor information. This includes matching the updated state to the set of defined states.
     */
    fun update(e: StateEvent) {
        when (e) {
            is RoboticArmState -> {
                snapshot = snapshot.copy(roboticArmState = e)
                val match = matchState(snapshot)
                if (match != null && latestMatch != match) {
                    latestMatch = match
                    if (match.second) {
                        notify(latestMatch.first)
                    } else if (latestMatch.first.altEnvironment != null) {
                        notify(latestMatch.first)
                    }
                }
            }
        }
    }

    /**
     * Return the defined successor state of the latest matching state, according to the state machine
     */
    fun next(): Transition? {
        val successor = stateMachine?.successor(latestMatch.first, latestMatch.second)
        if (successor != null) {
            targetState = successor
            return RoboticArmTransition(latestMatch.first.environment.roboticArmState!!, successor.environment.roboticArmState!!)
        } else {
            return null
        }
    }

    /**
     * Return the defined successor state of the latest matching state, according to the state machine
     */
    fun reset(): Transition {
        return RoboticArmTransition(latestMatch.first.environment.roboticArmState
                ?: RoboticArmState(), (stateMachine?.states?.first() as BasicState).environment.roboticArmState
                ?: RoboticArmState())
    }

    fun atEndState() = stateMachine?.isEndState(latestMatch.first) ?: true

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
                        "head-goto ${target.headPosition} 1.0",
                        "head-mount-goto ${target.headMountPosition} 1.0",
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
    private fun matchState(env: Environment): Pair<BasicState, Boolean>? {

        val basicStates = stateMachine?.states
                ?.filter { it is BasicState }
                ?.map { it as BasicState }
                ?: emptyList()
        val choiceStates = stateMachine?.states
                ?.filter { it is ChoiceState }
                ?.map { it as ChoiceState }
                ?.flatMap { it.choices.first + it.choices.second }
                ?: emptyList()

        val match = (basicStates + choiceStates).firstOrNull {
            env.matches(it.environment) || (it.altEnvironment != null && env.matches(it.altEnvironment!!))
        }

        return if (match != null) {
            Pair(match, env.matches(match.environment))
        } else {
            null
        }
    }
}
