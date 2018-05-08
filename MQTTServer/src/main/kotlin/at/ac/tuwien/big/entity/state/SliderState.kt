package at.ac.tuwien.big.entity.state

/**
 * State of the slider
 */
data class SliderState(
        override var name: String = "Snapshot",
        override var entity: String = "Slider",
        var sliderPosition: Double = 0.0
) : StateEvent {
    override fun match(other: StateEvent, similar: (Double, Double) -> Boolean): Boolean {
        return if (other is SliderState) {
            similar(this.sliderPosition, other.sliderPosition)
        } else {
            false
        }
    }
}
