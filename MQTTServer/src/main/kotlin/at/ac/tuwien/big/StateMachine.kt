package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.entity.transition.*
import at.ac.tuwien.big.StateMachine.States as s
import at.ac.tuwien.big.StateMachine.Transitions as t

/**
 * Holds all defined states and successor of the environment
 */
object StateMachine {

    object States {
        /*
         * Robotic arm states
         */
        val idle = RoboticArmState(name = "Idle")
        val approach = RoboticArmState(
                name = "Drive Down",
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
        val roboticArm = createMap(s.idle,
                s.approach,
                s.pickup,
                s.lift,
                s.park,
                s.halfRelease,
                s.fullRelease,
                s.wait,
                s.retrieve,
                s.retrieveGrip,
                s.depositGreen,
                s.releaseGreen,
                s.depositRed,
                s.releaseRed
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
        val slider = createMap(s.sliderHomePosition, s.sliderPushedPosition)

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
        val conveyorAdjusterPushed = ConveyorState("Conveyor Adjuster Push", adjusterPosition = 1.909)
        val conveyor = createMap(s.conveyorEmpty, s.conveyorObjectDetected, s.conveyorObjectInWindow, s.conveyorAdjusterPushed)

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
        val testingRig = createMap(s.none, s.green, s.red, s.green_heated, s.red_heated, s.tilted)

        val all = roboticArm.values union slider.values union conveyor.values union testingRig.values
    }

    object Transitions {
        /*
        * StateEvent successor
        */
        val slider_pushed = SliderTransition(States.sliderHomePosition, States.sliderPushedPosition)
        val slider_home = SliderTransition(States.sliderPushedPosition, States.sliderHomePosition)

        val adjuster_detected_pushed = ConveyorTransition(States.conveyorObjectDetected, States.conveyorAdjusterPushed)
        val adjuster_pushed_home = ConveyorTransition(States.conveyorAdjusterPushed, States.conveyorObjectInWindow)

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

    /**
     * This describes the core procedure of the pick-and-place unit. The procedure works as follows. Grab items from the conveyor, put them onto
     * the platform, scan the QR code, heat the plate according to the item class and unload the item into one of two
     * different ramps - again, according to the item class.
     */
    fun successor(current: Environment): Transition? {
        return when {
            current matches Environment(s.idle, conveyorState = s.conveyorEmpty, testingRigState = s.green) -> t.fullrelease_wait
            current matches Environment(s.idle, conveyorState = s.conveyorEmpty, testingRigState = s.red) -> t.fullrelease_wait
            current matches Environment(s.idle, conveyorState = s.conveyorEmpty) -> t.slider_pushed
            current matches Environment(sliderState = s.sliderPushedPosition, conveyorState = s.conveyorObjectDetected) -> t.slider_home
            current matches Environment(s.idle, conveyorState = s.conveyorObjectDetected) -> t.adjuster_detected_pushed
            current matches Environment(s.approach, conveyorState = s.conveyorObjectDetected) -> t.adjuster_detected_pushed
            current matches Environment(conveyorState = s.conveyorAdjusterPushed) -> t.adjuster_pushed_home
            current matches Environment(s.idle, conveyorState = s.conveyorObjectInWindow) -> t.idle_approach
            current matches Environment(s.approach) -> t.approach_pickup
            current matches Environment(s.pickup) -> t.pickup_lift
            current matches Environment(s.lift) -> t.lift_park
            current matches Environment(s.park) -> t.park_halfrelease
            current matches Environment(s.halfRelease) -> t.halfrelease_fullrelease
            current matches Environment(s.fullRelease) -> t.fullrelease_wait
            current matches Environment(s.wait, testingRigState = s.red_heated) -> t.wait_retrieve
            current matches Environment(s.wait, testingRigState = s.green_heated) -> t.wait_retrieve
            current matches Environment(testingRigState = s.green) -> t.green_heatup
            current matches Environment(testingRigState = s.red) -> t.red_heatup
            current matches Environment(s.retrieve) -> t.retrieve_retrievegrip
            current matches Environment(s.retrieveGrip, testingRigState = s.red_heated) -> t.retrievegrip_depositred
            current matches Environment(s.retrieveGrip, testingRigState = s.green_heated) -> t.retrievegrip_depositgreen
            current matches Environment(s.depositRed) -> t.depositred_releasered
            current matches Environment(s.releaseRed) -> t.releasered_idle
            current matches Environment(s.depositGreen) -> t.depositgreen_releasegreen
            current matches Environment(s.releaseGreen) -> t.releasegreen_idle
            else -> null
        }
    }
}
