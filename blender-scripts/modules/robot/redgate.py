import GameLogic

def main():
    GameLogic.server.client.publish("Sensor", payload="red-gate object-detected", qos=0, retain=False)
