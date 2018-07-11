<style scoped>

</style>
<template>
<div>
    <h2 style="display: inline-block;">Sensor Monitor</h2>
    <button @click="toggleRecording()">
            <i class="material-icons" v-bind:class="{ red: recording }"
               style="font-size: 12px; position: relative; top: 2px;">fiber_manual_record</i>
            <span v-if="!recording" style="font-size: 12px;">Record</span>
            <span v-if="recording" style="font-size: 12px;">Stop Recording</span>
        </button>
    <button @click="resetData()">
            <span style="font-size: 12px;">Reset</span>
        </button>
    <line-chart width="400" height="200" min="0" max="50" :data="data">
    </line-chart>

    <p>Message Rate: {{messageRate}} / sec
        <div style="display: inline-block; width: 150px;"><input class="mdl-slider mdl-js-slider" type="range" min="0" max="30" v-model="messageRate" /></div>
    </p>
    <p>Monitor 1: Main Arm</p>
    <div id="monitor-1" class="time-series"></div>
</div>
</template>

<script>
import LineChart from "./LineChart.vue";
export default {
    name: "sensor-monitor",
    props: ["socket"],
    components: {
        lineChart: LineChart
    },
    data() {
        return {
            messageRate: 0,
            recording: false,
            data: [{
                x: 0,
                y: 1
            }, {
                x: 1,
                y: 10
            }, {
                x: 2,
                y: 12
            }, {
                x: 3,
                y: 40
            }]
        };
    },
    mounted() {},
    watch: {
        messageRate: function(newValue) {
            this.socket.send("message-rate " + this.messageRate);
        }
    }
};
</script>
