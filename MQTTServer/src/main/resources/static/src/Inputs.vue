<style scoped>
h3 {
    font-size: 1.2em;
}

i {
    font-size: 0.9em;
    position: relative;
    top: 2px;
}

.red {
    color: #f44336;
}

.green {
    color: #4caf50;
}

input {
    width: 25px;
    display: inline;
}
</style>
<template>
<div>
    <h2>Sensors / Actuators</h2>
    <div>Simulation
        <small>(sim)</small>:
        <i class="material-icons green">fiber_manual_record</i> online</div>
    <div>Hedgehog
        <small>(real)</small>:
        <i class="material-icons red">fiber_manual_record</i> offline</div>
    <ul>
        <li>Kuka KR5-R850
            <small>(<input type="checkbox" checked/>sim, <input type="checkbox" checked/>real)</small>
        </li>
        <ul>
            <li>Sensors</li>
            <ul>
                <li>Base Position</li>
                <li>Main Arm Position</li>
                <li>Second Arm Position</li>
                <li>Gripper Position</li>
                <li>Gripper Contact</li>
            </ul>
            <li>Actuators</li>
            <ul>
                <li>Base</li>
                <li>Main Arm</li>
                <li>Second Arm</li>
                <li>Wrist</li>
                <li>Gripper</li>
            </ul>
        </ul>
        <li>Conveyor
            <small>(<input type="checkbox" checked/>sim)</small>
        </li>
        <ul>
            <li>Belt</li>
            <li>Adjuster</li>
            <li>Slider</li>
        </ul>
        <li>Testing Rig
            <small>(<input type="checkbox" checked/>sim)</small>
        </li>
        <ul>
            <li>Platform</li>
        </ul>
    </ul>
</div>
</template>

<script>
export default {
    name: "inputs",
    properties: ["socket"],
    data() {
        return {
            messageRate: 0
        };
    },
    watch: {
        messageRate: function(newValue) {
            this.sendSocketMessage("message-rate " + this.messageRate);
        }
    }
};
</script>
