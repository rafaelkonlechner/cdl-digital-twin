package at.ac.tuwien.big

import org.junit.Ignore
import org.junit.Test

class EventProcessingTest {

    @Test
    @Ignore
    fun basicCEPTest() {
        val cep = EventProcessing()
        cep.subscribe({ new, old ->
            run {
                println(new)
            }
        })
        cep.submitEvent(StateMachine.States.idle)
        cep.submitEvent(StateMachine.States.approach)
        cep.submitEvent(StateMachine.States.pickup)
        cep.submitEvent(StateMachine.States.lift)
        cep.submitEvent(StateMachine.States.park)
        cep.submitEvent(StateMachine.States.halfRelease)
        cep.submitEvent(StateMachine.States.fullRelease)
        cep.submitEvent(StateMachine.States.depositGreen)
        cep.submitEvent(StateMachine.States.releaseGreen)
    }
}
