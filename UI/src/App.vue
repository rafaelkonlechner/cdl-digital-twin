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
        <button @click="showBlueprint = true; show3D = false;">Blueprint</button>
        <button @click="showBlueprint = false; show3D = true;">3D</button>
    </header>
    <blueprint v-if="showBlueprint" :socket="socket"></blueprint>
    <webgl style="text-align: center; margin: 0 auto" v-if="show3D" width="800" height="400"></webgl>
    <main style="height: 100%;">
        <div class="mdl-grid">
            <div class="mdl-cell--2-col">
                <inputs></inputs>
            </div>
            <div class="mdl-cell--10-col">
                <div class="mdl-grid">
                    <div class="mdl-cell--4-col">
                        <h2>Camera Signals</h2>
                        <camera-signal :socket="socket" topic="detectionCamera" title="Detection Camera"></camera-signal>
                        <camera-signal :socket="socket" topic="pickupCamera" :fixedBox="[35, 60, 113, 68, 'yellow', 'Pickup Window']" title="Pickup Camera"></camera-signal>
                    </div>
                    <div class="mdl-cell--4-col">
                        <work-item-information :socket="socket"></work-item-information>
                    </div>
                    <div class="mdl-cell--2-col">
                        <h2>Controls</h2>
                        <div style="width: 200px;">
                            <key-controls :socket="socket"></key-controls>
                            <div style="width: 150px;">Base: {{basePosition}} <input class="mdl-slider mdl-js-slider" type="range" min="0.01" max="3.14" step="0.001" v-model="basePosition" /></div>
                            <div style="width: 150px;">Main Arm: {{mainArmPosition}} <input class="mdl-slider mdl-js-slider" type="range" min="0.01" max="3.14" step="0.001" v-model="mainArmPosition" /></div>
                            <div style="width: 150px;">Second Arm: {{secondArmPosition}} <input class="mdl-slider mdl-js-slider" type="range" min="0.01" max="3.14" step="0.001" v-model="secondArmPosition" /></div>
                            <div style="width: 150px;">Second Arm Rotation: {{secondArmRotation}} <input class="mdl-slider mdl-js-slider" type="range" min="0.01" max="3.14" step="0.001" v-model="secondArmRotation" /></div>
                            <div style="width: 150px;">Wrist: {{wristPosition}} <input class="mdl-slider mdl-js-slider" type="range" min="0.01" max="3.14" step="0.001" v-model="wristPosition" /></div>
                            <div style="width: 150px;">Wrist Rotation: {{wristRotation}} <input class="mdl-slider mdl-js-slider" type="range" min="0.01" max="3.14" step="0.001" v-model="wristRotation" /></div>
                            <button @click="set()">Set</button>
                        </div>
                    </div>
                    <div class="mdl-cell--4-col">
                        <sensor-monitor :socket="socket ">
                        </sensor-monitor>
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
import SensorMonitor from "./SensorMonitor.vue";
import StateChart from "./StateChart.vue";
import WebGL from "./WebGL.vue";
import KeyControls from "./KeyControls.vue";
import BluePrint from "./Blueprint.vue";
import CameraSignal from "./CameraSignal.vue";
import WorkItemInformation from "./WorkItemInformation.vue";

let states = [{
        id: "idle",
        name: "Idle",
        basePosition: 1000,
        mainArmPosition: 210,
        secondArmPosition: 200,
        x: 100,
        y: 250
    },
    {
        id: "pickup",
        name: "Pickup",
        basePosition: 1000,
        mainArmPosition: 2000,
        secondArmPosition: 1380,
        x: 600,
        y: 250
    },
    {
        id: "move",
        name: "Move",
        basePosition: 2000,
        mainArmPosition: 1250,
        secondArmPosition: 2000,
        x: 1100,
        y: 250
    }
];
let transitions = [{
        id: "t1",
        from: states[0],
        to: states[1]
    },
    {
        id: "t2",
        from: states[1],
        to: states[2]
    }
];

export default {
    name: "app",
    components: {
        inputs: Inputs,
        sensorMonitor: SensorMonitor,
        keyControls: KeyControls,
        blueprint: BluePrint,
        cameraSignal: CameraSignal,
        webgl: WebGL,
        workItemInformation: WorkItemInformation
    },
    data() {
        return {
            socket: null,
            messageRate: 0,
            showBlueprint: true,
            show3D: false,
            states: states,
            transitions: transitions,
            selected: null,
            command: "base-goto 3.12 1.0",
            basePosition: 0.0,
            mainArmPosition: 0.0,
            secondArmPosition: 0.0,
            secondArmRotation: 0.0,
            wristPosition: 0.0,
            wristRotation: 0.0,
            gripperPosition: 0.0
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
    },
    methods: {
        sendSocketMessage: function(message) {
            this.socket.send(message);
        },
        sendCommand() {
            this.sendSocketMessage(this.command);
        },
        set() {
            this.sendSocketMessage("base-goto " + this.basePosition + " 1.0");
            this.sendSocketMessage("main-arm-goto " + this.mainArmPosition + " 1.0");
            this.sendSocketMessage(
                "second-arm-goto " + this.secondArmPosition + " 1.0"
            );
            this.sendSocketMessage(
                "second-arm-roto " + this.secondArmRotation + " 1.0"
            );
            this.sendSocketMessage("wrist-goto " + this.wristPosition + " 1.0");
            this.sendSocketMessage("wrist-roto " + this.wristRotation + " 1.0");
        }
    }
};
</script>
