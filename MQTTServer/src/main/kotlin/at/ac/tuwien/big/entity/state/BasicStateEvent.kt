package at.ac.tuwien.big.entity.state

/**
 * Most basic [StateEvent]
 */
data class BasicStateEvent(
        override var name: String = "Basic",
        override var entity: String = "Empty"
) : StateEvent
