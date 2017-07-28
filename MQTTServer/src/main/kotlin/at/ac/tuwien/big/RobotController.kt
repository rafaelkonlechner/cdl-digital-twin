package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.States as s

object RobotController {

    fun next(c: Context): Transition? {
        return when (c) {
            Context(s.idle, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.adjuster_pushed
            }
            Context(s.idle, s.sliderHomePosition, s.adjusterPushedPosition, s.red) -> {
                s.idle_approach
            }
            Context(s.approach, s.sliderHomePosition, s.adjusterPushedPosition, s.red) -> {
                s.adjuster_home
            }
            Context(s.approach, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.approach_pickup
            }
            Context(s.pickup, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.pickup_lift
            }
            Context(s.lift, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.lift_park
            }
            Context(s.park, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.park_halfrelease
            }
            Context(s.halfRelease, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.halfrelease_fullrelease
            }
            Context(s.fullRelease, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.fullrelease_wait
            }
            Context(s.wait, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.wait_retrieve
            }
            Context(s.retrieve, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.retrieve_retrievegrip
            }
            Context(s.retrieveGrip, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.retrievegrip_depositred
            }
            Context(s.depositRed, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.depositred_releasered
            }
            Context(s.releaseRed, s.sliderHomePosition, s.adjusterHomePosition, s.red) -> {
                s.slider_pushed
            }
            Context(s.releaseRed, s.sliderPushedPosition, s.adjusterHomePosition, s.red) -> {
                s.releasered_idle
            }
            Context(s.idle, s.sliderPushedPosition, s.adjusterHomePosition, s.red) -> {
                s.slider_home
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