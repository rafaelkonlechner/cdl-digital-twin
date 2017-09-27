package at.ac.tuwien.big.entity.state

/**
 * State change of the slider
 */
data class SliderTransition(
        override val startState: SliderState,
        override val targetState: SliderState
) : Transition
