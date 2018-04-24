import GameLogic

def main():

    if (GameLogic.messageRate > 0 and GameLogic.messageRateCounter > 30 / GameLogic.messageRate):
        base_joint = GameLogic.getCurrentScene().objects["base_joint"]
        main_arm = GameLogic.getCurrentScene().objects["main_arm"]
        second_arm = GameLogic.getCurrentScene().objects["second_arm"]
        wrist = GameLogic.getCurrentScene().objects["wrist"]
        hand = GameLogic.getCurrentScene().objects["BarrettHand"]
        gripper = GameLogic.getCurrentScene().objects["Finger1.1"]
        slider = GameLogic.getCurrentScene().objects["SliderPanel"]
        adjuster = GameLogic.getCurrentScene().objects["Adjuster1"]
        platform = GameLogic.getCurrentScene().objects["Platform"]

        base_position = base_joint.localOrientation.to_euler().z
        main_arm_position = main_arm.localOrientation.to_euler().y
        second_arm_position = second_arm.localOrientation.to_euler().y
        wrist_position = wrist.localOrientation.to_euler().x
        hand_position = hand.worldPosition
        gripper_position = gripper.localOrientation.to_euler().y
        slider_position = slider.worldPosition.x
        adjuster_position = adjuster.worldPosition.x
        platform_position = platform.localOrientation.to_euler().y

        heatplate_temperature = GameLogic.heatplateTemperature

        if (GameLogic.handContact % 2 == 1):
            hand_contact = True
        else:
            hand_contact = False

        message_roboticarm = """
        {
        "entity": "RoboticArm",
        "basePosition": %s,
        "mainArmPosition": %s,
        "secondArmPosition": %s,
        "wristPosition": %s,
        "gripperPosition": %s,
        "handPosition": "%s",
        "gripperHasContact": "%s"
        }
        """ % (
        str(base_position),
        str(main_arm_position),
        str(second_arm_position),
        str(wrist_position),
        str(gripper_position),
        str(hand_position),
        str(hand_contact)
        )

        message_slider = "{ \"entity\": \"Slider\", \"sliderPosition\": %s }" % (str(slider_position))
        message_adjuster = "{ \"entity\": \"Conveyor\", \"adjusterPosition\": %s }" % (str(adjuster_position))
        message_platform = "{ \"entity\": \"TestingRig\", \"platformPosition\": %s, \"heatplateTemperature\": %s}" % (str(platform_position), str(heatplate_temperature))

        GameLogic.server.client.publish("Sensor-Simulation", payload=message_roboticarm, qos=0, retain=False)
        GameLogic.server.client.publish("Sensor-Simulation", payload=message_slider, qos=0, retain=False)
        GameLogic.server.client.publish("Sensor-Simulation", payload=message_adjuster, qos=0, retain=False)
        GameLogic.server.client.publish("Sensor-Simulation", payload=message_platform, qos=0, retain=False)
        GameLogic.messageRateCounter = 0
    GameLogic.messageRateCounter = GameLogic.messageRateCounter + 1
