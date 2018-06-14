from time import sleep
from hedgehog.client import connect
import paho.mqtt.client as mqtt
import signal
import sys
import math
import hedgeutil as hu

client = mqtt.Client()
base = 0
main_arm = 1
second_arm = 2
wrist = 3
wrist_r = 0
gripper = 1

delta = 0.02

base_target_position = 1.8
main_arm_target_position = 1.5
second_arm_target_position = 1.5
wrist_target_position = 2.6
wrist_target_rotation = 1.5
gripper_target_position = 2.3

base_position = base_target_position
main_arm_position = main_arm_target_position
second_arm_position = second_arm_target_position
wrist_position = wrist_target_position
wrist_rotation = wrist_target_rotation
gripper_position = gripper_target_position
bite_gripper = False
release_gripper = False
message_rate = 1

base_speed = 0.0
base_speed_manual = 0.0
base_target_speed = 0.0

main_arm_speed = 0.0
main_arm_speed_manual = 0.0
main_arm_target_speed = 0.0

second_arm_speed = 0.0
second_arm_speed_manual = 0.0
second_arm_target_speed = 0.1

wrist_speed = 0.0
wrist_speed_manual = 0.0
wrist_target_speed = 0.0

wrist_rotation_speed = 0.0
wrist_rotation_speed_manual = 0.0
wrist_rotation_target_speed = 0.0

acceleration = 0.000873 # 0.05° per frame (30 frames or 0.5 sec to top speed)
deceleration = acceleration

