package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.Context
import at.ac.tuwien.big.entity.state.Transition
import at.ac.tuwien.big.States as s

object RobotController {

    fun next(c: Context): Pair<Transition, Int>? {
        return when (c) {
            Context(s.idle, s.sliderHomePosition, s.conveyorEmpty, s.none) -> {
                Pair(s.slider_pushed, 10000)
            }
            Context(s.idle, s.sliderPushedPosition, s.conveyorEmpty, s.none),
            Context(s.idle, s.sliderPushedPosition, s.conveyorObjectDetected, s.none),
            Context(s.idle, s.sliderPushedPosition, s.conveyorObjectInWindow, s.none) -> {
                Pair(s.slider_home, 0)
            }
            Context(s.idle, s.sliderHomePosition, s.conveyorObjectDetected, s.none) -> {
                Pair(s.adjuster_detected_pushed, 0)
            }
            Context(s.idle, s.sliderHomePosition, s.conveyorAdjusterPushed, s.none),
            Context(s.approach, s.sliderHomePosition, s.conveyorAdjusterPushed, s.none) -> {
                Pair(s.adjuster_pushed_pickup, 0)
            }
            Context(s.idle, s.sliderHomePosition, s.conveyorObjectInWindow, s.none) -> {
                Pair(s.idle_approach, 0)
            }
            Context(s.approach, s.sliderHomePosition, s.conveyorObjectInWindow, s.none) -> {
                Pair(s.approach_pickup, 0)
            }
            Context(s.pickup, s.sliderHomePosition, s.conveyorObjectInWindow, s.none) -> {
                Pair(s.pickup_lift, 0)
            }
            Context(s.lift, s.sliderHomePosition, s.conveyorObjectInWindow, s.none) -> {
                Pair(s.lift_park, 0)
            }
            Context(s.park, s.sliderHomePosition, s.conveyorEmpty, s.red),
            Context(s.park, s.sliderHomePosition, s.conveyorEmpty, s.green)
            -> {
                Pair(s.park_halfrelease, 0)
            }
            Context(s.halfRelease, s.sliderHomePosition, s.conveyorEmpty, s.red),
            Context(s.halfRelease, s.sliderHomePosition, s.conveyorEmpty, s.green)
            -> {
                Pair(s.halfrelease_fullrelease, 0)
            }
            Context(s.fullRelease, s.sliderHomePosition, s.conveyorEmpty, s.red),
            Context(s.fullRelease, s.sliderHomePosition, s.conveyorEmpty, s.green)
            -> {
                Pair(s.fullrelease_wait, 0)
            }
            Context(s.wait, s.sliderHomePosition, s.conveyorEmpty, s.red),
            Context(s.wait, s.sliderHomePosition, s.conveyorEmpty, s.green)
            -> {
                Pair(s.wait_retrieve, 0)
            }
            Context(s.retrieve, s.sliderHomePosition, s.conveyorEmpty, s.red),
            Context(s.retrieve, s.sliderHomePosition, s.conveyorEmpty, s.green)
            -> {
                Pair(s.retrieve_retrievegrip, 0)
            }
            Context(s.retrieveGrip, s.sliderHomePosition, s.conveyorEmpty, s.red) -> {
                Pair(s.retrievegrip_depositred, 0)
            }
            Context(s.retrieveGrip, s.sliderHomePosition, s.conveyorEmpty, s.green) -> {
                Pair(s.retrievegrip_depositgreen, 0)
            }
            Context(s.depositRed, s.sliderHomePosition, s.conveyorEmpty, s.none) -> {
                Pair(s.depositred_releasered, 0)
            }
            Context(s.releaseRed, s.sliderHomePosition, s.conveyorEmpty, s.none) -> {
                Pair(s.releasered_idle, 0)
            }
            Context(s.depositGreen, s.sliderHomePosition, s.conveyorEmpty, s.none) -> {
                Pair(s.depositgreen_releasegreen, 0)
            }
            Context(s.releaseGreen, s.sliderHomePosition, s.conveyorEmpty, s.none) -> {
                Pair(s.releasegreen_idle, 0)
            }
            else -> null
        }
    }

    /**
     * start:
     * wait until object on conveyor
     * while object not in good position:
     *      push conveyor
     *      then pull conveyor
     * then approach
     * then grip
     * if gripped: lift
     * then park
     * then release half
     * then release full
     * then standby
     * then retrieve
     * then grip
     * if green: deposit and release green
     * if red: deposit and release red
     * then go back to start
     *
     * in the meantime:
     * push slider
     * then pull slider
     */


    /**
     * We need to now:
     * Which of the all are we in?
     * Is there an object on the conveyor?
     * Which color does the object have?
     * Does the gripper have contact with the object?
     */
}