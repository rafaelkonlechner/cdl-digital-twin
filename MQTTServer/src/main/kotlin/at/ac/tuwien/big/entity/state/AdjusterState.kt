package at.ac.tuwien.big.entity.state

data class AdjusterState(
        override val name: String,
        val adjusterPosition: Double = 0.0
) : State
