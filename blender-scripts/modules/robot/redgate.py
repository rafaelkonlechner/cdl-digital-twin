import GameLogic
import bge
import random

def main():
    GameLogic.server.client.publish("Sensor", payload="{\"entity\": \"RedGate\", \"event\": \"object-detected\"}", qos=0, retain=False)

    GameLogic.redCounter += 1
    if (GameLogic.redCounter % 2 == 1):
        controller = bge.logic.getCurrentController()
        owner = controller.owner
        scene = bge.logic.getCurrentScene()
        objects = scene.objectsInactive[1:]
        choice = random.choice(objects)
        hello = scene.addObject(choice, owner, 0)
        hello.worldPosition = choice.worldPosition
        hello.localScale = [1.0,1.0,1.0]
