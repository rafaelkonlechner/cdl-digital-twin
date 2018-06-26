package at.ac.tuwien.big

import at.ac.tuwien.big.sm.BasicState
import at.ac.tuwien.big.sm.ChoiceState
import at.ac.tuwien.big.sm.StateBase
import at.ac.tuwien.big.StateMachine.States as s
import at.ac.tuwien.big.StateMachine.Transitions as t

/**
 * Holds all defined states and successor of the environment
 */
class StateMachineHedgehog(val states: List<StateBase>) {

    fun successor(state: BasicState, useFirstChoice: Boolean?): BasicState? {
        val currentIndex = states.indexOf(state)
        if (currentIndex in 0 until states.lastIndex) {
            val next = states[currentIndex + 1]
            return if (next is ChoiceState) {
                if (useFirstChoice!!) next.choices.first.first() else next.choices.second.first()
            } else next as? BasicState
        } else if (currentIndex == states.lastIndex) {
            return null
        } else {
            val choices = states.filter { it is ChoiceState }.map { it as ChoiceState }
            val choice = choices.find { it.choices.first.contains(state) || it.choices.second.contains(state) }
            if (choice != null) {
                val first = choice.choices.first
                val second = choice.choices.second
                return if (first.contains(state)) {
                    val index = first.indexOf(state)
                    if (index in 0 until first.lastIndex) {
                        first[index + 1]
                    } else {
                        states[currentIndex + 1] as BasicState
                    }
                } else {
                    val index = second.indexOf(state)
                    if (index in 0 until second.lastIndex) {
                        second[index + 1]
                    } else {
                        states[currentIndex + 1] as BasicState
                    }
                }
            } else {
                return null
            }
        }
    }

    fun isEndState(state: BasicState): Boolean {
        val isLast = state == states.last()
        return if (isLast) {
            true
        } else {
            val last = states.last()
            if (last is ChoiceState) {
                state == last.choices.first.last() || state == last.choices.second.last()
            } else {
                false
            }
        }
    }
}
