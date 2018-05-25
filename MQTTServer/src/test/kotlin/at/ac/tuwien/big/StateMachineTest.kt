package at.ac.tuwien.big

import at.ac.tuwien.big.StateMachineSimulation.States.all
import at.ac.tuwien.big.entity.state.StateEvent
import org.junit.Test
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf

class StateMachineTest {

    @Test
    fun statesComplete() {
        val basicStateEventType: KType = StateEvent::class.createType()
        val builder = StringBuilder()

        StateMachineSimulation.States::class.declaredMemberProperties.forEach { it: KProperty1<StateMachineSimulation.States, *> ->
            if (it.returnType.isSubtypeOf(basicStateEventType)) {
                val value: StateEvent? = it.get(StateMachineSimulation.States) as StateEvent
                if (value != null && !all.contains(value)) {
                    builder.appendln("Missing state $value")
                }
            }
        }

        assert(builder.isEmpty()) {
            builder.toString()
        }
    }

    @Test
    fun similar() {
        var valueA: Double = Double.NaN
        var valueB: Double = Double.NaN

        // comparing NaN is always false
        assert(!similar(valueA, valueB, 0.02))

        valueA = Double.MIN_VALUE
        assert(!similar(valueA, valueB, 0.02))

        valueB = Double.MIN_VALUE
        assert(similar(valueA, valueB, 0.02))

        valueA = 0.0
        valueB = 0.1
        assert(!similar(valueA, valueB, 0.02))

        valueA = 0.01
        valueB = 0.029
        assert(similar(valueA, valueB, 0.02))

        valueA = 0.05
        valueB = 0.07
        assert(!similar(valueA, valueB, 0.02))
    }
}
