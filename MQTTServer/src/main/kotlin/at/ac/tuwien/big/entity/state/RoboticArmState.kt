package at.ac.tuwien.big.entity.state

/**
 * State of the robotic arm
 */
data class RoboticArmState(
        override var name: String = "Snapshot",
        override var entity: String = "RoboticArm",
        var basePosition: Double = 0.0,
        var mainArmPosition: Double = 0.0,
        var secondArmPosition: Double = 0.0,
        var secondArmRotation: Double = 0.0,
        var headMountPosition: Double = 0.0,
        var headPosition: Double = 0.0,
        var gripperPosition: Double = 0.0,
        var gripperHasContact: Boolean = false,
        var handPosition: Point3D? = null
) : StateEvent {
    override fun match(other: StateEvent, similar: (Double, Double) -> Boolean): Boolean {
        return if (other is RoboticArmState) {
            similar(this.basePosition, other.basePosition)
                    && similar(this.mainArmPosition, other.mainArmPosition)
                    && similar(this.secondArmPosition, other.secondArmPosition)
                    && similar(this.secondArmRotation, other.secondArmRotation)
                    && similar(this.headMountPosition, other.headMountPosition)
                    && similar(this.headPosition, other.headPosition)
                    && similar(this.gripperPosition, other.gripperPosition)
        } else {
            false
        }
    }
}
