package at.ac.tuwien.big.entity.state

data class Context(
        var roboticArmState: RoboticArmState? = null,
        var sliderState: SliderState? = null,
        var conveyorState: ConveyorState? = null,
        var testingRigState: TestingRigState? = null
)
