package at.ac.tuwien.big.entity.state

data class SliderTransition(
        override val startState: SliderState,
        override val targetState: SliderState
) : Transition
