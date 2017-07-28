package at.ac.tuwien.big.entity.state

data class RoboticArmState (
        override var name: String = "Snapshot",
        override var entity: String = "RoboticArm",
        var basePosition: Double = 0.0,
        var mainArmPosition: Double = 0.0,
        var secondArmPosition: Double = 0.0,
        var wristPosition: Double = 0.0,
        var gripperPosition: Double = 0.0
) : State
