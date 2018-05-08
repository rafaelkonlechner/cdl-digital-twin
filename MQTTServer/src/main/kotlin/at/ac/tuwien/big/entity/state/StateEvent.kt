package at.ac.tuwien.big.entity.state

/**
 * Encapsulates sensor signals of one entity at one point in time
 */
interface StateEvent {
    var name: String
    var entity: String

    fun match(other: StateEvent, similar: (Double, Double) -> Boolean): Boolean
}
