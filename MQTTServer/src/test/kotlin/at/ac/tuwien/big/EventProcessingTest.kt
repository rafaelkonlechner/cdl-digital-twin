package at.ac.tuwien.big

import org.junit.Ignore
import org.junit.Test
import java.time.Duration

class EventProcessingTest {

    @Test
    @Ignore
    fun basicCEPTest() {
        val cep = EventProcessing.runtime
        cep.sendEvent(StateMachine.States.idle)
        cep.sendEvent(StateMachine.States.approach)
        cep.sendEvent(StateMachine.States.pickup)
        cep.sendEvent(StateMachine.States.lift)
        cep.sendEvent(StateMachine.States.park)
        cep.sendEvent(StateMachine.States.halfRelease)
        cep.sendEvent(StateMachine.States.fullRelease)
        cep.sendEvent(StateMachine.States.depositGreen)
        cep.sendEvent(StateMachine.States.releaseGreen)

        println(TimeSeriesCollectionService.getSuccessfulProductions(Duration.ofHours(1), Duration.ofHours(1)))
    }
}
