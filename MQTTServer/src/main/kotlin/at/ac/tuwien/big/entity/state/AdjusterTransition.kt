package at.ac.tuwien.big.entity.state

data class AdjusterTransition(
        override val startState: AdjusterState,
        override val targetState: AdjusterState
) : Transition
