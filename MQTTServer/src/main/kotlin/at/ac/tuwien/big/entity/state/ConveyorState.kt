package at.ac.tuwien.big.entity.state

data class ConveyorState(
        override var name: String = "Snapshot",
        override var entity: String = "Conveyor",
        var adjusterPosition: Double = 0.0,
        var objectPosition: Double = 0.0,
        var objectRotation: Double = 0.0
) : State
