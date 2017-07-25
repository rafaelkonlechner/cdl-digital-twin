package at.ac.tuwien.big.entity.state

interface Transition {
    val startState: State
    val targetState: State
}