package at.ac.tuwien.big.entity.state

import at.ac.tuwien.big.doubleAccuracy
import at.ac.tuwien.big.singleAccuracy

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
        return (next.roboticArmState == null || this.roboticArmState?.match(next.roboticArmState, doubleAccuracy) ?: true)
                && (next.conveyorState == null || this.conveyorState?.match(next.conveyorState, doubleAccuracy) ?: true)
                && (next.testingRigState == null || this.testingRigState?.match(next.testingRigState, singleAccuracy) ?: true)
                && (next.sliderState == null || this.sliderState?.match(next.sliderState, doubleAccuracy) ?: true)
    }
}
