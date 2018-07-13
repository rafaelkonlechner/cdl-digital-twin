package at.ac.tuwien.big.entity.state

/**
 * State of the conveyor
 */
data class ConveyorState(
        override var name: String = "Snapshot",
        override var entity: String = "Conveyor",
        val adjusterPosition: Double? = null,
        val detected: Boolean? = null,
        val inPickupWindow: Boolean? = null
) : StateEvent {
    override fun match(other: StateEvent, similar: (Double, Double) -> Boolean): Boolean {
        return if (other is ConveyorState) {
            val adjusterSimilar = if (this.adjusterPosition != null && other.adjusterPosition != null) {
                similar(this.adjusterPosition, other.adjusterPosition)
            } else {
                true
            }
            return adjusterSimilar
                    && (other.detected == null || this.detected == other.detected)
                    && (other.inPickupWindow == null || this.inPickupWindow == other.inPickupWindow)
        } else {
            false
        }
    }
}
