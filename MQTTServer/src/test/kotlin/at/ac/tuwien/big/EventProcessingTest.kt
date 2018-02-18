package at.ac.tuwien.big

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.Duration

@RunWith(SpringRunner::class)
@SpringBootTest
class EventProcessingTest {

    @Test
    fun basicCEPTest() {
        val cep = EventProcessing.runtime
        cep.sendEvent(StateMachine.idle)
        cep.sendEvent(StateMachine.approach)
        cep.sendEvent(StateMachine.pickup)
        cep.sendEvent(StateMachine.lift)
        cep.sendEvent(StateMachine.park)
        cep.sendEvent(StateMachine.halfRelease)
        cep.sendEvent(StateMachine.fullRelease)
        cep.sendEvent(StateMachine.depositGreen)
        cep.sendEvent(StateMachine.releaseGreen)

        println(TimeSeriesCollectionService.getSuccessfulProductions(Duration.ofHours(1), Duration.ofHours(1)))
    }
}
