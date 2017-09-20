package at.ac.tuwien.big.entity.state

data class BasicStateEvent(
        override var name: String = "Basic",
        override var entity: String = "Empty"
) : StateEvent
