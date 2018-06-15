import bge
import GameLogic
import VideoTexture
import base64
from PIL import Image
from io import BytesIO
import time
import threading
import copy

class Worker: #(threading.Thread):

    def __init__(self, args):
        self.args = args
        #threading.Thread.__init__(self)

    def base64_rgb(self, array, imgX, imgY):
        image = Image.new('RGB', (imgX, imgY), 0)
        image.putdata([tuple(array[(i):(i)+3]) for i in range(0, imgX*imgY*4, 4)], scale = 1.0, offset = 0.0)
        buffered = BytesIO()
        image.save(buffered, format="PNG")
        img_str = base64.b64encode(buffered.getvalue())
        return img_str

    def run(self, imgDetectorX, imgDetectorY, imgPickupX, imgPickupY):
        rgb_detector = self.base64_rgb(GameLogic.detector_png, imgDetectorX, imgDetectorY)
        rgb_pickup = self.base64_rgb(GameLogic.pickup_png, imgPickupX, imgPickupY)
        GameLogic.server.client.publish("DetectionCamera", payload=rgb_detector, qos=0, retain=False)
        GameLogic.server.client.publish("PickupCamera", payload=rgb_pickup, qos=0, retain=False)

def main():
    imgDetectorX= 64*5
    imgDetectorY = 36*5
    imgPickupX = 256
    imgPickupY = 144
    if hasattr(GameLogic, 'detector') and hasattr(GameLogic, 'pickup'):
        GameLogic.detector.refresh(True)
        GameLogic.pickup.refresh(True)
        GameLogic.detector_png = list(GameLogic.detector.source.image)
        GameLogic.pickup_png = list(GameLogic.pickup.source.image)
        thread = Worker(None)
        thread.run(imgDetectorX, imgDetectorY, imgPickupX, imgPickupY)
    else:
        scene = GameLogic.getCurrentScene()
        screen1 = scene.objects['Screen1']
        screen2 = scene.objects['Screen2']
        detectorcamera = scene.objects['DetectionCamera']
        pickupcamera = scene.objects['PickupCamera']
        GameLogic.detector = bge.texture.Texture(screen1, 0, 0)
        GameLogic.pickup = bge.texture.Texture(screen2, 0, 0)

        GameLogic.detector.source = VideoTexture.ImageRender(scene, detectorcamera)
        GameLogic.detector.source.capsize = (imgDetectorX, imgDetectorY)
        GameLogic.detector_png = GameLogic.detector.source.image[:]

        GameLogic.pickup.source = VideoTexture.ImageRender(scene, pickupcamera)
        GameLogic.pickup.source.capsize = (imgPickupX, imgPickupY)
        GameLogic.pickup_png = GameLogic.pickup.source.image[:]
