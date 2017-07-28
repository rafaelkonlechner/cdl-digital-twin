package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.ConveyorState
import at.ac.tuwien.big.entity.state.RoboticArmState
import at.ac.tuwien.big.entity.state.SliderState
import com.espertech.esper.client.Configuration
import com.espertech.esper.client.EPRuntime
import com.espertech.esper.client.EPServiceProviderManager

object EventProcessing {

    fun setupCEP(): EPRuntime {
        val config = Configuration()
        config.addEventType("RoboticArmState", RoboticArmState::class.java.name)
        config.addEventType("SliderState", SliderState::class.java.name)
        config.addEventType("ConveyorState", ConveyorState::class.java.name)
        val cep = EPServiceProviderManager.getProvider("CEPEngine", config)
        val runtime = cep.epRuntime
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

        sensorUpdates.addListener { newEvents, oldEvents ->
            run {
                println("Match:")
                println(newEvents)
                println(oldEvents)
            }
        }
        return runtime
    }
}