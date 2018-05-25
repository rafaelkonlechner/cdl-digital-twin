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
        cep.submitEvent(StateMachineSimulation.States.idle)
        cep.submitEvent(StateMachineSimulation.States.approach)
        cep.submitEvent(StateMachineSimulation.States.pickup)
        cep.submitEvent(StateMachineSimulation.States.lift)
        cep.submitEvent(StateMachineSimulation.States.park)
        cep.submitEvent(StateMachineSimulation.States.halfRelease)
        cep.submitEvent(StateMachineSimulation.States.fullRelease)
        cep.submitEvent(StateMachineSimulation.States.depositGreen)
        cep.submitEvent(StateMachineSimulation.States.releaseGreen)
    }
}
