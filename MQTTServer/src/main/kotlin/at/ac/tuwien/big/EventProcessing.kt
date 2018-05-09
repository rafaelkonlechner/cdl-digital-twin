package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import com.espertech.esper.client.Configuration
import com.espertech.esper.client.EPRuntime
import com.espertech.esper.client.EPServiceProviderManager
import com.espertech.esper.client.EventBean

/**
 * Service for complex event processing
 */
class EventProcessing {

    private val runtime: EPRuntime
    private val subscribers = mutableListOf<(Array<out EventBean>, Array<out EventBean>) -> Unit>()

    init {
        val config = Configuration()
        config.addEventType("RoboticArmState", RoboticArmState::class.java.name)
        config.addEventType("SliderState", SliderState::class.java.name)
        config.addEventType("ConveyorState", ConveyorState::class.java.name)
        config.addEventType("Gate", GatePassed::class.java.name)
        val cep = EPServiceProviderManager.getProvider("CEPEngine", config)
        runtime = cep.epRuntime
        val cepAdm = cep.epAdministrator

        val sensorUpdates = cepAdm.createEPL(
                """
            select * from RoboticArmState
            match_recognize (
                measures IDLE.entity as a_name, APPROACH.entity as b_name
                pattern (IDLE APPROACH ARBITRARY* ( DEPOSIT_GREEN RELEASE_GREEN | DEPOSIT_RED RELEASE_RED))
                define
                IDLE as IDLE.name = "Idle",
                APPROACH as APPROACH.name = "Approach",
                ARBITRARY as ARBITRARY.entity = "RoboticArm",
                DEPOSIT_GREEN as DEPOSIT_GREEN.name = "Deposit Green",
                RELEASE_GREEN as RELEASE_GREEN.name = "Release Green",
                DEPOSIT_RED as DEPOSIT_RED.name = "Deposit Red",
                RELEASE_RED as RELEASE_RED.name = "Release Red"
            )
            """)

        /*
         * Register event listeners here
         */
        sensorUpdates.addListener { new, old ->
            run {
                for (callback in subscribers) {
                    callback(new, old)
                }
            }
        }
    }

    fun subscribe(callback: (Array<out EventBean>, Array<out EventBean>) -> Unit) {
        subscribers.add(callback)
    }

    /**
     * Submit event to the CEP engine
     */
    fun submitEvent(event: StateEvent) {
        runtime.sendEvent(event)
    }
}