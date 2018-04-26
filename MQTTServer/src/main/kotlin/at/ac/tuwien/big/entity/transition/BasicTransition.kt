package at.ac.tuwien.big.entity.transition

import at.ac.tuwien.big.entity.state.StateEvent

/**
 * Most basic [Transition]
 */
data class BasicTransition(
        override var startState: StateEvent,
        override var targetState: StateEvent
) : Transition