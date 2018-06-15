import bge
import math
from math import pi
from robot.util import rad
import random
import GameLogic

def rot_direction(position, target):
    if position < target:
        x = position + pi
        y = target + pi
        if y - x > (2 * pi - y) + x:
            return "neg"
        else:
            return "pos"
    else:
        y = position + pi
        x = target + pi
        if y - x > (2*pi - y) + x:
            return "pos"
        else:
            return "neg"

def break_distance(speed, deceleration):
    x = speed
    dist = 0
    while (x > 0):
        dist += x
        x -= deceleration
    return dist

def calc_speed(position, target, speed, max_speed, acceleration, deceleration):
    if not math.isclose(position, target, rel_tol = acceleration, abs_tol = acceleration):
        distance = abs(target - position)
        direction = rot_direction(position, target)
        if distance > break_distance(abs(speed) + acceleration, deceleration):
            if direction == "pos":
                new_speed = min(speed + acceleration, max_speed)
            if direction == "neg":
                new_speed = max(speed - acceleration, -max_speed)
        else:
            if speed > 0:
                new_speed = max(0, speed - deceleration)
            if speed < 0:
                new_speed = min(0, speed + deceleration)
    else:
        new_speed = 0
    return new_speed
def main():
    base_joint = GameLogic.getCurrentScene().objects["base_joint"]
    main_arm = GameLogic.getCurrentScene().objects["main_arm"]
    second_arm = GameLogic.getCurrentScene().objects["second_arm"]
    head = GameLogic.getCurrentScene().objects["head"]
    head_mount = GameLogic.getCurrentScene().objects["head_mount"]
    gripper = GameLogic.getCurrentScene().objects["Finger1.1"]
    slider = GameLogic.getCurrentScene().objects["SliderPanel"]
    adjuster = GameLogic.getCurrentScene().objects["Adjuster1"]
    tilt = GameLogic.getCurrentScene().objects["Platform"]
    base_position = base_joint.localOrientation.to_euler().z
    main_arm_position = main_arm.localOrientation.to_euler().y
    second_arm_position = second_arm.localOrientation.to_euler().y
    head_mount_position = head_mount.localOrientation.to_euler().x
    head_position = head.localOrientation.to_euler().y
    gripper_position = gripper.localOrientation.to_euler().y
    slider_position = slider.worldPosition.x
    adjuster_position = adjuster.worldPosition.x
    platform_position = tilt.localOrientation.to_euler().y
    base_speed = GameLogic.base_speed
    base_target_speed = GameLogic.base_target_speed
    main_arm_speed = GameLogic.main_arm_speed
    main_arm_target_speed = GameLogic.main_arm_target_speed
    second_arm_speed = GameLogic.second_arm_speed
    second_arm_target_speed = GameLogic.second_arm_target_speed
    head_mount_speed = GameLogic.head_mount_speed
    head_mount_target_speed = GameLogic.head_mount_target_speed
    head_speed = GameLogic.head_speed
    head_target_speed = GameLogic.head_target_speed

    acceleration = 0.000873 # 0.05° per frame (30 frames or 0.5 sec to top speed)
    deceleration = acceleration # 0.05° per frame (15 frames or 0.25 sec from top speed to zero)

    if GameLogic.left_rotate_base:
        GameLogic.sendMessage("left-rotate-base")
        GameLogic.base_target_position = base_joint.localOrientation.to_euler().z
    if GameLogic.right_rotate_base:
        GameLogic.sendMessage("right-rotate-base")
        GameLogic.base_target_position = base_joint.localOrientation.to_euler().z
    if GameLogic.lift_main_arm:
        GameLogic.sendMessage("lower-main-arm")
        GameLogic.main_arm_target_position = main_arm.localOrientation.to_euler().y
    if GameLogic.lower_main_arm:
        GameLogic.sendMessage("lift-main-arm")
        GameLogic.main_arm_target_position = main_arm.localOrientation.to_euler().y
    if GameLogic.lift_second_arm:
        GameLogic.sendMessage("lift-second-arm")
        GameLogic.second_arm_target_position = second_arm.localOrientation.to_euler().y
    if GameLogic.lower_second_arm:
        GameLogic.sendMessage("lower-second-arm")
        GameLogic.second_arm_target_position = second_arm.localOrientation.to_euler().y
    if GameLogic.left_rotate_head_mount:
        GameLogic.sendMessage("left-rotate-head-mount")
        GameLogic.head_mount_target_position = head_mount.localOrientation.to_euler().x
    if GameLogic.right_rotate_head_mount:
        GameLogic.sendMessage("right-rotate-head-mount")
        GameLogic.head_mount_target_position = head_mount.localOrientation.to_euler().x
    if GameLogic.lift_head:
        GameLogic.sendMessage("lift-head")
        GameLogic.head_target_position = head.localOrientation.to_euler().y
    if GameLogic.lower_head:
        GameLogic.sendMessage("lower-head")
        GameLogic.head_target_position = head.localOrientation.to_euler().y
    if GameLogic.grip_gripper:
        GameLogic.sendMessage("grip-gripper")
        GameLogic.gripper_target_position = gripper.localOrientation.to_euler().x
    if GameLogic.release_gripper:
        GameLogic.sendMessage("release-gripper")
        GameLogic.gripper_target_position = gripper.localOrientation.to_euler().x

    base_speed = calc_speed(base_position, GameLogic.base_target_position, base_speed, rad(base_target_speed), acceleration, deceleration)
    if base_speed == 0:
        GameLogic.base_target = True

    main_arm_speed = calc_speed(main_arm_position, GameLogic.main_arm_target_position, main_arm_speed, rad(main_arm_target_speed), acceleration, deceleration)
    if main_arm_speed == 0:
        GameLogic.main_arm_target = True

    second_arm_speed = calc_speed(second_arm_position, GameLogic.second_arm_target_position, second_arm_speed, rad(second_arm_target_speed), acceleration, deceleration)
    if second_arm_speed == 0:
        GameLogic.second_arm_target = True

    head_mount_speed = calc_speed(head_mount_position, GameLogic.head_mount_target_position, head_mount_speed, rad(head_mount_target_speed), acceleration, deceleration)
    if head_mount_speed == 0:
        GameLogic.head_mount_target = True

    head_speed = calc_speed(head_position, GameLogic.head_target_position, head_speed, rad(head_target_speed), acceleration, deceleration)
    if head_speed == 0:
        GameLogic.head_position_target = True

    base_joint.applyRotation((0, 0, base_speed), True)
    GameLogic.base_speed = base_speed
    main_arm.applyRotation((0, main_arm_speed, 0), True)
    GameLogic.main_arm_speed = main_arm_speed
    second_arm.applyRotation((0, second_arm_speed, 0), True)
    GameLogic.second_arm_speed = second_arm_speed
    head_mount.applyRotation((head_mount_speed, 0, 0), True)
    GameLogic.head_mount_speed = head_mount_speed
    head.applyRotation((0, head_speed, 0), True)
    GameLogic.head_speed = head_speed

    if math.isclose(gripper_position, GameLogic.gripper_target_position, rel_tol=0.017, abs_tol=0.017):
        GameLogic.gripper_target = False
    if GameLogic.gripper_target and gripper_position < GameLogic.gripper_target_position:
        GameLogic.sendMessage("release-gripper")
    if GameLogic.gripper_target and gripper_position > GameLogic.gripper_target_position:
        GameLogic.sendMessage("grip-gripper")

    if math.isclose(slider_position, GameLogic.slider_target_position, rel_tol=0.002, abs_tol=0.002):
        GameLogic.slider_target = False
    if GameLogic.slider_target and slider_position < GameLogic.slider_target_position:
        GameLogic.sendMessage("push-slider")
    if GameLogic.slider_target and slider_position > GameLogic.slider_target_position:
        GameLogic.sendMessage("backup-slider")

    if math.isclose(adjuster_position, GameLogic.adjuster_target_position, rel_tol=0.003, abs_tol=0.003):
        GameLogic.adjuster_target = False
    if GameLogic.adjuster_target and adjuster_position < GameLogic.adjuster_target_position:
        GameLogic.sendMessage("push-adjuster")
    if GameLogic.adjuster_target and adjuster_position > GameLogic.adjuster_target_position:
        GameLogic.sendMessage("backup-adjuster")


    if math.isclose(platform_position, GameLogic.platform_target_position, rel_tol=0.003, abs_tol=0.003):
        GameLogic.platform_target = False
    if GameLogic.platform_target and platform_position > GameLogic.platform_target_position:
        GameLogic.sendMessage("tilt-platform")
    if GameLogic.platform_target and platform_position < GameLogic.platform_target_position:
        GameLogic.sendMessage("untilt-platform")

    if not math.isclose(GameLogic.heatplateTemperature, GameLogic.heatplateTargetTemperature, rel_tol=0.0001, abs_tol=0.05):
        if GameLogic.heatplateTemperature < GameLogic.heatplateTargetTemperature:
            GameLogic.heatplateTemperature += random.uniform(0, 0.1)
        else:
            GameLogic.heatplateTemperature -= random.uniform(0, 0.1)
