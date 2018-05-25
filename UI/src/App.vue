<style scoped>
header {
    text-align: center;
    padding: 2vh;
}

header h1 {
    line-height: 1em;
}

footer {
    text-align: center;
    padding: 3vh;
    background-color: #f5f5f5;
    color: #9e9e9e;
    box-shadow: inset 0 0 1px #bdbdbd;
}

footer h5 {
    font-family: "Roboto Mono", monospace;
    line-height: 1.4;
}

footer h5 small {
    font-size: 0.7em;
}

main {
    width: 100%;
    height: 60vh;
}

article {
    width: 40vw;
    margin: 0 auto 5vh;
    text-align: justify;
}

.mdl-grid {
    width: 90vw;
}
</style>
<template>
<div id="app">
    <header>
        <h1>6-Axis Robotic Arm Twin
        <small>Kuka KR5-R850</small>
      </h1>
    </header>
    <blueprint :socket="socket" :context="context"></blueprint>
    <main style="height: 100%;">
        <div class="mdl-grid">
            <div class="mdl-cell--2-col">
                <inputs></inputs>
            </div>
            <div class="mdl-cell--10-col">
                <div class="mdl-grid">
                    <div class="mdl-cell--2-col">
                        <controls :socket="socket" :context="context"></controls>
                    </div>
                </div>
            </div>
        </div>
    </main>
    <hr/>
    <article>
        <h1>Project description</h1>
        <p>This
            <a href="https://cdl-mint.big.tuwien.ac.at/" target="_blank">CDL-MINT</a> project explores technologies for the model-integrated development and monitoring of production systems. This includes the simulation of a 3D robotic arm model in Blender
            GE, a MQTT message pipeline, a controller that runs on a server and the dashboard that can be seen above. A detailed description of the setup can be found on
            <a href="https://github.com/rafaelkonlechner/cdl-digital-twin">GitHub</a>.</p>
    </article>
    <footer>
        <h5>
        <strong>Christian Doppler Laboratory</strong><br/>
        <small>on Model-Integrated Smart Production</small>
      </h5>
    </footer>
</div>
</template>

<script>
import Inputs from "./Inputs.vue";
import Controls from "./Controls.vue";
import SensorMonitor from "./SensorMonitor.vue";
import StateChart from "./StateChart.vue";
import KeyControls from "./KeyControls.vue";
import BluePrint from "./Blueprint.vue";
import CameraSignal from "./CameraSignal.vue";

export default {
    name: "app",
    components: {
        inputs: Inputs,
        controls: Controls,
        sensorMonitor: SensorMonitor,
        keyControls: KeyControls,
        blueprint: BluePrint,
        cameraSignal: CameraSignal
    },
    data() {
        return {
            socket: null,
            messageRate: 0,
            context: {
                roboticArmState: {
                    name: "Snapshot",
                    basePosition: 0.0,
                    mainArmPosition: 0.0,
                    secondArmPosition: 0.0,
                    secondArmRotation: 0.0,
                    wristPosition: 0.0,
                    wristRotation: 0.0,
                    gripperPosition: 0.0,
                    gripperHasContact: false,
                    handPosition: {
                        x: 0.0,
                        y: 0.0,
                        z: 0.0
                    }
                },
                testingRigState: {
                    name: "Snapshot",
                    objectCategory: "Empty",
                    platformPosition: 0.0,
                    heatplateTemperature: 0.0,
                },
                conveyorState: {
                    name: "Snapshot",
                    adjusterPosition: 0.0,
                    detected: false,
                    inPickupWindow: false
                },
                sliderState: {
                    name: "",
                    sliderPosition: 0.0,
                }
            }
        };
    },
    watch: {
        messageRate: function(newValue) {
            this.sendSocketMessage("message-rate " + this.messageRate);
        }
    },
    created() {
        let self = this;
        this.socket = new WebSocket("ws://127.0.0.1:8080/websocket");
        this.socket.addEventListener("open", function(event) {
            console.log("Connected");
        });
        self.socket.addEventListener("message", function(event) {
            let msg = JSON.parse(event.data);
            if (msg.topic === "sensor") {
                let data = JSON.parse(msg.message)
                if (data.entity === "RoboticArm") {
                    self.context.roboticArmState.basePosition = data.basePosition;
                    self.context.roboticArmState.mainArmPosition = data.mainArmPosition;
                    self.context.roboticArmState.secondArmPosition = data.secondArmPosition;
                    self.context.roboticArmState.secondArmRotation = data.secondArmRotation;
                    self.context.roboticArmState.wristPosition = data.wristPosition;
                    self.context.roboticArmState.wristRotation = data.wristRotation;
                    self.context.roboticArmState.gripperPosition = data.gripperPosition;
                    self.context.roboticArmState.handPosition = data.handPosition;
                }
                if (data.entity === "TestingRig") {
                    self.context.testingRigState.objectCategory = data.objectCategory;
                    self.context.testingRigState.platformPosition = data.platformPosition;
                    self.context.testingRigState.heatplateTemperature = data.heatplateTemperature;
                }
            }
        });
    }
};
</script>
