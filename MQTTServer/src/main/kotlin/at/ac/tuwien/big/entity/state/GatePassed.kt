package at.ac.tuwien.big.entity.state

/**
 * Signals an object passing through a gate. Gates act as a light barrier.
 */
data class GatePassed(
        override var name: String = "Snapshot",
        override var entity: String = "Gate",
        var channel: String
) : StateEvent