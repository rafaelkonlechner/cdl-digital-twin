import GameLogic

def main():
    GameLogic.server.client.publish("Sensor", payload="green-gate object-detected", qos=0, retain=False)
