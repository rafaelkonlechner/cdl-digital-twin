import paho.mqtt.client as mqtt
import GameLogic
import re
import bpy

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))
    client.subscribe("Actuator-Simulation")

def on_message(client, userdata, msg):

    print(msg.topic + " " + str(msg.payload))
    payload = str(msg.payload)
    print(payload)

    if len(payload.split(' ')) == 2:
        on_goto_message(payload)
    if len(payload.split(' ')) == 3:
        on_goto_speed_control_message(payload)

    if payload == "b'left-rotate-base'":
        GameLogic.left_rotate_base = True
    if payload == "b'stop-left-rotate-base'":
        GameLogic.left_rotate_base = False
    if payload == "b'right-rotate-base'":
        GameLogic.right_rotate_base = True
    if payload == "b'stop-right-rotate-base'":
        GameLogic.right_rotate_base = False

    if payload == "b'lift-main-arm'":
        GameLogic.lift_main_arm = True
    if payload == "b'stop-lift-main-arm'":
        GameLogic.lift_main_arm = False
    if payload == "b'lower-main-arm'":
        GameLogic.lower_main_arm = True
    if payload == "b'stop-lower-main-arm'":
        GameLogic.lower_main_arm = False

    if payload == "b'lift-second-arm'":
        GameLogic.lift_second_arm = True
    if payload == "b'stop-lift-second-arm'":
        GameLogic.lift_second_arm = False
    if payload == "b'lower-second-arm'":
        GameLogic.lower_second_arm = True
    if payload == "b'stop-lower-second-arm'":
        GameLogic.lower_second_arm = False

    if payload == "b'left-rotate-head-mount'":
        GameLogic.left_rotate_head_mount = True
    if payload == "b'stop-left-rotate-head-mount'":
        GameLogic.left_rotate_head_mount = False
    if payload == "b'right-rotate-head-mount'":
        GameLogic.right_rotate_head_mount = True
    if payload == "b'stop-right-rotate-head-mount'":
        GameLogic.right_rotate_head_mount = False

    if payload == "b'lift-head'":
        GameLogic.lift_head = True
    if payload == "b'stop-lift-head'":
        GameLogic.lift_head = False
    if payload == "b'lower-head'":
        GameLogic.lower_head = True
    if payload == "b'stop-lower-head'":
        GameLogic.lower_head = False
    if payload == "b'grip-gripper'":
        GameLogic.grip_gripper = True
    if payload == "b'stop-grip-gripper'":
        GameLogic.grip_gripper = False
    if payload == "b'release-gripper'":
        GameLogic.release_gripper = True
    if payload == "b'stop-release-gripper'":
        GameLogic.release_gripper = False

def on_goto_message(message):
    print(message)
    msg_type, position = message.split(' ')
    position = float(position[:len(position) - 1])
    if msg_type == "b'message-rate":
        GameLogic.messageRate = int(position)
    elif msg_type == "b'slider-goto":
        GameLogic.slider_target_position = position
        print("New slider target position: " + str(GameLogic.slider_target_position))
        GameLogic.slider_target = True
    elif msg_type == "b'adjuster-goto":
        GameLogic.adjuster_target_position = position
        print("New adjuster target position: " + str(GameLogic.adjuster_target_position))
        GameLogic.adjuster_target = True
    elif msg_type == "b'platform-goto":
        GameLogic.platform_target_position = position
        print("New platform target position: " + str(GameLogic.platform_target_position))
        GameLogic.platform_target = True
    elif msg_type == "b'platform-heatup":
        GameLogic.heatplateTargetTemperature = int(position)

