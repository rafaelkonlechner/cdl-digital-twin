package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.Context
import at.ac.tuwien.big.entity.state.Transition
import at.ac.tuwien.big.StateMachine.States as s
import at.ac.tuwien.big.StateMachine.Transitions as t

/**
 * This controller holds the core logic of the production steps during a simulation by searching the defined successor
 * state for a given input state.
 */
object RobotController {

    fun next(c: Context): Pair<Transition, Int>? {
        return when {
            c matches Context(s.idle, conveyorState = s.conveyorEmpty, testingRigState = s.red) -> Pair(t.fullrelease_wait, 0)
            c matches Context(s.idle, conveyorState = s.conveyorEmpty, testingRigState = s.green) -> Pair(t.fullrelease_wait, 0)
            c matches Context(s.idle, conveyorState = s.conveyorEmpty) -> Pair(t.slider_pushed, 8000)
            c matches Context(sliderState = s.sliderPushedPosition) -> Pair(t.slider_home, 0)
            c matches Context(conveyorState = s.conveyorObjectDetected) -> Pair(t.adjuster_detected_pushed, 0)
            c matches Context(conveyorState = s.conveyorAdjusterPushed) -> Pair(t.adjuster_pushed_pickup, 0)
            c matches Context(s.idle) -> Pair(t.idle_approach, 0)
            c matches Context(s.approach) -> Pair(t.approach_pickup, 0)
            c matches Context(s.pickup) -> Pair(t.pickup_lift, 0)
            c matches Context(s.lift) -> Pair(t.lift_park, 0)
            c matches Context(s.park) -> Pair(t.park_halfrelease, 0)
            c matches Context(s.halfRelease) -> Pair(t.halfrelease_fullrelease, 0)
            c matches Context(s.idle) -> Pair(t.fullrelease_wait, 0)
            c matches Context(s.fullRelease) -> Pair(t.fullrelease_wait, 0)
            c matches Context(s.wait, testingRigState = s.red_heated) -> Pair(t.wait_retrieve, 0)
            c matches Context(s.wait, testingRigState = s.green_heated) -> Pair(t.wait_retrieve, 0)
            c matches Context(testingRigState = s.green) -> Pair(t.green_heatup, 2000)
            c matches Context(testingRigState = s.red) -> Pair(t.red_heatup, 2000)
            c matches Context(s.retrieve) -> Pair(t.retrieve_retrievegrip, 0)
            c matches Context(s.retrieveGrip, testingRigState = s.red_heated) -> Pair(t.retrievegrip_depositred, 0)
            c matches Context(s.retrieveGrip, testingRigState = s.green_heated) -> Pair(t.retrievegrip_depositgreen, 0)
            c matches Context(s.depositRed) -> Pair(t.depositred_releasered, 0)
            c matches Context(s.releaseRed) -> Pair(t.releasered_idle, 0)
            c matches Context(s.depositGreen) -> Pair(t.depositgreen_releasegreen, 0)
            c matches Context(s.releaseGreen) -> Pair(t.releasegreen_idle, 0)
            else -> null
        }
    }


    infix fun Context.matches(next: Context): Boolean {
        return (next.roboticArmState == null || this.roboticArmState == next.roboticArmState)
                && (next.conveyorState == null || this.conveyorState == next.conveyorState)
                && (next.testingRigState == null || this.testingRigState == next.testingRigState)
                && (next.sliderState == null || this.sliderState == next.sliderState)
    }
}
