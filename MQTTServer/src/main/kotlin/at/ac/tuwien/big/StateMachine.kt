package at.ac.tuwien.big

import at.ac.tuwien.big.sm.BasicState
import at.ac.tuwien.big.sm.ChoiceState
import at.ac.tuwien.big.sm.StateBase

/**
 * Holds all defined states and according successor
 */
class StateMachine(val states: List<StateBase>) {

    /**
     * Finds the successor state for the given [state]. Our state machine defines two state types: [BasicState] und
     * [ChoiceState]. A choice state defines two alternative sequences of basic states. Based on a condition, either the
     * first or the second sequence is chosen as successor. This condition is [useFirstChoice], which is determined in
     * an earlier step, where the current system environment is compared to [BasicState.environment] and
     * [BasicState.altEnvironment]. If the first two match, [useFirstChoice] is true, if the second matches, it is false.
     */
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
                val choiceIndex = states.indexOf(choice)
                val first = choice.choices.first
                val second = choice.choices.second
                return if (first.contains(state)) {
                    val index = first.indexOf(state)
                    if (index in 0 until first.lastIndex) {
                        first[index + 1]
                    } else {
                        if (choiceIndex + 1 in states.indices) {
                            states[choiceIndex + 1] as BasicState
                        } else {
                            null
                        }
                    }
                } else {
                    val index = second.indexOf(state)
                    if (index in 0 until second.lastIndex) {
                        second[index + 1]
                    } else {
                        if (choiceIndex + 1 in states.indices) {
                            states[choiceIndex + 1] as BasicState
                        } else {
                            null
                        }
                    }
                }
            } else {
                return null
            }
        }
    }

    /**
     * Is the given state an end state?
     * @returns `true`, if yes, `false` if no
     */
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

    fun all(): List<BasicState> {
        return states
                .map {
                    when (it) {
                        is BasicState -> listOf(it)
                        is ChoiceState -> it.choices.first + it.choices.second
                        else -> emptyList()
                    }

                }
                .flatten()
    }
}
