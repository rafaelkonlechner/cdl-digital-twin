package at.ac.tuwien.big.entity.state

/**
 * State of the testing rig
 */
data class TestingRigState(
        override var name: String = "Snapshot",
        override var entity: String = "TestingRig",
        val objectCategory: ObjectCategory? = null,
        val platformPosition: Double? = null,
        val heatplateTemperature: Double? = null,
        val criterion: (TestingRigState) -> Boolean = { _ -> true }
) : StateEvent
