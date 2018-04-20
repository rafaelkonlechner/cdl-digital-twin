package at.ac.tuwien.big

import at.ac.tuwien.big.StateMachine as s
import at.ac.tuwien.big.entity.state.Context
import at.ac.tuwien.big.entity.state.Transition

/**
 * This controller holds the core logic of the production steps during a simulation by searching the defined successor
 * state for a given input state.
 */
object RobotController {

    /**
     * Defines the rules for transitions between states. The basic control loop can be described as:
     *
     * Start:
     * + Is there an item on the conveyor?
     *      + If yes: Do nothing
     *      + If no: *Slider Push*, then *Slider Home*
     * + Is the item in the pickup window?
     *      + While no: *Adjuster Push*, then *Adjuster Home*
     * + *Approach* - *Grip* - *Lift* - *Park* - *Release Half* - *Release Full* - *Standby*
     * + ... Testing Rig will detect category ...
     * + *Retrieve* - *Retrieve Grip*
     * + What color is the item?
     *      + If green: *Deposit Green* - *Release Green*
     *      + If red: *Deposit Red* - *Release Red*
     * + *IDLE*
     *
     * The return value is a pair of a transition and a waiting time in milliseconds. Sometimes it is necessary to wait
     * for the the next matching, as the expected state change requires time.
     *
     * @return a pair of transition and waiting time in milliseconds
     */
    fun next(c: Context): Pair<Transition, Int>? {
        return when (c) {
            Context(s.States.idle, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.none) -> {
                Pair(s.Transitions.slider_pushed, 8000)
            }
            Context(s.States.idle, s.States.sliderPushedPosition, s.States.conveyorEmpty, s.States.none),
            Context(s.States.idle, s.States.sliderPushedPosition, s.States.conveyorObjectDetected, s.States.none),
            Context(s.States.idle, s.States.sliderPushedPosition, s.States.conveyorObjectInWindow, s.States.none) -> {
                Pair(s.Transitions.slider_home, 0)
            }
            Context(s.States.idle, s.States.sliderHomePosition, s.States.conveyorObjectDetected, s.States.none) -> {
                Pair(s.Transitions.adjuster_detected_pushed, 0)
            }
            Context(s.States.idle, s.States.sliderHomePosition, s.States.conveyorAdjusterPushed, s.States.none),
            Context(s.States.approach, s.States.sliderHomePosition, s.States.conveyorAdjusterPushed, s.States.none) -> {
                Pair(s.Transitions.adjuster_pushed_pickup, 0)
            }
            Context(s.States.idle, s.States.sliderHomePosition, s.States.conveyorObjectInWindow, s.States.none) -> {
                Pair(s.Transitions.idle_approach, 0)
            }
            Context(s.States.approach, s.States.sliderHomePosition, s.States.conveyorObjectInWindow, s.States.none) -> {
                Pair(s.Transitions.approach_pickup, 0)
            }
            Context(s.States.pickup, s.States.sliderHomePosition, s.States.conveyorObjectInWindow, s.States.none),
            Context(s.States.pickup, s.States.sliderHomePosition, s.States.conveyorObjectDetected, s.States.none) -> {
                Pair(s.Transitions.pickup_lift, 0)
            }
            Context(s.States.lift, s.States.sliderHomePosition, s.States.conveyorObjectInWindow, s.States.none) -> {
                Pair(s.Transitions.lift_park, 0)
            }
            Context(s.States.park, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.none),
            Context(s.States.park, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.red),
            Context(s.States.park, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.green)
            -> {
                Pair(s.Transitions.park_halfrelease, 0)
            }
            Context(s.States.halfRelease, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.none),
            Context(s.States.halfRelease, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.red),
            Context(s.States.halfRelease, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.green)
            -> {
                Pair(s.Transitions.halfrelease_fullrelease, 0)
            }
            Context(s.States.idle, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.red),
            Context(s.States.idle, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.green),
            Context(s.States.fullRelease, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.none),
            Context(s.States.fullRelease, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.red),
            Context(s.States.fullRelease, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.green)
            -> {
                Pair(s.Transitions.fullrelease_wait, 0)
            }
            Context(s.States.wait, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.red),
            Context(s.States.wait, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.green)
            -> {
                Pair(s.Transitions.wait_retrieve, 0)
            }
            Context(s.States.retrieve, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.red),
            Context(s.States.retrieve, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.green)
            -> {
                Pair(s.Transitions.retrieve_retrievegrip, 0)
            }
            Context(s.States.retrieveGrip, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.red) -> {
                Pair(s.Transitions.retrievegrip_depositred, 0)
            }
            Context(s.States.retrieveGrip, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.green) -> {
                Pair(s.Transitions.retrievegrip_depositgreen, 0)
            }
            Context(s.States.depositRed, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.red) -> {
                Pair(s.Transitions.red_tilt, 0)
            }
            Context(s.States.depositGreen, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.green) -> {
                Pair(s.Transitions.green_tilt, 0)
            }
            Context(s.States.depositRed, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.tilted),
            Context(s.States.depositGreen, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.tilted) -> {
                Pair(s.Transitions.tilted_none, 0)
            }
            Context(s.States.idle, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.tilted) -> {
                Pair(s.Transitions.tilted_none, 0)
            }
            Context(s.States.depositRed, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.none) -> {
                Pair(s.Transitions.depositred_releasered, 0)
            }
            Context(s.States.releaseRed, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.none) -> {
                Pair(s.Transitions.releasered_idle, 0)
            }
            Context(s.States.depositGreen, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.none) -> {
                Pair(s.Transitions.depositgreen_releasegreen, 0)
            }
            Context(s.States.releaseGreen, s.States.sliderHomePosition, s.States.conveyorEmpty, s.States.none) -> {
                Pair(s.Transitions.releasegreen_idle, 0)
            }
            else -> null
        }
    }
}