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
            <p>Plate Temp.: <input type="number" step="0.1" style="width: 45px;" :disabled="!testingRig" v-model="heatplateTemperature">°C</p>
        </div>
    </div>
    <div class="third">
        <h2>Robotic Arm <input type="checkbox" v-model="roboticArm"></h2>
        <div v-bind:class="{ disabled: !roboticArm}">
            <div v-if="state.environment.roboticArmState">
                <p>Base: <input v-model="state.environment.roboticArmState.basePosition" type="number" step="0.01"></p>
                <p>Main Arm: <input v-model="state.environment.roboticArmState.mainArmPosition" type="number" step="0.01"></p>
                <p>Second Arm: <input v-model="state.environment.roboticArmState.secondArmPosition" type="number" step="0.01"></p>
                <p>Head: <input v-model="state.environment.roboticArmState.headPosition" type="number" step="0.01"></p>
                <p>Head Mount: <input v-model="state.environment.roboticArmState.headMountPosition" type="number" step="0.01"></p>
                <p>Gripper: <select v-model="state.environment.roboticArmState" :disabled="!roboticArm"><option>Open</option><<option>Closed</option></select></p>
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
            <p>Adjuster: <select :disabled="!conveyor" v-model="adjusterPosition"><option>Open</option><<option>Pushed</option></select></p>
            <p>Slider: <select :disabled="!conveyor" v-model="sliderPosition"><option>Open</option><<option>Pushed</option></select></p>
        </div>
    </div>
    <button @click="close()">Done</button>
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
            heatplateTemperature: 0,
            adjusterPosition: 'Open',
            sliderPosition: 'Open'
        }
    },
    methods: {
        close() {
            this.$emit('recordPosition');
            this.$emit('close');
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
            xhr.open("POST", 'http://localhost:8080/moveRoboticArm', true);
            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
            xhr.send(JSON.stringify(this.state.environment.roboticArmState));
        }
    }
}
</script>
