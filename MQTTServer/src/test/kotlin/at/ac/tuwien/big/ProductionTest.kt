package at.ac.tuwien.big

import at.ac.tuwien.big.entity.transition.Transition
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class ProductionTest {

    @Autowired
    lateinit var messageController: MessageController

    @Test
    fun testProduction() {

        val production = mutableListOf<Pair<Transition, Long>>()

        production.add(Pair(StateMachine.Transitions.idle_approach, 3000))
        production.add(Pair(StateMachine.Transitions.approach_pickup, 4000))
        production.add(Pair(StateMachine.Transitions.pickup_lift, 200))
        production.add(Pair(StateMachine.Transitions.lift_park, 4000))
        production.add(Pair(StateMachine.Transitions.slider_pushed, 0))
        production.add(Pair(StateMachine.Transitions.park_halfrelease, 3000))
        production.add(Pair(StateMachine.Transitions.halfrelease_fullrelease, 4000))
        production.add(Pair(StateMachine.Transitions.fullrelease_wait, 4000))
        production.add(Pair(StateMachine.Transitions.wait_retrieve, 4000))
        production.add(Pair(StateMachine.Transitions.retrieve_retrievegrip, 5000))
        production.add(Pair(StateMachine.Transitions.retrievegrip_depositgreen, 3000))
        production.add(Pair(StateMachine.Transitions.depositgreen_releasegreen, 1000))
        production.add(Pair(StateMachine.Transitions.releasegreen_idle, 0))
        production.add(Pair(StateMachine.Transitions.slider_home, 0))

        for ((first, second) in production) {
            messageController.sendMQTTTransitionCommand(first)
            Thread.sleep(second)
        }
        for ((first, second) in production) {
            messageController.sendMQTTTransitionCommand(first)
            Thread.sleep(second)
        }
    }
}
