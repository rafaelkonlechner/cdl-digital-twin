package at.ac.tuwien.big.entity.state

data class ConveyorTransition(
        override var startState: ConveyorState,
        override var targetState: ConveyorState
) : Transition
