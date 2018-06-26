<style scoped>
h1 {
    margin-top: 0px;
    margin-bottom: 0px;
}

p {
    font-size: 11px;
    line-height: 30px;
    margin-bottom: 0px;
}

input {
    width: 45px;
    float: right;
}

.third {
    width: 28%;
    padding: 2%;
    display: inline-block;
    vertical-align: top;
}

.attribute {
    font-family: 'Roboto Mono' monospace;
}

.disabled {
    opacity: 0.5;
}
</style>
<template>
<div>
    <h1>{{state.name}}</h1>
    <div class="third">
        <h2>Testing Rig <input type="checkbox" v-model="testingRig"></h2>
        <div v-bind:class="{ disabled: !testingRig}">
            <h3>Sensors</h3>
            <p>QR Code:
                <select style="float: right;" v-model="state.environment.testingRigState.objectCategory" v-if="testingRig">
                    <option value="NONE">None</option>
                    <option value="GREEN">Class 1</option>
                    <option value="RED">Class 2</option>
                </select>
            </p>
            <h3>Actuators</h3>
            <p>Plate Temp.: <input type="number" step="0.1" style="width: 45px;" v-if="testingRig" v-model="state.environment.testingRigState.heatplateTemperature">°C</p>
        </div>
    </div>
    <div class="third">
        <h2>Robotic Arm</h2>
        <div v-bind:class="{ disabled: !roboticArm}">
            <h3>Actuators</h3>
            <div v-if="state.environment.roboticArmState">
                <p>Base: <input v-model="state.environment.roboticArmState.basePosition" type="number" step="0.01"></p>
                <p>Main Arm: <input v-model="state.environment.roboticArmState.mainArmPosition" type="number" step="0.01"></p>
                <p>Second Arm: <input v-model="state.environment.roboticArmState.secondArmPosition" type="number" step="0.01"></p>
                <p>Head: <input v-model="state.environment.roboticArmState.headPosition" type="number" step="0.01"></p>
                <p>Head Mount: <input v-model="state.environment.roboticArmState.headMountPosition" type="number" step="0.01"></p>
                <p>Gripper: <select style="float: right;" v-model="state.environment.roboticArmState.gripperPosition" :disabled="!roboticArm"><option value="1.50">Open</option><<option value="-0.40">Closed</option></select></p>
                <button @click="move()">Move to Position</button>
            </div>
            <div v-if="isEmpty(state.environment)" style="margin-bottom: 10px;">
                <p>Use the <b>arrow keys</b> to move the robot.</p>
                <key-controls :socket="socket"></key-controls>
                <button style="font-size: 1.2em;" @click="savePosition()">Save</button>
            </div>
        </div>
    </div>
    <div class="third">
        <h2>Conveyor <input type="checkbox" v-model="conveyor"></h2>
        <div v-bind:class="{ disabled: !conveyor}">
            <h3>Sensors</h3>
            <p>Object Detectected: <input v-if="conveyor" style="float: right;" type="checkbox" v-model="state.environment.conveyorState.detected"></p>
            <p>Object In Window: <input v-if="conveyor" style="float: right;" type="checkbox" v-model="state.environment.conveyorState.inPickupWindow"></p>
            <h3>Actuators</h3>
            <p>Adjuster: <select v-if="conveyor" style="float: right;" v-model="state.environment.conveyorState.adjusterPosition"><option value="1.67">Open</option><option value="1.91">Pushed</option></select></p>
            <p>Activate Slider: <input style="float: right;" type="checkbox" v-model="slider"></p>
            <p>Slider: <select v-if="slider" style="float: right;" v-model="state.environment.sliderState.sliderPosition"><option value="0.08">Home</option><option value="0.42">Pushed</option></select></p>
        </div>
    </div>
    <button @click="save()">Save</button>
    <button @click="close()">Close</button>
</div>
</template>

<script>
import KeyControls from './KeyControls.vue'

export default {
    components: {
        keyControls: KeyControls
    },
    props: ["state", "context", "socket"],
    data() {
        return {
            testingRig: this.state.environment.testingRigState != null,
            roboticArm: this.state.environment.roboticArmState != null,
            conveyor: this.state.environment.conveyorState != null,
            slider: this.state.environment.sliderState != null
        }
    },
    watch: {
        testingRig(val) {
            if (val) {
                this.state.environment.testingRigState = {
                    heatplateTemperature: null,
                    objectCategory: null
                }
            } else {
                this.state.environment.testingRigState = null
            }
        },
        conveyor(val) {
            if (val) {
                this.state.environment.conveyorState = {
                    detected: null,
                    inPickupWindow: null
                }
            } else {
                this.state.environment.conveyorState = null
            }
        },
        slider(val) {
            if (val) {
                this.state.environment.sliderState = {
                    sliderPosition: null
                }
            } else {
                this.state.environment.sliderState = null
            }
        }
    },
    methods: {
        close() {
            this.$emit('close');
        },
        save() {
            this.$emit('recordPosition');
        },
        isEmpty(env) {
            return env.testingRigState == null &&
                env.roboticArmState == null &&
                env.conveyorState == null
        },
        savePosition() {
            console.log(this.context)
            var context = this.context.roboticArmState
            this.state.environment.roboticArmState = {}
            var r = this.state.environment.roboticArmState
            r.basePosition = context.basePosition.toFixed(2);
            r.mainArmPosition = context.mainArmPosition.toFixed(2);
            r.secondArmPosition = context.secondArmPosition.toFixed(2);
            r.headPosition = context.headPosition.toFixed(2);
            r.headMountPosition = context.headMountPosition.toFixed(2);
            r.gripperPosition = context.gripperPosition.toFixed(2);
            this.$emit('recordPosition');
            this.$forceUpdate();
        },
        move() {
            var xhr = new XMLHttpRequest();
            xhr.open("POST", 'http://localhost:8080/moveEnvironment', true);
            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
            xhr.send(JSON.stringify(this.state.environment));
        }
    }
}
</script>
