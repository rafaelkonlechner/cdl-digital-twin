package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
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
        try {
            when (e) {
                is RoboticArmState -> {
                    snapshot = snapshot.copy(roboticArmState = e)
                }
                is SliderState -> {
                    snapshot = snapshot.copy(sliderState = e)
                }
                is ConveyorState -> {
                    val c = snapshot.conveyorState ?: ConveyorState()
                    val cNew = c.copy(
                            adjusterPosition = e.adjusterPosition ?: c.adjusterPosition,
                            detected = e.detected ?: c.detected,
                            inPickupWindow = e.inPickupWindow ?: c.inPickupWindow)
                    snapshot = snapshot.copy(conveyorState = cNew)
                }
                is TestingRigState -> {
                    val t = snapshot.testingRigState ?: TestingRigState()
                    val tNew = t.copy(
                            objectCategory = e.objectCategory ?: t.objectCategory,
                            platformPosition = e.platformPosition ?: t.platformPosition,
                            heatplateTemperature = e.heatplateTemperature ?: t.heatplateTemperature)
                    snapshot = snapshot.copy(testingRigState = tNew)
                }
            }
            val match = matchState(snapshot)
            if (match != null && latestMatch != match) {
                latestMatch = match
                if (match.second) {
                    notify(latestMatch.first)
                } else if (latestMatch.first.altEnvironment != null) {
                    notify(latestMatch.first)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Return the defined successor state of the latest matching state, according to the state machine
     */
    fun next(): List<Transition> {
        val successor = stateMachine?.successor(latestMatch.first, latestMatch.second)
        return if (successor != null) {
            targetState = successor
            val env = latestMatch.first.environment
            val succ = successor.environment
            val result = mutableListOf<Transition>()
            if (succ.roboticArmState != null) {
                result.add(RoboticArmTransition(env.roboticArmState ?: RoboticArmState(), succ.roboticArmState))
            }
            if (succ.conveyorState != null) {
                result.add(ConveyorTransition(env.conveyorState ?: ConveyorState(), succ.conveyorState))
            }
            if (succ.testingRigState != null) {
                result.add(TestingRigTransition(env.testingRigState ?: TestingRigState(), succ.testingRigState))
            }
            if (succ.sliderState != null) {
                result.add(SliderTransition(env.sliderState ?: SliderState(), succ.sliderState))
            }
            return result
        } else {
            emptyList()
        }
    }

    /**
     * Return the defined successor state of the latest matching state, according to the state machine
     */
    fun reset(): List<Transition> {
        val env = (stateMachine?.states?.first() as BasicState).environment
        return listOf(
                RoboticArmTransition(RoboticArmState(), env.roboticArmState
                        ?: RoboticArmState()),
                ConveyorTransition(ConveyorState(), env.conveyorState
                        ?: ConveyorState()),
                SliderTransition(SliderState(), env.sliderState
                        ?: SliderState()),
                TestingRigTransition(TestingRigState(), env.testingRigState
                        ?: TestingRigState())
        )
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

        val matches = (basicStates + choiceStates).filter {
            env.matches(it.environment) || (it.altEnvironment != null && env.matches(it.altEnvironment!!))
        }

        return if (matches.isNotEmpty()) {
            val match = if (matches.size > 1) {
                matches.component2()
            } else {
                matches.first()
            }
            Pair(match, env.matches(match.environment))
        } else {
            null
        }
    }
}
