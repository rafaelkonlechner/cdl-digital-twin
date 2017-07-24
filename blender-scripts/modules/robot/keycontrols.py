import GameLogic

def main():
    if GameLogic.left_rotate_base:
        GameLogic.sendMessage("left-rotate-base")
    if GameLogic.right_rotate_base:
        GameLogic.sendMessage("right-rotate-base")
    if GameLogic.lift_main_arm:
        GameLogic.sendMessage("lower-main-arm")
    if GameLogic.lower_main_arm:
        GameLogic.sendMessage("lift-main-arm")
    if GameLogic.lift_second_arm:
        GameLogic.sendMessage("lift-second-arm")
    if GameLogic.lower_second_arm:
        GameLogic.sendMessage("lower-second-arm")
    if GameLogic.left_rotate_wrist:
        GameLogic.sendMessage("left-rotate-wrist")
    if GameLogic.right_rotate_wrist:
        GameLogic.sendMessage("right-rotate-wrist")
    if GameLogic.grip_gripper:
        GameLogic.sendMessage("grip-gripper")
    if GameLogic.release_gripper:
        GameLogic.sendMessage("release-gripper")
