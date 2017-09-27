package at.ac.tuwien.big.entity.state

/**
 * Most basic [Transition]
 */
data class BasicTransition(
        override var startState: StateEvent,
        override var targetState: StateEvent
) : Transition