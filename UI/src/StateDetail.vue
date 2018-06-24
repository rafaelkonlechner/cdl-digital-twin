<style scoped>
p {
    font-size: 11px;
    line-height: 30px;
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
        <div v-bind:class="{ disabled: !testingRig }">
            <p>Plate Temp.: <input type="number" step="0.1" style="width: 45px;">°C</p>
        </div>
    </div>
    <div class="third">
        <h2>Robotic Arm <input type="checkbox" v-model="roboticArm"></h2>
        <div v-bind:class="{ disabled: !roboticArm }">
            <span v-if="Object.keys(state).length > 1" class="attribute" v-for="(value,key) in state">{{key}}: {{value}}</br></span>
            <div v-if="Object.keys(state).length <= 1" style="margin-bottom: 10px;">
                <p>Use the <b>arrow keys</b> to move the robot.</p>
                <key-controls :socket="socket"></key-controls>
                <button style="font-size: 1.2em;" @click="savePosition()">Save</button>
            </div>
            <p>Gripper: <select><option>Open</option><<option>Closed</option></select></p>
        </div>
    </div>
    <div class="third">
        <h2>Conveyor <input type="checkbox" v-model="conveyor"></h2>
        <div v-bind:class="{ disabled: !conveyor }">
            <p>Adjuster: <select><option>Pushed</option><<option>Open</option></select></p>
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
    data() {
        return {
            testingRig: false,
            roboticArm: false,
            conveyor: false
        }
    },
    props: ["state", "context", "socket"],
    methods: {
        close() {
            this.$emit('close');
        }
    }
}
</script>
