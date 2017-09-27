package at.ac.tuwien.big.entity.state

/**
 * Describes the change from one state to another
 */
interface Transition {
    val startState: StateEvent
    val targetState: StateEvent
}