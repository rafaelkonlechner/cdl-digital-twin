package at.ac.tuwien.big.entity.state

data class BasicTransition(
        override var startState: StateEvent,
        override var targetState: StateEvent
) : Transition