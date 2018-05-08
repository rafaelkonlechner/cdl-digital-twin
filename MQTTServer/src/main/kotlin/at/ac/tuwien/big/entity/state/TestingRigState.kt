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
) : StateEvent {

    override fun match(other: StateEvent, similar: (Double, Double) -> Boolean): Boolean {
        return if (other is TestingRigState) {
            val platform = if (other.platformPosition != null) {
                similar(this.platformPosition ?: 0.0, other.platformPosition)
            } else {
                true
            }
            val heatplate = if (other.heatplateTemperature != null) {
                similar(this.heatplateTemperature ?: 0.0, other.heatplateTemperature)
            } else {
                true
            }
            this.objectCategory == other.objectCategory && platform && heatplate
        } else {
            false
        }
    }
}
