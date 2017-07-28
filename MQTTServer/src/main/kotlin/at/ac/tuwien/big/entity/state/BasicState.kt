package at.ac.tuwien.big.entity.state

data class BasicState(
        override var name: String = "Basic",
        override var entity: String = "Empty"
) : State
