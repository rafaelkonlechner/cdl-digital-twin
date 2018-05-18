<style scoped>
.blueprint {
    position: relative;
    text-align: center;
    color: white;
    width: 100vw;
    padding: 10px;
    background-color: #1E88E5;
    box-shadow: inset 0 0 5px #000000;
}

.blueprint-main {
    width: 85vw;
    margin: 0 auto;
}

.blueprint input {
    width: 40px;
}

.blueprint-img {
    width: 30%;
    height: 100%;
    display: inline-block;
    vertical-align: bottom;
}

.blueprint-text {
    color: white;
    font-family: 'Roboto Mono', monospace;
    margin: 0;
    line-height: 1;
}

.control-table {
    text-align: left;
    margin: 10% 5px;
    width: 100%;
    height: 100%;
    font-size: 11px;
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
    <div class="absolute blueprint-text small" style="top: 56%; left: 44%; height: 35px; text-align: left;">
        x: {{context.roboticArmState.handPosition.x.toFixed(2)}}
        <br/> y: {{context.roboticArmState.handPosition.y.toFixed(2)}}
        <br/> z: {{context.roboticArmState.handPosition.z.toFixed(2)}}
    </div>
    <div class="absolute blueprint-text small" style="top: 33%; left: 44%; height: 35px; text-align: left;">
        contact: {{context.roboticArmState.gripperHasContact}}
    </div>
    <div class="absolute blueprint-text small" style="top: 31%; left: 22%; height: 35px;">
        Detection Camera
    </div>
    <div class="absolute blueprint-text small" style="top: 51%; left: 24%; height: 35px;">
        Plate Temperature: {{context.testingRigState.heatplateTemperature.toFixed(2)}}°C
    </div>
    <div class="absolute blueprint-text small" style="top: 34%; left: 56%; height: 35px;">
        Pickup Camera
    </div>
    <div class="blueprint-main">
        <div class="mdl-grid">
            <div class="mdl-cell mdl-cell--9-col">
                <div class="blueprint-img">
                    <h2 class="blueprint-text">Testing Rig
                        <br/>
                        <small>&mdash; {{context.testingRigState.name}}</small>
                    </h2>
                    <testing-rig-model></testing-rig-model>
                </div>
                <div class="blueprint-img">
                    <h2 class="blueprint-text">KR5-R850
                        <br/>
                        <small>&mdash; {{context.roboticArmState.name}}</small>
                    </h2>
                    <robotic-arm-model></robotic-arm-model>
                </div>
                <div class="blueprint-img">
                    <h2 class="blueprint-text">Conveyor
                        <br/>
                        <small>&mdash; {{context.conveyorState.name}}</small>
                    </h2>
                    <conveyor-model></conveyor-model>
                </div>
            </div>
            <div class="mdl-cell mdl-cell--2-col mdl-cell--1-offset">
                <h2 class="blueprint-text">Controls</h2>
                <button class="tabs" v-bind:class="{'active-tab': !manualMode}" style="width: 68px" @click="manualMode = false">Automatic
                </button>
                <button class="tabs" v-bind:class="{'active-tab': manualMode}" @click="manualMode = true">
                    Manual
                </button>
                <div class="tab" v-if="!manualMode">
                    <state-machine></state-machine>
                    <div style="height: 36px;">
                        <button class="tabs active-tab" style="width: 68px" @click="toggleAutoPlay()">
                                <span v-if="!autoPlay">
                                    <i class="material-icons"
                                       style="font-size: 10px; position: relative; top: 1px;">play_arrow</i>
                                    Start</span>
                            <span v-if="autoPlay">
                                    <i class="material-icons"
                                       style="font-size: 10px; position: relative; top: 1px;">stop</i>
                                    Stop</span>
                        </button>
                    </div>
                </div>
                <div class="tab" v-if="manualMode">
                    <table class="control-table">
                        <thead>
                            <tr>
                                <td>
                                    <strong>Joint</strong>
                                </td>
                                <td>
                                    <strong>Current</strong>
                                </td>
                                <td>
                                    <strong>Target</strong>
                                </td>
                            </tr>
                        </thead>
                        <tr>
                            <td>Base</td>
                            <td>{{basePosition}}</td>
                            <td>
                                <input v-model="baseTargetPosition" type="number" step="0.01" />
                                <button @click="baseGoto()">Set</button>
                            </td>
                        </tr>
                        <tr>
                            <td>Main Arm</td>
                            <td>{{mainArmPosition}}</td>
                            <td>
                                <input v-model="mainArmTargetPosition" type="number" step="0.01" />
                                <button @click="mainArmGoto()">Set</button>
                            </td>
                        </tr>
                        <tr>
                            <td>Forearm</td>
                            <td>{{secondArmPosition}}</td>
                            <td>
                                <input v-model="secondArmTargetPosition" type="number" step="0.01" />
                                <button @click="secondArmGoto()">Set</button>
                            </td>
                        </tr>
                        <tr>
                            <td>Wrist</td>
                            <td>{{wristPosition}}</td>
                            <td>
                                <input v-model="wristTargetPosition" type="number" step="0.01" />
                                <button @click="wristGoto()">Set</button>
                            </td>
                        </tr>
                        <tr>
                            <td>Gripper</td>
                            <td>{{gripperPosition}}</td>
                            <td>
                                <input v-model="gripperTargetPosition" type="number" step="0.01" />
                                <button @click="gripperGoto()">Set</button>
                                <small v-if="gripperHasContact">has-contact</small>
                            </td>
                        </tr>
                        <tr>
                            <td>Slider</td>
                            <td>{{sliderPosition}}</td>
                            <td>
                                <button>Push</button>
                                <button>Pull</button>
                            </td>
                        </tr>
                        <tr>
                            <td>Adjuster</td>
                            <td>{{adjusterPosition}}</td>
                            <td>
                                <button>Push</button>
                                <button>Pull</button>
                            </td>
                        </tr>
                        <tr>
                            <td>Platform</td>
                            <td>{{platformPosition}}</td>
                            <td>
                                <button>Tilt</button>
                                <button>Level</button>
                            </td>
                        </tr>
                    </table>
                    <div style="height: 36px"></div>
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<script>
import Conveyor from './models/Conveyor.vue'
import RoboticArm from './models/RoboticArm.vue'
import TestingRig from './models/TestingRig.vue'
import StateMachine from './StateMachine.vue'
import * as request from 'superagent'

export default {
    components: {
        conveyorModel: Conveyor,
        roboticArmModel: RoboticArm,
        testingRigModel: TestingRig,
        stateMachine: StateMachine
    },
    props: ["socket"],
    data() {
        return {
            autoPlay: false,
            manualMode: false,
            context: {
                roboticArmState: {
                    name: "Snapshot",
                    basePosition: 0.0,
                    mainArmPosition: 0.0,
                    secondArmPosition: 0.0,
                    wristPosition: 0.0,
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
            },
            savedPositions: [],
            savePositionName: "",
            baseTargetPosition: 0.0,
            baseTargetSpeed: 1.0,
            mainArmTargetSpeed: 1.0,
            secondArmTargetSpeed: 1.0,
            mainArmTargetPosition: 0.0,
            secondArmTargetPosition: 0.0,
            wristTargetPosition: 0.0,
            gripperTargetPosition: 0.0,
            sliderTargetPosition: 0.0,
            adjusterTargetPosition: 0.0,
            platformTargetPosition: 0.0
        };
    },
    mounted() {
        var self = this;
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
                    self.context.roboticArmState.handPosition = data.handPosition;
                }
                if (data.entity === "TestingRig") {
                    self.context.testingRigState.objectCategory = data.objectCategory;
                    self.context.testingRigState.platformPosition = data.platformPosition;
                    self.context.testingRigState.heatplateTemperature = data.heatplateTemperature;
                }
            }
            if (msg.topic === "context") {
                let data = JSON.parse(msg.message)
                //self.context = data;
            }
        });
    },
    methods: {
        toggleAutoPlay: function() {
            console.log("Toggle AutoPlay");
            this.autoPlay = !this.autoPlay;
            var self = this;
            request.put("http://localhost:8080/autoPlay")
                .set('Content-Type', 'application/json')
                .send(JSON.stringify(self.autoPlay))
                .end();
        },
        toggleRecording: function() {
            console.log("Toggle Recordindg");
            this.recording = !this.recording;
            var self = this;
            request.put("http://localhost:8080/recording")
                .set('Content-Type', 'application/json')
                .send(JSON.stringify(self.recording))
                .end();
        }
    }
}
</script>
