package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.ConveyorState
import at.ac.tuwien.big.entity.state.RoboticArmState
import at.ac.tuwien.big.entity.state.SliderState
import at.ac.tuwien.big.entity.state.TestingRigState
import at.ac.tuwien.big.entity.transition.RoboticArmTransition
import at.ac.tuwien.big.entity.transition.Transition
import at.ac.tuwien.big.StateMachine.States as s
import at.ac.tuwien.big.StateMachine.Transitions as t

/**
 * Holds all defined states and successor of the environment
 */
class StateMachineHedgehog(val states: List<RoboticArmState>) {

    fun successor(state: RoboticArmState): RoboticArmState? {
        val currentIndex = states.indexOf(state)
        return if (currentIndex in 0 until states.lastIndex) {
            states[currentIndex + 1]
        } else {
            null
        }
    }

    fun isEndState(state: RoboticArmState) = state == states.last()
}
