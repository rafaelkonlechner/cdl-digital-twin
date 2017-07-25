import GameLogic

def main():
    GameLogic.server.client.publish("Sensor", payload="hand-plate object-detected", qos=0, retain=False)
