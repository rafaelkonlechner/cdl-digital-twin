package at.ac.tuwien.big.sm

data class Job(
        val id: String = "",
        var name: String = "",
        val target: String = "",
        val states: MutableList<StateBase> = mutableListOf()
)