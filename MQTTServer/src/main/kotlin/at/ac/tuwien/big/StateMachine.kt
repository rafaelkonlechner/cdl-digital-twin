package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.entity.transition.*

/**
 * Holds all defined states and transitions of the environment
 */
object StateMachine {

    object States {
        /*
         * Robotic arm states
         */
        val idle = RoboticArmState(name = "Idle")
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
                mainArmPosition = 1.30,
                secondArmPosition = -1.30,
                wristPosition = -1.5,
                gripperPosition = 1.0
        )
        val retrieveGrip = RoboticArmState(
                name = "Retrieve Grip",
                basePosition = 3.142,
                mainArmPosition = 1.30,
                secondArmPosition = -1.30,
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

        /*
         * Slider states
         */
        val sliderHomePosition = SliderState(
                "Slider Home", sliderPosition = 0.08
        )
        val sliderPushedPosition = SliderState(
                "Slider Push", sliderPosition = 0.42
        )

        /*
         * Conveyor states
         */
        val conveyorEmpty = ConveyorState(
                "Conveyor Empty", adjusterPosition = 1.669, detected = false, inPickupWindow = false
        )
        val conveyorObjectDetected = ConveyorState(
                "Conveyor Object Detected", adjusterPosition = 1.669, detected = true, inPickupWindow = false
        )
        val conveyorObjectInWindow = ConveyorState(
                "Conveyor Object In Window", adjusterPosition = 1.669, detected = true, inPickupWindow = true
        )
        val conveyorAdjusterPushed = ConveyorState(
                "Conveyor Adjuster Push", adjusterPosition = 1.909, detected = true, inPickupWindow = true
        )

        /*
         * Testing rig states
         */
        val none = TestingRigState("None", objectCategory = ObjectCategory.NONE, criterion = { _ -> true })
        val green = TestingRigState("Green", objectCategory = ObjectCategory.GREEN, criterion = { x ->
            x.heatplateTemperature != null && !similar(x.heatplateTemperature, 130.0, 0.1)
        })
        val red = TestingRigState("Red", objectCategory = ObjectCategory.RED, criterion = { x ->
            x.heatplateTemperature != null && !similar(x.heatplateTemperature, 150.0, 0.1)
        })
        val green_heated = TestingRigState("Green Heated", objectCategory = ObjectCategory.GREEN, heatplateTemperature = 130.0, criterion = { x -> x.heatplateTemperature != null })
        val red_heated = TestingRigState("Red Heated", objectCategory = ObjectCategory.RED, heatplateTemperature = 150.0, criterion = { x -> x.heatplateTemperature != null })
        val tilted = TestingRigState("Tilted", platformPosition = -1.0, criterion = { _ -> true })
    }

    object Transitions {
        /*
        * StateEvent transitions
        */
        val slider_pushed = SliderTransition(States.sliderHomePosition, States.sliderPushedPosition)
        val slider_home = SliderTransition(States.sliderPushedPosition, States.sliderHomePosition)

        val adjuster_empty_detected = ConveyorTransition(States.conveyorEmpty, States.conveyorObjectDetected)
        val adjuster_empty_pickup = ConveyorTransition(States.conveyorEmpty, States.conveyorObjectInWindow)
        val adjuster_detected_pushed = ConveyorTransition(States.conveyorObjectDetected, States.conveyorAdjusterPushed)
        val adjuster_pushed_detected = ConveyorTransition(States.conveyorAdjusterPushed, States.conveyorObjectDetected)
        val adjuster_pushed_pickup = ConveyorTransition(States.conveyorAdjusterPushed, States.conveyorObjectInWindow)

        val none_green = TestingRigTransition(States.none, States.green)
        val none_red = TestingRigTransition(States.none, States.red)
        val to_none = TestingRigTransition(States.none, States.none)
        val green_heatup = TestingRigTransition(States.green, States.green_heated)
        val red_heatup = TestingRigTransition(States.red, States.red_heated)
        val red_tilt = TestingRigTransition(States.red, States.tilted)
        val green_tilt = TestingRigTransition(States.green, States.tilted)
        val tilted_none = TestingRigTransition(States.tilted, States.none)

