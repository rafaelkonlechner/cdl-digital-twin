package at.ac.tuwien.big

import org.junit.Test

class StateMachineTest {

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

