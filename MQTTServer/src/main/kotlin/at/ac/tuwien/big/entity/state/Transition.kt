package at.ac.tuwien.big.entity.state

interface Transition {
    val startState: StateEvent
    val targetState: StateEvent
}