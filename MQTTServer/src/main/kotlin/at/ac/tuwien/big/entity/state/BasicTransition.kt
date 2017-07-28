package at.ac.tuwien.big.entity.state

data class BasicTransition(
        override var startState: State,
        override var targetState: State
) : Transition