with connect('tcp://localhost:10789', emergency=15) as hedgehog:
    with connect('tcp://hedgehog13.local:10789', emergency=15) as hedgehog2:
        hedgehog.set_servo(base, True, hu.from_rad(base_position))
        hedgehog.set_servo(main_arm, True, hu.from_rad(main_arm_position))
        hedgehog.set_servo(second_arm, True, hu.from_rad(second_arm_position))
        hedgehog.set_servo(wrist, True, hu.from_rad(wrist_position))

        hedgehog2.set_servo(wrist_r, True, hu.from_rad(wrist_rotation))
        hedgehog2.set_servo(gripper, True, hu.from_rad(gripper_position))

        def shutdown(signal, frame):
            client.loop_stop(force=True)
            print("stopping servos ...")
            hedgehog.set_servo(0, False, 1)
            hedgehog.set_servo(1, False, 1)
            hedgehog.set_servo(2, False, 1)
            hedgehog.set_servo(3, False, 1)

            hedgehog2.set_servo(0, False, 1)
            hedgehog2.set_servo(1, False, 1)

            print("shutdown complete.")
            sys.exit(0)
        signal.signal(signal.SIGINT, shutdown)

        def on_connect(client, userdata, flags, rc):
            print("connected with result code "+str(rc))
            client.subscribe("Actuator")

        def on_message(client, userdata, msg):
            payload = str(msg.payload)
            if len(payload.split(' ')) == 1:
                on_one_arg_message(payload)
            if len(payload.split(' ')) == 2:
                on_two_arg_message(payload)
            if len(payload.split(' ')) == 3:
                on_three_arg_message(payload)

        def on_one_arg_message(payload):
            global base_speed_manual
            global main_arm_speed_manual
            global second_arm_speed_manual
            global wrist_speed_manual
            global wrist_rotation_speed_manual
            global bite_gripper
            global release_gripper

            if payload == "b'left-rotate-base'":
                base_speed_manual = 0.01
            if payload == "b'stop-left-rotate-base'":
                base_speed_manual = 0.0
            if payload == "b'right-rotate-base'":
                base_speed_manual = -0.01
            if payload == "b'stop-right-rotate-base'":
                base_speed_manual = 0.0

            if payload == "b'lift-main-arm'":
                main_arm_speed_manual = -0.01
            if payload == "b'stop-lift-main-arm'":
                main_arm_speed_manual = 0.0
            if payload == "b'lower-main-arm'":
                main_arm_speed_manual = 0.01
            if payload == "b'stop-lower-main-arm'":
                main_arm_speed_manual = 0.0

            if payload == "b'lift-second-arm'":
                second_arm_speed_manual = -0.01
            if payload == "b'stop-lift-second-arm'":
                second_arm_speed_manual = 0.0
            if payload == "b'lower-second-arm'":
                second_arm_speed_manual = 0.01
            if payload == "b'stop-lower-second-arm'":
                second_arm_speed_manual = 0.0

            if payload == "b'lower-wrist'":
                wrist_speed_manual = -0.01
            if payload == "b'stop-lower-wrist'":
                wrist_speed_manual = 0.0
            if payload == "b'lift-wrist'":
                wrist_speed_manual = 0.01
            if payload == "b'stop-lift-wrist'":
                wrist_speed_manual = 0.0

            if payload == "b'rotate-left-wrist'":
                wrist_rotation_speed_manual = -0.04
            if payload == "b'stop-rotate-left-wrist'":
                wrist_rotation_speed_manual = 0.0
            if payload == "b'rotate-right-wrist'":
                wrist_rotation_speed_manual = 0.04
            if payload == "b'stop-rotate-right-wrist'":
                wrist_rotation_speed_manual = 0.0

            if payload == "b'grip-gripper'":
                bite_gripper = True
            if payload == "b'stop-grip-gripper'":
                bite_gripper = False
            if payload == "b'release-gripper'":
                release_gripper = True
            if payload == "b'stop-release-gripper'":
                release_gripper = False

        def on_two_arg_message(payload):
            global message_rate
            component, rate = payload.split(' ')
            rate = float(rate[:len(rate) - 1])
            if component == "b'message-rate":
                message_rate = int(rate)

        def on_three_arg_message(payload):
            print("now")
            global base_target_position
            global base_target_speed
            global main_arm_target_position
            global main_arm_target_speed
            global second_arm_target_position
            global second_arm_target_speed
            global wrist_target_position
            global wrist_target_speed
            global wrist_target_rotation
            global wrist_rotation_target_speed
            global gripper_target_position

            component, position, speed = payload.split(' ')
            position = float(position)
            speed = float(speed[:len(speed) - 1])
            if component == "b'base-goto":
                base_target_position = position #+ 3.14
                base_target_speed = speed
            if component == "b'main-arm-goto":
                main_arm_target_position = position #+ 1.5
                main_arm_target_speed = speed
                second_arm_target_speed = speed
            if component == "b'second-arm-goto":
                #position = -position
                second_arm_target_position = position #+ 2.9 - (main_arm_target_position + 1.5)
                second_arm_target_speed = speed
            if component == "b'wrist-goto":
                wrist_target_position = position
                wrist_target_speed = speed
            if component == "b'wrist-roto":
                wrist_target_rotation = position
                wrist_rotation_target_speed = speed
            if component == "b'gripper-goto":
                gripper_target_position = position

        client.on_connect = on_connect
        client.on_message = on_message
        #client.username_pw_set("test",password="test")
        try:
            ip = sys.argv[1]
        except IndexError:
            ip = "172.20.10.2"
        client.connect(ip, 1883, 60)

        client.loop_start()

        counter = 0
        while True:
            base_speed = hu.calc_speed(base_position, base_target_position, base_speed, base_target_speed, acceleration, deceleration)
            main_arm_speed = hu.calc_speed(main_arm_position, main_arm_target_position, main_arm_speed, main_arm_target_speed, acceleration * 2, deceleration * 2)
            second_arm_speed = hu.calc_speed(second_arm_position, second_arm_target_position, second_arm_speed, second_arm_target_speed, acceleration * 2, deceleration * 2)
            wrist_rotation_speed = hu.calc_speed(wrist_rotation, wrist_target_rotation, wrist_rotation_speed, wrist_rotation_target_speed, acceleration*3, deceleration*3)
            wrist_speed = hu.calc_speed(wrist_position, wrist_target_position, wrist_speed, wrist_target_speed, acceleration*3, deceleration*3)
            old_base = hu.from_rad(base_position)
            if base_speed + base_speed_manual != 0.0:
                if base_speed_manual != 0.0:
                    base_position = base_position + base_speed_manual
                    base_target_position = base_position
                else:
                    base_position = base_position + base_speed
                base_position = max(base_position, 0.0)
                base_position = min(base_position, 3.14)
                if old_base != hu.from_rad(base_position):
                    hedgehog.set_servo(0, True, hu.from_rad(base_position))

            old_main = hu.from_rad(main_arm_position)
            if main_arm_speed + main_arm_speed_manual != 0.0:
                if main_arm_speed_manual != 0.0:
                    main_arm_position = main_arm_position + main_arm_speed_manual
                    main_arm_target_position = main_arm_position
                else:
                    main_arm_position = main_arm_position + main_arm_speed
                main_arm_position = max(main_arm_position, 0.0)
                main_arm_position = min(main_arm_position, 3.14)
                if old_main != hu.from_rad(main_arm_position):
                    hedgehog.set_servo(1, True, hu.from_rad(main_arm_position))

            old_second = hu.from_rad(second_arm_position)
            if  second_arm_speed + second_arm_speed_manual != 0.0:
                if second_arm_speed_manual != 0.0:
                    second_arm_position = max(0.0, min(3.14, second_arm_position + second_arm_speed_manual))
                    second_arm_target_position = second_arm_position
                else:
                    second_arm_position = max(0.0, min(3.14, second_arm_position + second_arm_speed))
                if old_second != hu.from_rad(second_arm_position):
                    hedgehog.set_servo(2, True, hu.from_rad(second_arm_position))

            if  wrist_speed + wrist_speed_manual != 0.0:
                if wrist_speed_manual != 0.0:
                    wrist_position = max(0.0, min(3.14, wrist_position + wrist_speed_manual))
                    wrist_target_position = wrist_position
                else:
                    wrist_position = max(0.0, min(3.14, wrist_position + wrist_speed))
                hedgehog.set_servo(3, True, hu.from_rad(wrist_position))

            if  wrist_rotation_speed + wrist_rotation_speed_manual != 0.0:
                if wrist_rotation_speed_manual != 0.0:
                    wrist_rotation = max(0.0, min(3.14, wrist_rotation + wrist_rotation_speed_manual))
                    wrist_target_rotation = wrist_rotation
                else:
                    wrist_rotation = max(0.0, min(3.14, wrist_rotation + wrist_rotation_speed))
                hedgehog2.set_servo(0, True, hu.from_rad(wrist_rotation))

            if bite_gripper:
                gripper_target_position = max(2.12, min(3.14, gripper_position + delta))
            if release_gripper:
                gripper_target_position = max(2.12, min(3.14, gripper_position - delta))
            if gripper_target_position != gripper_position:
                hedgehog2.set_servo(1, True, hu.from_rad(gripper_target_position))
                gripper_position = gripper_target_position

            # message_rate = 1 (1 mal in 30)
            if message_rate > 0 and counter > 60 / message_rate:
                message_roboticarm = """
    {
    "entity": "RoboticArm",
    "basePosition": %s,
    "mainArmPosition": %s,
    "secondArmPosition": %s,
    "wristPosition": %s,
    "wristRotation": %s,
    "gripperPosition": %s
    }""" % (
                str(base_position),
                str(main_arm_position),
                str(second_arm_position),
                str(wrist_position),
                str(wrist_rotation),
                str(gripper_position)
                )
                client.publish("Sensor", payload=message_roboticarm, qos=0, retain=False)
                counter = 0

            counter += 1
            sleep(0.00825)
            #sleep(0.0165)
