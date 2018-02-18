package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*

/**
 * Holds all defined states of the environment
 */
object StateMachine {
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
            "Conveyor Empty", adjusterPosition = 1.669
    )
    val conveyorObjectDetected = ConveyorState(
            "Conveyor Object Detected", adjusterPosition = 1.669, detected = true
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
    val none = TestingRigState("None", objectCategory = ObjectCategory.NONE)
    val green = TestingRigState("Green", objectCategory = ObjectCategory.GREEN)
    val red = TestingRigState("Red", objectCategory = ObjectCategory.RED)
    val tilted = TestingRigState("Tilted", platformPosition = -1.0)

    /*
     * StateEvent transitions
     */
    val slider_pushed = SliderTransition(sliderHomePosition, sliderPushedPosition)
    val slider_home = SliderTransition(sliderPushedPosition, sliderHomePosition)

    val adjuster_empty_detected = ConveyorTransition(conveyorEmpty, conveyorObjectDetected)
    val adjuster_empty_pickup = ConveyorTransition(conveyorEmpty, conveyorObjectInWindow)
    val adjuster_detected_pushed = ConveyorTransition(conveyorObjectDetected, conveyorAdjusterPushed)
    val adjuster_pushed_detected = ConveyorTransition(conveyorAdjusterPushed, conveyorObjectDetected)
    val adjuster_pushed_pickup = ConveyorTransition(conveyorAdjusterPushed, conveyorObjectInWindow)

    val none_green = TestingRigTransition(none, green)
    val none_red = TestingRigTransition(none, red)
    val to_none = TestingRigTransition(none, none)
    val red_tilt = TestingRigTransition(red, tilted)
    val green_tilt = TestingRigTransition(green, tilted)
    val tilted_none = TestingRigTransition(tilted, none)

    val idle_approach = RoboticArmTransition(idle, approach, mainArmSpeed = 0.6)
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

    /*
     * Maps of state names to states, per entity
     */
    val roboticArm = mapOf(
            Pair(idle.name, idle),
            Pair(approach.name, approach),
            Pair(pickup.name, pickup),
            Pair(lift.name, lift),
            Pair(park.name, park),
            Pair(halfRelease.name, halfRelease),
            Pair(fullRelease.name, fullRelease),
            Pair(wait.name, wait),
            Pair(retrieve.name, retrieve),
            Pair(retrieveGrip.name, retrieveGrip),
            Pair(depositGreen.name, depositGreen),
            Pair(releaseGreen.name, releaseGreen),
            Pair(depositRed.name, depositRed),
            Pair(releaseRed.name, releaseRed)
    )

    val slider = mapOf(
            Pair(sliderHomePosition.name, sliderHomePosition),
            Pair(sliderPushedPosition.name, sliderPushedPosition)
    )
    val conveyor = mapOf(
            Pair(conveyorEmpty.name, conveyorEmpty),
            Pair(conveyorObjectDetected.name, conveyorObjectDetected),
            Pair(conveyorObjectInWindow.name, conveyorObjectInWindow),
            Pair(conveyorAdjusterPushed.name, conveyorAdjusterPushed)
    )

    val testingRig = mapOf(
            Pair(none.name, none),
            Pair(green.name, green),
            Pair(red.name, red),
            Pair(tilted.name, tilted)
    )

    val all = roboticArm.values union slider.values union conveyor.values union testingRig.values

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    fun matchState(roboticArmState: RoboticArmState): RoboticArmState? {
        return roboticArm.values.filter { match(it, roboticArmState) }.firstOrNull()
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    fun matchState(sliderState: SliderState): SliderState? {
        return slider.values.filter { match(it, sliderState) }.firstOrNull()
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    fun matchState(conveyorState: ConveyorState): ConveyorState? {
        return conveyor.values.filter { match(it, conveyorState) }.firstOrNull()
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    fun matchState(testingRigState: TestingRigState): TestingRigState? {
        return testingRig.values.filter { match(it, testingRigState) }.firstOrNull()
    }

    /**
     * Matches two given states
     * @return true, if the states are equal with a certain tolerance for real values and false otherwise
     */
    private fun match(a: StateEvent, b: StateEvent): Boolean {
        return if (a is RoboticArmState && b is RoboticArmState) {
            similar(a.basePosition, b.basePosition)
                    && similar(a.mainArmPosition, b.mainArmPosition)
                    && similar(a.secondArmPosition, b.secondArmPosition)
                    && similar(a.wristPosition, b.wristPosition)
                    && similar(a.gripperPosition, b.gripperPosition)
        } else if (a is SliderState && b is SliderState) {
            similar(a.sliderPosition, b.sliderPosition)
        } else if (a is ConveyorState && b is ConveyorState) {
            similar(a.adjusterPosition, b.adjusterPosition)
                    && a.detected == b.detected
                    && a.inPickupWindow == b.inPickupWindow
        } else if (a is TestingRigState && b is TestingRigState) {
            a.objectCategory == b.objectCategory
                    && similar(a.platformPosition, b.platformPosition)
        } else {
            false
        }
    }

    /**
     * Equality check with an epsilon of 0.02
     * @return true, if the distance between the two real numbers is < 0.02 and false otherwise
     */
    private fun similar(a: Double, b: Double): Boolean {
        return Math.abs(a - b) <= 0.02
    }

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
                listOf("platform-goto ${transition.targetState.platformPosition}")
            }
            else -> emptyList()
        }
    }
}
