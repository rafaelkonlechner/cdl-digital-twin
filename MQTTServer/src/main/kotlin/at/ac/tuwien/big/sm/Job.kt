package at.ac.tuwien.big.sm

import at.ac.tuwien.big.entity.state.RoboticArmState

data class Job(val name: String, val states: List<RoboticArmState>)