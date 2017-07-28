import GameLogic

def main():
    GameLogic.server.client.publish("Sensor", payload="{\"entity\": \"RedGate\", \"event\": \"object-detected\"}", qos=0, retain=False)
