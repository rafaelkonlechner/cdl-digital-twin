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
) : StateEvent
