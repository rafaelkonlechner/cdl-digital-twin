package at.ac.tuwien.big.entity.transition

import at.ac.tuwien.big.entity.state.StateEvent

/**
 * Describes the change from one state to another
 */
interface Transition {
    val startState: StateEvent
    val targetState: StateEvent
}