package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
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

    val idle = RoboticArmState("Idle")
    val approach = RoboticArmState(
            name = "Approach",
            basePosition = 0.0,
            mainArmPosition = 1.50,
            secondArmPosition = -0.12,
            wristPosition = 0.0,
            gripperPosition = 1.5)
    val pickup = RoboticArmState(
            name = "Pickup",
            basePosition = 0.0,
            mainArmPosition = 1.50,
            secondArmPosition = -0.12,
            wristPosition = 0.0,
            gripperPosition = -0.40
    )
    val lift = RoboticArmState(
            name = "Lift",
            basePosition = 0.0,
            mainArmPosition = 1.315,
            secondArmPosition = -0.12,
            wristPosition = 0.0,
            gripperPosition = -0.40
    )
    val park = RoboticArmState(
            name = "Park",
            basePosition = 3.142,
            mainArmPosition = 1.40,
            secondArmPosition = -1.55,
            wristPosition = -1.5,
            gripperPosition = -0.40
    )
    val halfRelease = RoboticArmState(
            name = "Half Release",
            basePosition = 3.142,
            mainArmPosition = 1.36,
            secondArmPosition = -1.34,
            wristPosition = -1.5,
            gripperPosition = -0.2
    )
    val fullRelease = RoboticArmState(
            name = "Full Release",
            basePosition = 3.142,
            mainArmPosition = 1.36,
            secondArmPosition = -1.334,
            wristPosition = -1.5,
            gripperPosition = 1.0
    )
    val wait = RoboticArmState(
            name = "Wait",
            basePosition = 3.142,
            mainArmPosition = 0.0,
            secondArmPosition = 0.0,
            wristPosition = -1.5,
            gripperPosition = 1.0
    )
    val retrieve = RoboticArmState(
            name = "Retrieve",
            basePosition = 3.142,
            mainArmPosition = 1.22,
            secondArmPosition = -1.23,
            wristPosition = -1.5,
            gripperPosition = 1.0
    )
    val retrieveGrip = RoboticArmState(
            name = "Retrieve Grip",
            basePosition = 3.142,
            mainArmPosition = 1.22,
            secondArmPosition = -1.23,
            wristPosition = -1.5,
            gripperPosition = -0.4
    )
    val depositGreen = RoboticArmState(
            name = "Deposit Green",
            basePosition = -1.745,
            mainArmPosition = 0.942,
            secondArmPosition = -0.89,
            wristPosition = 1.5,
            gripperPosition = -0.4
    )
    val releaseGreen = RoboticArmState(
            name = "Release Green",
            basePosition = -1.745,
            mainArmPosition = 0.942,
            secondArmPosition = -0.89,
            wristPosition = 1.5,
            gripperPosition = 0.5
    )
    val depositRed = RoboticArmState(
            name = "Deposit Red",
            basePosition = -1.449,
            mainArmPosition = 0.942,
            secondArmPosition = -0.89,
            wristPosition = 1.5,
            gripperPosition = -0.4
    )
    val releaseRed = RoboticArmState(
            name = "Release Red",
            basePosition = -1.449,
            mainArmPosition = 0.942,
            secondArmPosition = -0.89,
            wristPosition = 1.5,
            gripperPosition = 0.5
    )

    val sliderHomePosition = SliderState(
            "Slider Home", sliderPosition = 0.08
    )
    val sliderPushedPosition = SliderState(
            "Slider Push", sliderPosition = 0.42
    )
    val adjusterHomePosition = AdjusterState(
            "Adjuster Home", adjusterPosition = 1.669
    )
    val adjusterPushedPosition = AdjusterState(
            "Adjuster Push", adjusterPosition = 1.909
    )

    fun transform(transition: Transition): List<String> {
        return when (transition) {
            is RoboticArmTransition -> {
                val target = transition.targetState
                listOf(
                        "base-goto ${target.basePosition} ${transition.baseSpeed}",
                        "main-arm-goto ${target.mainArmPosition} ${transition.mainArmSpeed}",
                        "second-arm-goto ${target.secondArmPosition} ${transition.secondArmSpeed}",
                        "wrist-goto ${target.wristPosition}",
                        "gripper-goto ${target.gripperPosition}"
                )
            }
            is SliderTransition -> {
                listOf("slider-goto ${transition.targetState.sliderPosition}")
            }
            is AdjusterTransition -> {
                listOf("adjuster-goto ${transition.targetState.adjusterPosition}")
            }
            else -> emptyList()
        }
    }

    @Test
    fun testProduction() {

        val slider_pushed = SliderTransition(sliderHomePosition, sliderPushedPosition)
        val slider_home = SliderTransition(sliderPushedPosition, sliderHomePosition)
        val adjuster_pushed = AdjusterTransition(adjusterHomePosition, adjusterPushedPosition)
        val adjuster_home = AdjusterTransition(adjusterPushedPosition, adjusterHomePosition)

        val idle_approach = RoboticArmTransition(idle, approach)
        val approach_pickup = RoboticArmTransition(approach, pickup)
        val pickup_lift = RoboticArmTransition(pickup, lift, mainArmSpeed = 0.2, secondArmSpeed = 0.2)
        val lift_park = RoboticArmTransition(lift, park)
        val park_halfrelease = RoboticArmTransition(park, halfRelease, mainArmSpeed = 0.1, secondArmSpeed = 0.1)
        val halfrelease_fullrelease = RoboticArmTransition(halfRelease, fullRelease, mainArmSpeed = 0.1, secondArmSpeed = 0.1)
        val fullrelease_wait = RoboticArmTransition(fullRelease, wait)
        val wait_retrieve = RoboticArmTransition(wait, retrieve)
        val retrieve_retrievegrip = RoboticArmTransition(retrieve, retrieveGrip)
        val retrievegrip_depositgreen = RoboticArmTransition(retrieveGrip, depositGreen)
        val retrievegrip_depositred = RoboticArmTransition(retrieveGrip, depositRed)
        val depositgreen_releasegreen = RoboticArmTransition(depositGreen, releaseGreen)
        val depositred_releasered = RoboticArmTransition(depositRed, releaseRed)
        val releasered_idle = RoboticArmTransition(releaseRed, idle)
        val releasegreen_idle = RoboticArmTransition(releaseGreen, idle)

        val production = mutableListOf<Pair<Transition, Long>>()

        production.add(Pair(adjuster_pushed, 2000))
        production.add(Pair(adjuster_home, 500))
        production.add(Pair(idle_approach, 3000))
        production.add(Pair(approach_pickup, 4000))
        production.add(Pair(pickup_lift, 200))
        production.add(Pair(lift_park, 4000))
        production.add(Pair(slider_pushed, 0))
        production.add(Pair(park_halfrelease, 3000))
        production.add(Pair(halfrelease_fullrelease, 4000))
        production.add(Pair(fullrelease_wait, 4000))
        production.add(Pair(wait_retrieve, 4000))
        production.add(Pair(retrieve_retrievegrip, 5000))
        production.add(Pair(retrievegrip_depositgreen, 3000))
        production.add(Pair(depositgreen_releasegreen, 1000))
        production.add(Pair(releasegreen_idle, 0))
        production.add(Pair(slider_home, 0))

        for (p in production) {
            for (m in transform(p.first)) {
                messageController.sendMQTTMessageActuator(m)
            }
            Thread.sleep(p.second)
        }
        for (p in production) {
            for (m in transform(p.first)) {
                messageController.sendMQTTMessageActuator(m)
            }
            Thread.sleep(p.second)
        }
    }
}
