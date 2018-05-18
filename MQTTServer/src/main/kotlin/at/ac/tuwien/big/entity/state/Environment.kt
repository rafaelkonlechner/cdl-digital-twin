package at.ac.tuwien.big.entity.state

/**
 * Encapsulates all signals of the environment at one point in time
 */
data class Environment(
        val roboticArmState: RoboticArmState? = null,
        val sliderState: SliderState? = null,
        val conveyorState: ConveyorState? = null,
        val testingRigState: TestingRigState? = null
) {
    infix fun matches(next: Environment): Boolean {
        return (next.roboticArmState == null || this.roboticArmState == next.roboticArmState)
                && (next.conveyorState == null || this.conveyorState == next.conveyorState)
                && (next.testingRigState == null || this.testingRigState == next.testingRigState)
                && (next.sliderState == null || this.sliderState == next.sliderState)
    }
}
