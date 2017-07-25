package at.ac.tuwien.big.entity.state

data class SliderState(
        override val name: String,
        val sliderPosition: Double = 0.0
) : State
