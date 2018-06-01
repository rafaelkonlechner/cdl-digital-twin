<style scoped>
.blueprint {
    position: relative;
    text-align: center;
    color: white;
    width: 100vw;
    min-height: 35vh;
    padding: 10px;
    background-color: #1E88E5;
    box-shadow: inset 0 0 5px #000000;
}

.blueprint-main {
    width: 85vw;
    margin: 0 auto;
}

.blueprint-img {
    width: 26%;
    height: 100%;
    margin-left: 3%;
    margin-right: 3%;
    display: inline-block;
    vertical-align: bottom;
}

.blueprint-text {
    color: white;
    font-family: 'Roboto Mono', monospace;
    margin: 0;
    line-height: 1;
}

.absolute {
    position: absolute;
    z-index: 10;
}

.small {
    font-size: 0.8em;
    line-height: 1.2em;
}

.tabs {
    border-radius: 5px;
    border: 2px solid white;
    color: white;
    padding: 2px 5px;
    margin-top: 10px;
    margin-bottom: 5px;
    display: inline-block;
    font-size: 10px;
    background-color: #1E88E5;
}

.active-tab {
    color: #1E88E5;
    background-color: white;
}
</style>
<template>
<div class="blueprint">
    <button class="tabs" v-bind:class="{'active-tab': show2D}" @click="show2D = true; show3D = false;">Default</button>
    <button class="tabs" v-bind:class="{'active-tab': show3D}" @click="show2D = false; show3D = true;">3D</button>
    <div class="absolute" style="top: 30%; left: 4%;">
        <work-item-information :socket="socket"></work-item-information>
    </div>
    <div class="absolute" style="top: 30%; left: 84%;">
        <h2>Running Jobs</h2>
    </div>
    <div v-if="selected" class="absolute blueprint-text small" style="top: 14%; left: 85%; height: 35px; text-align: left;">
        <h2>{{selected.name}}</h2>
        <p><small>Current: {{selected.value}}</small></p>
        <p><small>Target: <input style="width: 50px;" v-model="selected.target"></input></small>
        </p>
        <!--<line-chart width="200" height="100" min="0" max="100" :data="data"></line-chart>-->
    </div>
    <div class="blueprint-main">
        <div class="mdl-grid">
            <div v-if="show2D" class="mdl-cell mdl-cell--10-col mdl-cell--1-offset">
                <div class="absolute blueprint-text small" style="top: 56%; left: 54%; height: 35px; text-align: left;">
                    <!--x: {{context.roboticArmState.handPosition.x.toFixed(2)}}
                    <br/> y: {{context.roboticArmState.handPosition.y.toFixed(2)}}
                    <br/> z: {{context.roboticArmState.handPosition.z.toFixed(2)}}-->
                </div>
                <div class="absolute" style="top: 25%; left: 33%;">
                    <camera-signal v-if="showDetectionCamera" :socket="socket" topic="detectionCamera" title="Detection Camera"></camera-signal>
                </div>
                <div class="absolute blueprint-text small" style="top: 36%; left: 54%; height: 35px; text-align: left;">
                    contact: {{context.roboticArmState.gripperHasContact}}
                </div>
                <div class="absolute blueprint-text small" style="top: 31%; left: 21%; height: 35px;">
                    Detection Camera
                </div>
                <div class="absolute blueprint-text small" style="top: 64%; left: 32.5%; height: 35px;">
                    Plate Temperature: {{context.testingRigState.heatplateTemperature.toFixed(2)}}°C
                </div>
                <div class="absolute blueprint-text small" style="top: 34%; left: 63%; height: 35px;">
                    Pickup Camera
                </div>
                <div class="absolute" style="top: 27%; left: 72%;">
                    <camera-signal v-if="showPickupCamera" :socket="socket" topic="pickupCamera" :fixedBox="[26, 45, 85, 51, 'yellow', 'Pickup Window']" title="Pickup Camera"></camera-signal>
                </div>
                <div class="blueprint-img">
                    <h2 class="blueprint-text">Testing Rig
                        <br/>
                        <small>&mdash; {{context.testingRigState.name}}</small>
                    </h2>
                    <testing-rig-model :click="toggleDetectionCamera"></testing-rig-model>
                </div>
                <div class="blueprint-img">
                    <h2 class="blueprint-text">KR5-R850
                        <br/>
                        <small>&mdash; {{context.roboticArmState.name}}</small>
                    </h2>
                    <robotic-arm-model v-on:select="selectionChanged($event)"></robotic-arm-model>
                </div>
                <div class="blueprint-img">
                    <h2 class="blueprint-text">Conveyor
                        <br/>
                        <small>&mdash; {{context.conveyorState.name}}</small>
                    </h2>
                    <conveyor-model :click="togglePickupCamera"></conveyor-model>
                </div>
            </div>
            <webgl v-if="show3D" v-on:select="selectionChanged($event)" style="text-align: center; margin: 0 auto" width="800" height="360"></webgl>
        </div>
    </div>
</div>
</template>

<script>
import CameraSignal from './CameraSignal.vue'
import Conveyor from './models/Conveyor.vue'
import RoboticArm from './models/RoboticArm.vue'
import TestingRig from './models/TestingRig.vue'
import WorkItemInformation from "./WorkItemInformation.vue";
import LineChart from "./LineChart.vue";
import WebGL from "./WebGL.vue";
import * as request from 'superagent'

export default {
    components: {
        cameraSignal: CameraSignal,
        conveyorModel: Conveyor,
        lineChart: LineChart,
        roboticArmModel: RoboticArm,
        testingRigModel: TestingRig,
        workItemInformation: WorkItemInformation,
        webgl: WebGL
    },
    props: ["socket", "context"],
    data() {
        return {
            selected: null,
            show2D: true,
            show3D: false,
            showDetectionCamera: false,
            showPickupCamera: false,
            data: [{
                x: 1,
                y: 10
            }, {
                x: 2,
                y: 20
            }]
        }
    },
    methods: {
        sendSocketMessage: function(message) {
            console.log("Send: " + message);
            this.socket.send(message);
        },
        toggleDetectionCamera() {
            this.showDetectionCamera = !this.showDetectionCamera
        },
        togglePickupCamera() {
            this.showPickupCamera = !this.showPickupCamera
        },
        selectionChanged(event) {
            if (event != null) {
                this.selected = event;
            } else {
                this.selected = null
            }
        }
    }
}
</script>
