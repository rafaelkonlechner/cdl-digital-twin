package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.BasicStateEvent
import org.junit.Test
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import at.ac.tuwien.big.entity.state.StateEvent

class StateMachineTest {

    @Test
    fun statesComplete() {
        val basicStateEventType: KType = StateEvent::class.createType()
        var builder = StringBuilder()

        StateMachine.States::class.declaredMemberProperties.forEach { it: KProperty1<StateMachine.States, *> ->
            if (it.returnType.isSubtypeOf(basicStateEventType)) {
                var value: StateEvent? = it.get(StateMachine.States) as StateEvent
                if (value != null && !StateMachine.all.contains( value)) {
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
        var valueA: Double = Double.NaN;
        var valueB: Double = Double.NaN;

        // comparing NaN is always false
        assert(!StateMachine.similar(valueA, valueB))

        valueA = Double.MIN_VALUE
        assert(!StateMachine.similar(valueA, valueB))

        valueB = Double.MIN_VALUE
        assert(StateMachine.similar(valueA, valueB))

        valueA = 0.0
        valueB = 0.1
        assert(!StateMachine.similar(valueA, valueB))

        valueA = 0.01
        valueB = 0.029
        assert(StateMachine.similar(valueA, valueB))


        valueA = 0.05
        valueB = 0.07
        assert(!StateMachine.similar(valueA, valueB))

    }
}