        val idle_approach = RoboticArmTransition(States.idle, States.approach, mainArmSpeed = 0.6)
        val approach_pickup = RoboticArmTransition(States.approach, States.pickup)
        val pickup_lift = RoboticArmTransition(States.pickup, States.lift, mainArmSpeed = 0.2, secondArmSpeed = 0.2)
        val lift_park = RoboticArmTransition(States.lift, States.park)
        val park_halfrelease = RoboticArmTransition(States.park, States.halfRelease, mainArmSpeed = 0.1, secondArmSpeed = 0.1)
        val halfrelease_fullrelease = RoboticArmTransition(States.halfRelease, States.fullRelease, mainArmSpeed = 0.1, secondArmSpeed = 0.1)
        val fullrelease_wait = RoboticArmTransition(States.fullRelease, States.wait)
        val wait_retrieve = RoboticArmTransition(States.wait, States.retrieve)
        val retrieve_retrievegrip = RoboticArmTransition(States.retrieve, States.retrieveGrip)
        val retrievegrip_depositgreen = RoboticArmTransition(States.retrieveGrip, States.depositGreen)
        val retrievegrip_depositred = RoboticArmTransition(States.retrieveGrip, States.depositRed)
        val depositgreen_releasegreen = RoboticArmTransition(States.depositGreen, States.releaseGreen)
        val depositred_releasered = RoboticArmTransition(States.depositRed, States.releaseRed)
        val releasered_idle = RoboticArmTransition(States.releaseRed, States.idle)
        val releasegreen_idle = RoboticArmTransition(States.releaseGreen, States.idle)
    }

    /*
     * Maps of state names to states, per entity
     */
    val roboticArm = createMap(States.idle,
            States.approach,
            States.pickup,
            States.lift,
            States.park,
            States.halfRelease,
            States.fullRelease,
            States.wait,
            States.retrieve,
            States.retrieveGrip,
            States.depositGreen,
            States.releaseGreen,
            States.depositRed,
            States.releaseRed
    )

    val slider = createMap(States.sliderHomePosition, States.sliderPushedPosition)

    val conveyor = createMap(States.conveyorEmpty, States.conveyorObjectDetected, States.conveyorObjectInWindow, States.conveyorAdjusterPushed)

    val testingRig = createMap(States.none, States.green, States.red, States.green_heated, States.red_heated, States.tilted)

    val all = roboticArm.values union slider.values union conveyor.values union testingRig.values

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    fun matchState(roboticArmState: RoboticArmState): RoboticArmState? {
        return roboticArm.values.firstOrNull { match(roboticArmState, it) }
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    fun matchState(sliderState: SliderState): SliderState? {
        return slider.values.firstOrNull { match(sliderState, it) }
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    fun matchState(conveyorState: ConveyorState): ConveyorState? {
        return conveyor.values.firstOrNull { match(conveyorState, it) }
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    fun matchState(testingRigState: TestingRigState): TestingRigState? {
        return testingRig.values
                .filter { match(testingRigState, it) }
                .find { it.criterion(testingRigState) }
    }

    private fun <T : StateEvent> createMap(vararg states: T) = states.map { Pair(it.name, it) }.toMap()

    /**
     * Matches two given states
     * @return true, if the states are equal with a certain tolerance for real values and false otherwise
     */
    private fun match(a: StateEvent, b: StateEvent): Boolean {
        return if (a is RoboticArmState && b is RoboticArmState) {
            similar(a.basePosition, b.basePosition, 0.02)
                    && similar(a.mainArmPosition, b.mainArmPosition, 0.02)
                    && similar(a.secondArmPosition, b.secondArmPosition, 0.02)
                    && similar(a.wristPosition, b.wristPosition, 0.02)
                    && similar(a.gripperPosition, b.gripperPosition, 0.02)
        } else if (a is SliderState && b is SliderState) {
            similar(a.sliderPosition, b.sliderPosition, 0.02)
        } else if (a is ConveyorState && b is ConveyorState) {
            if (b.adjusterPosition != null) {
                similar(a.adjusterPosition ?: 0.0, b.adjusterPosition, 0.02)
            } else {
                true
            }
                    && a.detected == b.detected
                    && a.inPickupWindow == b.inPickupWindow
        } else if (a is TestingRigState && b is TestingRigState) {
            a.objectCategory == b.objectCategory
                    && if (b.platformPosition != null) {
                similar(a.platformPosition ?: 0.0, b.platformPosition, 0.02)
            } else {
                true
            }
                    && if (b.heatplateTemperature != null) {
                similar(a.heatplateTemperature ?: 0.0, b.heatplateTemperature, 0.1)
            } else {
                true
            }
        } else {
            false
        }
    }

    /**
     * Equality check with an epsilon of 0.02
     * @return true, if the distance between the two real numbers is < 0.02 and false otherwise
     */
    fun similar(a: Double, b: Double, accuracy: Double) = Math.abs(a - b) <= accuracy

    /**
     * Transform transitions into 'goto' commands for the MQTT API
     */
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
            is ConveyorTransition -> {
                listOf("adjuster-goto ${transition.targetState.adjusterPosition}")
            }
            is TestingRigTransition -> {
                val t = transition.targetState
                listOf(
                        if (t.platformPosition != null) {
                            "platform-goto ${t.platformPosition}"
                        } else {
                            ""
                        },
                        if (t.heatplateTemperature != null) {
                            "platform-heatup ${t.heatplateTemperature}"
                        } else {
                            ""
                        }
                ).filter { !it.isEmpty() }
            }
            else -> emptyList()
        }
    }
}
