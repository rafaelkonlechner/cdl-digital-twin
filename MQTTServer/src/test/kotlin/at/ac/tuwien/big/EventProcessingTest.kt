package at.ac.tuwien.big

import org.junit.Test

class EventProcessingTest {

    @Test
    fun basicCEPTest() {
        val cep = EventProcessing.setupCEP()
        cep.sendEvent(States.idle)
        cep.sendEvent(States.approach)
        cep.sendEvent(States.pickup)
        cep.sendEvent(States.lift)
        cep.sendEvent(States.park)
        cep.sendEvent(States.halfRelease)
        cep.sendEvent(States.fullRelease)
        cep.sendEvent(States.depositGreen)
        cep.sendEvent(States.releaseGreen)
    }
}
