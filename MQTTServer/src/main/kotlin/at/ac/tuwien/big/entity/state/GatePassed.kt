package at.ac.tuwien.big.entity.state

data class GatePassed(
        override var name: String = "Snapshot",
        override var entity: String = "Gate",
        var channel: String
) : StateEvent