package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.ConveyorState
import at.ac.tuwien.big.entity.state.RoboticArmState
import at.ac.tuwien.big.entity.state.SliderState
import at.ac.tuwien.big.entity.state.TestingRigState
import at.ac.tuwien.big.entity.transition.RoboticArmTransition
import at.ac.tuwien.big.entity.transition.Transition
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
        val idle = RoboticArmState(
                name = "Idle",
                basePosition = 1.5,
                mainArmPosition = 1.5,
                secondArmPosition = 2.9,
                headMountPosition = 3.00,
                headPosition = 1.82,
                gripperPosition = 1.0
        )

        val approach = RoboticArmState(
                name = "Approach",
                basePosition = 1.52,
                mainArmPosition = 1.98,
                secondArmPosition = 2.18,
                headMountPosition = 2.22,
                headPosition = 1.82,
                gripperPosition = 0.48
        )
        val pickup = RoboticArmState(
                name = "Pickup",
                basePosition = 1.52,
                mainArmPosition = 1.9,
                secondArmPosition = 2.24,
                headMountPosition = 2.22,
                headPosition = 1.82,
                gripperPosition = 1.2
        )
        val lift = RoboticArmState(
                name = "Lift",
                basePosition = 0.86,
                mainArmPosition = 1.9,
                secondArmPosition = -3.71,
                headMountPosition = 0.08,
                headPosition = 0.52,
                gripperPosition = 1.2
        )
        val park = RoboticArmState(
                name = "Park",
                basePosition = 0.86,
                mainArmPosition = 1.69,
                secondArmPosition = -3.71,
                headMountPosition = 0.08,
                headPosition = 0.52,
                gripperPosition = 1.2
        )
        val roboticArm = createMap(idle, approach, pickup, lift, park)
        val slider = emptyMap<String, SliderState>()
        val conveyor = emptyMap<String, ConveyorState>()
        val testingRig = emptyMap<String, TestingRigState>()
    }

    object Transitions {
        val idle_approach = RoboticArmTransition(States.idle, States.approach, mainArmSpeed = 0.6)
        val approach_pickup = RoboticArmTransition(States.approach, States.pickup)
        val pickup_lift = RoboticArmTransition(States.pickup, States.lift, mainArmSpeed = 0.2, secondArmSpeed = 0.2)
        val lift_park = RoboticArmTransition(States.lift, States.park)
        val park_idle = RoboticArmTransition(States.park, States.idle)
    }

    fun successor(current: RoboticArmState): Transition? {
        return when {
            current.match(s.idle, doubleAccuracy) -> Transitions.idle_approach
            current.match(s.approach, doubleAccuracy) -> Transitions.approach_pickup
            current.match(s.pickup, doubleAccuracy) -> Transitions.pickup_lift
            current.match(s.lift, doubleAccuracy) -> Transitions.lift_park
            current.match(s.park, doubleAccuracy) -> Transitions.park_idle
            else -> null
        }
    }
}
