package at.ac.tuwien.big.entity.state

/**
 * State change of the conveyor
 */
data class ConveyorTransition(
        override var startState: ConveyorState,
        override var targetState: ConveyorState
) : Transition
