package at.ac.tuwien.big.entity.state

data class ConveyorState(
        override var name: String = "Snapshot",
        override var entity: String = "Conveyor",
        var adjusterPosition: Double = 0.0,
        var detected: Boolean = false,
        var inPickupWindow: Boolean = false
) : StateEvent
