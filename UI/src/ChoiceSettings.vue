<style scoped>

</style>
<template>
<div>
    <div style="text-align: center; padding: 10px;">
        <p>Sensor:
            <select v-model="state.sensor">
                <option value="qr">QR Code</option>
                <option value="pickup">Pickup Camera</option>
            </select>
        </p>
    </div>
    <div>
        <div style="float: left; width: 49%; text-align: center;">
            <h2>Condition</h2>
            <div style="height: 75px; padding-top: 45px;">
                <p v-if="state.sensor == 'qr'">
                    Scanning: <select v-model="state.environment.testingRigState.objectCategory">
                        <option value="NONE">None</option>
                        <option value="GREEN">Class 1</option>
                        <option value="RED">Class 2</option>
                    </select>
                </p>
                <p v-if="state.sensor == 'pickup'">
                    Condition: <select>
                        <option>Empty</option>
                        <option>Detected</option>
                        <option>in Pickup Window</option>
                    </select>
                </p>
            </div>
            <p style="margin-bottom: 0px; height: 30px;">&mdash; OR &mdash;</p>
            <div style="height: 110px; padding-top: 40px;">
                <p v-if="state.sensor=='qr' ">
                    Scanning: <select>
                        <option>Empty</option>
                        <option>Class 1</option>
                        <option>Class 2</option>
                    </select>
                </p>
                <p v-if="state.sensor=='pickup' ">
                    Condition: <select>
                        <option>Empty</option>
                        <option>Detected</option>
                        <option>in Pickup Window</option>
                    </select>
                </p>
            </div>
        </div>
        <div style="float: right; width: 49%; ">
            <h2>Resulting State</h2>
            <div>
                <state-preview :state="followupState.choices.first[0] "></state-preview>
            </div>
            <div>
                <state-preview :state="followupState.choices.second[0] "></state-preview>
            </div>
        </div>
    </div>
    <div style="width: 100%; text-align: right;">
        <button style="margin-right: 5px;" @click="close()">Done</button>
    </div>
</div>
</template>

<script>
import StatePreview from "./StatePreview.vue";

export default {
    props: ["state", "followupState"],
    components: {
        statePreview: StatePreview
    },
    created() {
        if (this.state.environment.testingRigState.objectCategory === undefined) {
            this.state.environment.testingRigState.objectCategory = 'NONE'
        }
        if (this.state.altEnvironment.testingRigState.objectCategory === undefined) {
            this.state.altEnvironment.testingRigState.objectCategory = 'NONE'
        }
    },
    methods: {
        close: function() {
            this.$emit('close');
        }
    }
}
</script>
