package at.ac.tuwien.big.entity.state

data class TestingRigTransition(
        override var startState: TestingRigState,
        override var targetState: TestingRigState
) : Transition
