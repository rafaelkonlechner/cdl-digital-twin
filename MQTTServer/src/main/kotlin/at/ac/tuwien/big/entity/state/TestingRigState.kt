package at.ac.tuwien.big.entity.state

data class TestingRigState(
        override var name: String = "Snapshot",
        override var entity: String = "TestingRig",
        var platformPosition: Double = 0.0,
        var objectCategory: ObjectCategory = ObjectCategory.NONE
) : State
