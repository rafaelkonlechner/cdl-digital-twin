package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.RoboticArmState
import at.ac.tuwien.big.entity.transition.RoboticArmTransition
import at.ac.tuwien.big.entity.transition.Transition

object ResidualError {

    private var transitionStart = System.currentTimeMillis()
    private var timeBase = 1000L
    private var timeMainArm = 1000L
    private var timeSecondArm = 1000L
    private var timeWrist = 1000L
    private var timeGripper = 1000L
    var targetState = RoboticArmState()
    var started = false


    /**
     * Indicate the start of a transition. This sets the starting time for finding reference points.
     */
    fun start(transition: Transition?) {
        if (!started && transition is RoboticArmTransition) {
            started = true
            targetState = transition.targetState
            transitionStart = System.currentTimeMillis()
            val start = StateObserver.latestMatch.first.environment.roboticArmState!!
            timeBase = timeToTarget(start.basePosition, targetState.basePosition, 0.000873, transition.baseSpeed)
            timeMainArm = timeToTarget(start.mainArmPosition, targetState.mainArmPosition, 0.000873, transition.mainArmSpeed)
            timeSecondArm = timeToTarget(start.secondArmPosition, targetState.secondArmPosition, 0.000873, transition.secondArmSpeed)
            timeWrist = timeToTarget(start.headMountPosition, targetState.headMountPosition)
            timeGripper = timeToTarget(start.gripperPosition, targetState.gripperPosition)
        }
    }

    fun stop() {
        started = false
    }

    /**
     * Return a reference point indicating the desired state during state transitions. Reference points are linear
     * interpolations between the attribute values of the start and the target state.
     */
    fun getReference(time: Long): RoboticArmState? {
        val diff = time - transitionStart
        val startState = StateObserver.latestMatch.first.environment.roboticArmState
        return if (startState != null) {
            val baseRef = getPosition(startState.basePosition, targetState.basePosition, timeBase, Math.min(timeBase, diff))
            val mainArmRef = getPosition(startState.mainArmPosition, targetState.mainArmPosition, timeMainArm, Math.min(timeMainArm, diff))
            val secondArmRef = getPosition(startState.secondArmPosition, targetState.secondArmPosition, timeSecondArm, Math.min(timeSecondArm, diff))
            val wristRef = getPosition(startState.headMountPosition, targetState.headMountPosition, timeWrist, Math.min(timeWrist, diff))
            val gripperRef = getPosition(startState.gripperPosition, targetState.gripperPosition, timeGripper, Math.min(timeGripper, diff))
            RoboticArmState("Reference", "RoboticArm", baseRef, mainArmRef, secondArmRef, wristRef, gripperRef)
        } else {
            null
        }
    }
}