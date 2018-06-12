package at.ac.tuwien.big.sm

import at.ac.tuwien.big.entity.state.RoboticArmState

data class Job(
        val id: String = "",
        var name: String = "",
        val states: MutableList<RoboticArmState> = mutableListOf()
)