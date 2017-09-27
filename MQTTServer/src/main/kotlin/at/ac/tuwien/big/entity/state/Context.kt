package at.ac.tuwien.big.entity.state

/**
 * Encapsulates all signals of the environment at one point in time
 */
data class Context(
        var roboticArmState: RoboticArmState? = null,
        var sliderState: SliderState? = null,
        var conveyorState: ConveyorState? = null,
        var testingRigState: TestingRigState? = null
)
