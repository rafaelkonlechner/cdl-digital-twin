<style scoped>

</style>
<template>
<div>
    <div style="text-align: center; padding: 10px;">
        <p>Sensor:
            <select v-model="sensor">
                <option value="qr">QR Code</option>
                <option value="pickup">Pickup Camera</option>
            </select>
        </p>
    </div>
    <div>
        <div style="float: left; width: 49%; text-align: center;">
            <h2>Condition</h2>
            <div style="height: 75px; padding-top: 45px;">
                <p v-if="sensor === 'qr'">
                    Scanning: <select v-model="state.environment.testingRigState.objectCategory">
                        <option value="NONE">None</option>
                        <option value="GREEN">Green</option>
                        <option value="RED">Red</option>
                    </select>
                </p>
                <p v-if="sensor === 'pickup'">
                    Condition: <select v-model="pickupSelection">
                        <option value="empty">Empty</option>
                        <option value="detected">Detected</option>
                        <option value="inPickupWindow">in Pickup Window</option>
                    </select>
                </p>
            </div>
            <p style="margin-bottom: 0px; height: 30px;">&mdash; OR &mdash;</p>
            <div style="height: 110px; padding-top: 40px;">
                <p v-if="sensor === 'qr'">
                    Scanning: <select v-model="state.altEnvironment.testingRigState.objectCategory">
                        <option value="NONE">None</option>
                        <option value="GREEN">Green</option>
                        <option value="RED">Red</option>
                    </select>
                </p>
                <p v-if="sensor === 'pickup'">
                    Condition:
                    <select v-model="altPickupSelection">
                        <option value="empty">Empty</option>
                        <option value="detected">Detected</option>
                        <option value="inPickupWindow">in Pickup Window</option>
                    </select>
                </p>
            </div>
        </div>
        <div style="float: right; width: 49%;">
            <h2>Resulting State</h2>
            <div>
                <state-preview :state="followupState.choices.first[0]"></state-preview>
            </div>
            <div>
                <state-preview :state="followupState.choices.second[0]"></state-preview>
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
    data() {
        return {
            pickupSelection: null,
            altPickupSelection: null,
            sensor: this.state.sensor
        }
    },
    changed: {
        pickupSelection(val) {
            if (val === 'detected') {
                this.state.environment.conveyorState.detected = true;
                this.altPickupSelection = false
            } else {
                this.state.environment.conveyorState.detected = false;
            }
            if (val === 'inPickupWindow') {
                this.state.environment.conveyorState.inPickupWindow = true;
            } else {
                this.state.environment.conveyorState.inPickupWindow = false;
            }
        },
        altPickupSelection(val) {
            if (val === 'detected') {
                this.state.altEnvironment.conveyorState.detected = true;
            } else {
                this.state.altEnvironment.conveyorState.detected = false;
            }
            if (val === 'inPickupWindow') {
                this.state.altEnvironment.conveyorState.inPickupWindow = true;
            } else {
                this.state.altEnvironment.conveyorState.inPickupWindow = false;
            }
        },
        sensor(val) {
            this.state.sensor = this.sensor
        }
    },
    mounted() {
        if (this.state.environment.testingRigState === null) {
            this.state.environment.testingRigState = {
                objectCategory: null
            }
        }
        if (this.state.environment.conveyorState === null) {
            this.state.environment.conveyorState = {
                detected: null,
                inPickupWindow: null

            }
        }
        if (this.state.altEnvironment.testingRigState === null) {
            this.state.altEnvironment.testingRigState = {
                objectCategory: null
            }
        }
        if (this.state.altEnvironment.conveyorState === null) {
            this.state.altEnvironment.conveyorState = {
                detected: null,
                inPickupWindow: null
            }
        }
    },
    methods: {
        close: function() {
            this.$emit('close');
        },
        save: function() {
            this.$emit('recordPosition');
        }
    }
}
</script>
