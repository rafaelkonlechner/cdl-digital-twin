package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*

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
    val roboticArm = mapOf(
            Pair(States.idle.name, States.idle),
            Pair(States.approach.name, States.approach),
            Pair(States.pickup.name, States.pickup),
            Pair(States.lift.name, States.lift),
            Pair(States.park.name, States.park),
            Pair(States.halfRelease.name, States.halfRelease),
            Pair(States.fullRelease.name, States.fullRelease),
            Pair(States.wait.name, States.wait),
            Pair(States.retrieve.name, States.retrieve),
            Pair(States.retrieveGrip.name, States.retrieveGrip),
            Pair(States.depositGreen.name, States.depositGreen),
            Pair(States.releaseGreen.name, States.releaseGreen),
            Pair(States.depositRed.name, States.depositRed),
            Pair(States.releaseRed.name, States.releaseRed)
    )

    val slider = mapOf(
            Pair(States.sliderHomePosition.name, States.sliderHomePosition),
            Pair(States.sliderPushedPosition.name, States.sliderPushedPosition)
    )
    val conveyor = mapOf(
            Pair(States.conveyorEmpty.name, States.conveyorEmpty),
            Pair(States.conveyorObjectDetected.name, States.conveyorObjectDetected),
            Pair(States.conveyorObjectInWindow.name, States.conveyorObjectInWindow),
            Pair(States.conveyorAdjusterPushed.name, States.conveyorAdjusterPushed)
    )

    val testingRig = mapOf(
            Pair(States.none.name, States.none),
            Pair(States.green.name, States.green),
            Pair(States.red.name, States.red),
            Pair(States.tilted.name, States.tilted)
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
    internal fun similar(a: Double, b: Double): Boolean {
        return if (a == b) true else Math.abs(a - b) < 0.02
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
