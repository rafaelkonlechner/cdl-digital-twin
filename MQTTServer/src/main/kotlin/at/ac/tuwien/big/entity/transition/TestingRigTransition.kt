package at.ac.tuwien.big.entity.transition

import at.ac.tuwien.big.entity.state.TestingRigState

/**
 * State change of the testing rig
 */
data class TestingRigTransition(
        override var startState: TestingRigState,
        override var targetState: TestingRigState
) : Transition
