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
        cep.sendEvent(States.idle)
        cep.sendEvent(States.approach)
        cep.sendEvent(States.pickup)
        cep.sendEvent(States.lift)
        cep.sendEvent(States.park)
        cep.sendEvent(States.halfRelease)
        cep.sendEvent(States.fullRelease)
        cep.sendEvent(States.depositGreen)
        cep.sendEvent(States.releaseGreen)

        println(TimeSeriesCollectionService.getSuccessfulProductions(Duration.ofHours(1), Duration.ofHours(1)))
    }
}
