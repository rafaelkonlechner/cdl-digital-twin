package at.ac.tuwien.big.entity.transition

import at.ac.tuwien.big.entity.state.ConveyorState

/**
 * State change of the conveyor
 */
data class ConveyorTransition(
        override var startState: ConveyorState,
        override var targetState: ConveyorState
) : Transition
