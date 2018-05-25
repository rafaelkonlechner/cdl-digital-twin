<style scoped>
.control-table {
    text-align: left;
    margin: 10% 5px;
    width: 100%;
    height: 100%;
    font-size: 11px;
}

input {
    width: 40px;
}
</style>
<template>
<div>
    <h2>Controls</h2>
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
    <button class="tabs active-tab" @click="gotoIdle()">Idle</button>
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
            <td>{{context.roboticArmState.basePosition.toFixed(2)}}</td>
            <td>
                <input v-model="baseTargetPosition" type="number" step="0.01" />
                <button @click="baseGoto()">Set</button>
            </td>
        </tr>
        <tr>
            <td>Main Arm</td>
            <td>{{context.roboticArmState.mainArmPosition.toFixed(2)}}</td>
            <td>
                <input v-model="mainArmTargetPosition" type="number" step="0.01" />
                <button @click="mainArmGoto()">Set</button>
            </td>
        </tr>
        <tr>
            <td>Second Arm</td>
            <td>{{context.roboticArmState.secondArmPosition.toFixed(2)}}</td>
            <td>
                <input v-model="secondArmTargetPosition" type="number" step="0.01" />
                <button @click="secondArmGoto()">Set</button>
            </td>
        </tr>
        <tr>
            <td>Second Arm Rot.</td>
            <td>{{context.roboticArmState.secondArmRotation.toFixed(2)}}</td>
            <td>
                <input v-model="secondArmTargetRotation" type="number" step="0.01" />
                <button @click="secondArmRoto()">Set</button>
            </td>
        </tr>
        <tr>
            <td>Wrist</td>
            <td>{{context.roboticArmState.wristPosition.toFixed(2)}}</td>
            <td>
                <input v-model="wristTargetPosition" type="number" step="0.01" />
                <button @click="wristGoto()">Set</button>
            </td>
        </tr>
        <tr>
            <td>Wrist Rot.</td>
            <td>{{context.roboticArmState.wristRotation.toFixed(2)}}</td>
            <td>
                <input v-model="wristTargetRotation" type="number" step="0.01" />
                <button @click="wristRoto()">Set</button>
            </td>
        </tr>
        <tr>
            <td>Gripper</td>
            <td>{{context.roboticArmState.gripperPosition.toFixed(2)}}</td>
            <td>
                <input v-model="gripperTargetPosition" type="number" step="0.01" />
                <button @click="gripperGoto()">Set</button>
            </td>
        </tr>
        <tr>
            <td>Slider</td>
            <td>{{context.sliderState.sliderPosition.toFixed(2)}}</td>
            <td>
                <button>Push</button>
                <button>Pull</button>
            </td>
        </tr>
        <tr>
            <td>Adjuster</td>
            <td>{{context.conveyorState.adjusterPosition.toFixed(2)}}</td>
            <td>
                <button>Push</button>
                <button>Pull</button>
            </td>
        </tr>
        <tr>
            <td>Platform</td>
            <td>{{context.testingRigState.platformPosition.toFixed(2)}}</td>
            <td>
                <button>Tilt</button>
                <button>Level</button>
            </td>
        </tr>
    </table>
</div>
</template>

<script>
import KeyControls from './KeyControls.vue'
export default {
    components: {
        keyControls: KeyControls
    },
    name: "controls",
    props: ["socket", "context"],
    data() {
        return {
            basePosition: 0.0,
            mainArmPosition: 0.0,
            secondArmPosition: 0.0,
            secondArmRotation: 0.0,
            wristPosition: 0.0,
            wristRotation: 0.0,
            gripperPosition: 0.0,


            autoPlay: false,
            manualMode: false,
            savedPositions: [],
            savePositionName: "",
            baseTargetPosition: 0.0,
            baseTargetSpeed: 1.0,
            mainArmTargetSpeed: 1.0,
            secondArmTargetSpeed: 1.0,
            mainArmTargetPosition: 0.0,
            secondArmTargetPosition: 0.0,
            secondArmTargetRotation: 0.0,
            wristTargetPosition: 0.0,
            wristTargetRotation: 0.0,
            gripperTargetPosition: 0.0,
            sliderTargetPosition: 0.0,
            adjusterTargetPosition: 0.0,
            platformTargetPosition: 0.0
        };
    },
    methods: {
        sendSocketMessage: function(message) {
            this.socket.send(message);
        },
        toggleAutoPlay: function() {
            console.log("Toggle AutoPlay");
            this.autoPlay = !this.autoPlay;
            var self = this;
            request.put("http://localhost:8080/autoPlay")
                .set('Content-Type', 'application/json')
                .send(JSON.stringify(self.autoPlay))
                .end();
        },
        gotoIdle() {
            this.sendSocketMessage("idle");
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
            this.sendSocketMessage("gripper-goto " + this.gripperPosition + " 1.0");
        },
        baseGoto() {
            this.sendSocketMessage("base-goto " + this.baseTargetPosition + " 1.0");
        },
        mainArmGoto() {
            this.sendSocketMessage("main-arm-goto " + this.mainArmTargetPosition + " 1.0");
        },
        secondArmGoto() {
            this.sendSocketMessage(
                "second-arm-goto " + this.secondArmTargetPosition + " 1.0"
            );
        },
        secondArmRoto() {
            this.sendSocketMessage(
                "second-arm-roto " + this.secondArmTargetRotation + " 1.0"
            );
        },
        wristGoto() {
            this.sendSocketMessage("wrist-goto " + this.wristTargetPosition + " 1.0");
        },
        wristRoto() {
            this.sendSocketMessage("wrist-roto " + this.wristTargetRotation + " 1.0");
        },
        gripperGoto() {
            this.sendSocketMessage("gripper-goto " + this.gripperTargetPosition + " 1.0");
        }
    }
};
</script>
