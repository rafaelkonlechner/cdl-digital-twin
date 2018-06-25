package at.ac.tuwien.big.sm

import at.ac.tuwien.big.entity.state.Environment

data class BasicState(
        override var name: String = "Snapshot",
        override var type: String = "BasicState",
        var environment: Environment = Environment(),
        var altEnvironment: Environment? = null
) : StateBase()