package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.entity.transition.RoboticArmTransition
import at.ac.tuwien.big.entity.transition.Transition
import at.ac.tuwien.big.StateMachine.States as s
import at.ac.tuwien.big.StateMachine.Transitions as t

/**
 * This controller holds the core logic of the production steps during a simulation by searching the defined successor
 * state for a given input state.
 */
object PickAndPlaceController {

    var roboticArmSnapshot: RoboticArmState = RoboticArmState()
        private set
    var roboticArmState: RoboticArmState = RoboticArmState()
        private set

    var sliderSnapshot: SliderState = SliderState()
        private set
    var sliderState: SliderState = SliderState()
        private set

    var conveyorSnapshot: ConveyorState = ConveyorState()
        private set
    var conveyorState: ConveyorState = ConveyorState()
        private set

    var testingRigSnapshot: TestingRigState = TestingRigState()
        private set
    var testingRigState: TestingRigState = TestingRigState()
        private set

    var latestMatch: State = State()
        private set

    var targetState = StateMachine.States.idle
        private set

    private var transitionStart = System.currentTimeMillis()
    private var timeBase = 1000L
    private var timeMainArm = 1000L
    private var timeSecondArm = 1000L

    fun latest() = latestMatch

    fun update(e: StateEvent) {
        var change = false
        when (e) {
            is RoboticArmState -> {
                roboticArmSnapshot = e
                val match = StateMachine.matchState(roboticArmSnapshot)
                if (match != null && roboticArmSnapshot != match) {
                    roboticArmState = match
                    change = true
                }
            }
            is SliderState -> {
                sliderSnapshot = e
                val match = StateMachine.matchState(sliderSnapshot)
                if (match != null && sliderSnapshot != match) {
                    sliderState = match
                    change = true
                }
            }
            is ConveyorState -> {
                if (e.adjusterPosition != null) {
                    conveyorSnapshot = conveyorSnapshot.copy(adjusterPosition = e.adjusterPosition)
                }
                if (e.detected != null) {
                    conveyorSnapshot = conveyorSnapshot.copy(detected = e.detected)
                }
                if (e.inPickupWindow != null) {
                    conveyorSnapshot = conveyorSnapshot.copy(inPickupWindow = e.inPickupWindow)
                }
                val match = StateMachine.matchState(conveyorSnapshot)
                if (match != null && conveyorSnapshot != match) {
                    conveyorState = match
                    change = true
                }
            }
            is TestingRigState -> {
                if (e.objectCategory != null) {
                    testingRigSnapshot = testingRigSnapshot.copy(objectCategory = e.objectCategory)
                }
                if (e.platformPosition != null) {
                    testingRigSnapshot = testingRigSnapshot.copy(platformPosition = e.platformPosition)
                }
                if (e.heatplateTemperature != null) {
                    testingRigSnapshot = testingRigSnapshot.copy(heatplateTemperature = e.heatplateTemperature)
                }
                val match = StateMachine.matchState(testingRigSnapshot)
                if (match != null && e != match) {
                    testingRigState = match
                    change = true
                }
            }
        }
        if (change) {
            latestMatch = State(roboticArmState, sliderState, conveyorState, testingRigState)
        }
    }

    fun next() = next(latestMatch)

    private fun next(current: State): Transition? {
        return when {
            current matches State(s.idle, conveyorState = s.conveyorEmpty, testingRigState = s.green_heated) -> t.fullrelease_wait
            current matches State(s.idle, conveyorState = s.conveyorEmpty, testingRigState = s.red_heated) -> t.fullrelease_wait
            current matches State(s.idle, conveyorState = s.conveyorEmpty) -> t.slider_pushed
            current matches State(sliderState = s.sliderPushedPosition, conveyorState = s.conveyorObjectDetected) -> t.slider_home
            current matches State(conveyorState = s.conveyorObjectDetected) -> t.adjuster_detected_pushed
            current matches State(conveyorState = s.conveyorAdjusterPushed) -> t.adjuster_pushed_home
            current matches State(s.idle, conveyorState = s.conveyorObjectInWindow) -> t.idle_approach
            current matches State(s.approach) -> t.approach_pickup
            current matches State(s.pickup) -> t.pickup_lift
            current matches State(s.lift) -> t.lift_park
            current matches State(s.park) -> t.park_halfrelease
            current matches State(s.halfRelease) -> t.halfrelease_fullrelease
            current matches State(s.fullRelease) -> t.fullrelease_wait
            current matches State(s.wait, testingRigState = s.red_heated) -> t.wait_retrieve
            current matches State(s.wait, testingRigState = s.green_heated) -> t.wait_retrieve
            current matches State(testingRigState = s.green) -> t.green_heatup
            current matches State(testingRigState = s.red) -> t.red_heatup
            current matches State(s.retrieve) -> t.retrieve_retrievegrip
            current matches State(s.retrieveGrip, testingRigState = s.red_heated) -> t.retrievegrip_depositred
            current matches State(s.retrieveGrip, testingRigState = s.green_heated) -> t.retrievegrip_depositgreen
            current matches State(s.depositRed) -> t.depositred_releasered
            current matches State(s.releaseRed) -> t.releasered_idle
            current matches State(s.depositGreen) -> t.depositgreen_releasegreen
            current matches State(s.releaseGreen) -> t.releasegreen_idle
            else -> null
        }
    }

    fun start(transition: Transition?) {
        if (transition is RoboticArmTransition) {
            targetState = transition.targetState
            transitionStart = System.currentTimeMillis()
            val start = latestMatch.roboticArmState!!
            timeBase = Util.timeToTarget(start.basePosition, targetState.basePosition, 0.000873, transition.baseSpeed)
            timeMainArm = Util.timeToTarget(start.mainArmPosition, targetState.mainArmPosition, 0.000873, transition.mainArmSpeed)
            timeSecondArm = Util.timeToTarget(start.secondArmPosition, targetState.secondArmPosition, 0.000873, transition.secondArmSpeed)
        }
    }

    fun getReference(time: Long): RoboticArmState? {
        val diff = time - transitionStart
        val startState = latestMatch.roboticArmState
        if (startState != null) {
            val baseRef = Util.getPosition(startState.basePosition, targetState.basePosition, timeBase, Math.min(timeBase, diff))
            val mainArmRef = Util.getPosition(startState.mainArmPosition, targetState.mainArmPosition, timeMainArm, Math.min(timeMainArm, diff))
            val secondArmRef = Util.getPosition(startState.secondArmPosition, targetState.secondArmPosition, timeSecondArm, Math.min(timeSecondArm, diff))
            return RoboticArmState(basePosition = baseRef, mainArmPosition = mainArmRef, secondArmPosition = secondArmRef)
        } else {
            return null
        }
    }
}