def on_goto_speed_control_message(message):
    print(message)
    msg_type, position, speed = message.split(' ')
    position = float(position)
    speed = float(speed[:len(speed) - 1])
    if msg_type == "b'base-goto":
        GameLogic.base_target_position = position - 1.68
        GameLogic.base_target_speed = speed
        GameLogic.base_target = True
        print("New base target position: " + str(GameLogic.base_target_position))
    elif msg_type == "b'main-arm-goto":
        GameLogic.main_arm_target_position = position - 1.50
        GameLogic.main_arm_target_speed = speed
        print("New main arm target position: " + str(GameLogic.main_arm_target_position))
        GameLogic.main_arm_target = True
    elif msg_type == "b'second-arm-goto":
        GameLogic.second_arm_target_position = position - 2.08
        GameLogic.second_arm_target_speed = speed
        print("New second arm target position: " + str(GameLogic.second_arm_target_position))
        GameLogic.second_arm_target = True
    elif msg_type == "b'head-mount-goto":
        GameLogic.head_mount_target_position = position - 1.5
        GameLogic.head_mount_target_speed = speed
        print("New head_mount target position: " + str(GameLogic.head_mount_target_position))
        GameLogic.head_mount_target = True
    elif msg_type == "b'head-goto":
        GameLogic.head_target_position = position - 2.0
        GameLogic.head_target_speed = speed
        print("New head target position: " + str(GameLogic.head_target_position))
        GameLogic.head_target = True
    elif msg_type == "b'gripper-goto":
        GameLogic.gripper_target_position = position - 2.30
        print("New gripper target position: " + str(GameLogic.gripper_target_position))
        GameLogic.gripper_target = True

class Server:
    def __init__(self):
        self.client = mqtt.Client()
        self.client.on_connect = on_connect
        self.client.on_message = on_message
        self.client.connect("localhost", 1883, 60)
        print("Connected")
        self.client.loop_start()
    def __del__(self):
        print("Disconnect");
        self.client.loop_stop(force=False)
def main():
    GameLogic.left_rotate_base = False
    GameLogic.right_rotate_base = False
    GameLogic.lift_main_arm = False
    GameLogic.lower_main_arm = False
    GameLogic.lift_second_arm = False
    GameLogic.lower_second_arm = False
    GameLogic.left_rotate_head_mount = False
    GameLogic.right_rotate_head_mount = False
    GameLogic.lower_head = False
    GameLogic.lift_head = False
    GameLogic.grip_gripper = False
    GameLogic.release_gripper = False
    GameLogic.base_target_position = 0.0
    GameLogic.main_arm_target_position = 0.0
    GameLogic.second_arm_target_position = 0.0
    GameLogic.head_mount_target_position = 0.0
    GameLogic.head_target_position = 0.0
    GameLogic.gripper_target_position = 0.0
    GameLogic.slider_target_position = 0.0
    GameLogic.adjuster_target_position = 0.0
    GameLogic.platform_target_position = 0.0
    GameLogic.base_target_speed = 0.0
    GameLogic.main_arm_target_speed = 0.0
    GameLogic.second_arm_target_speed = 0.0
    GameLogic.head_mount_target_speed = 0.0
    GameLogic.head_target_speed = 0.0
    GameLogic.base_speed = 0.0
    GameLogic.main_arm_speed = 0.0
    GameLogic.second_arm_speed = 0.0
    GameLogic.head_mount_speed = 0.0
    GameLogic.head_speed = 0.0
    GameLogic.base_manual = False
    GameLogic.main_arm_manual = False
    GameLogic.second_arm_manual = False
    GameLogic.base_target = False
    GameLogic.main_arm_target = False
    GameLogic.second_arm_target = False
    GameLogic.head_mount_target = False
    GameLogic.head_target = False
    GameLogic.gripper_target = False
    GameLogic.slider_target = False
    GameLogic.adjuster_target = False
    GameLogic.platform_target = False
    GameLogic.greenCounter = 0
    GameLogic.redCounter = 0
    GameLogic.handContact = 0
    GameLogic.messageRate = 5
    GameLogic.secondaryMessageRate = 1
    GameLogic.messageRateCounter = 0
    GameLogic.secondaryMessageRateCounter = 0
    GameLogic.heatplateTemperature = 120 # degree Celsius
    GameLogic.heatplateTargetTemperature = 120 # degree Celsius

    # Paint QR-Code textures
    for i in range(1, 9):
        qr_code = bpy.data.images.load("//qr-codes/code-" + str(i) + ".png")
        material = bpy.data.objects["Object" + str(i)].active_material.copy()
        texture = bpy.data.objects["Object" + str(i)].active_material.active_texture.copy()
        texture.image = qr_code
        material.active_texture = texture
        bpy.data.objects["Object" + str(i)].active_material = material

    GameLogic.server = Server()
