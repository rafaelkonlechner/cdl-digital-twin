package at.ac.tuwien.big.entity.state

data class RoboticArmState(
        override val name: String,
        val basePosition: Double = 0.0,
        val mainArmPosition: Double = 0.0,
        val secondArmPosition: Double = 0.0,
        val wristPosition: Double = 0.0,
        val gripperPosition: Double = 0.0
) : State
