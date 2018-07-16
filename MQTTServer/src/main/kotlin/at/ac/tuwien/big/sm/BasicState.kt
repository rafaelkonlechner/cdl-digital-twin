package at.ac.tuwien.big.sm

import at.ac.tuwien.big.entity.state.Environment

/**
 * Basic state in a state machine. [altEnvironment] defines an optional second matching environment, which can be used to
 * define a condition for a choice state. [altEnvironment] is only considered, if the successor state is a [ChoiceState].
 */
data class BasicState(
        override var name: String = "Snapshot",
        override var type: String = "BasicState",
        var sensor: String? = null,
        var environment: Environment = Environment(),
        var altEnvironment: Environment? = null
) : StateBase()