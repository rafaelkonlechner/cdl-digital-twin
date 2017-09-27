package at.ac.tuwien.big.entity.state

/**
 * State change of the testing rig
 */
data class TestingRigTransition(
        override var startState: TestingRigState,
        override var targetState: TestingRigState
) : Transition
