<style scoped>
.state {
    width: 70%;
    min-height: 260px;
    margin-bottom: 30px;
    border-radius: 10px;
    box-shadow: 1px 1px 10px grey;
}

.active {
    border: 2px solid #1E88E5;
    box-shadow: 2px 2px 10px grey;
}

.state-head {
    padding: 10px;
}

.state-content {
    padding: 0px 12px 10px 12px;
}

.attribute {
    font-family: 'Roboto Mono' monospace;
}

.material-icons {
    font-size: 1.5em;
    color: darkslategrey;
}

.state-content p {
    font-size: 1.1em;
    color: darkslategray;
}
</style>
<template>
<div v-bind:class="{ 'active': active, 'state': true }">
    <div class="state-head">
        <i @click="remove(index)" style="float: right;" class="material-icons">delete</i>
        <i @click="moveRight(index)" style="float: right;" class="material-icons">arrow_right</i>
        <i @click="moveLeft(index)" style="float: right;" class="material-icons">arrow_left</i>
        <!--<i v-if="index === 0" style="float: right;" class="material-icons">check_circle</i>
        <i v-if="index === 1" style="float: right;" class="material-icons">cached</i>
        <i v-if="index >= 2" style="float: right;" class="material-icons">schedule</i>
        <i v-if="false" style="float: right;" class="material-icons">offline_bolt</i>-->
        <h3 v-if="!editName" @click="editName=true">{{state.name}}</h3>
        <input style="color: darkslategray; font-family: 'Roboto'; font-size: 14px; margin-top: 14px; width: 80px;" v-if="editName" v-on:keyup.enter="editName=false" v-model="state.name" />
    </div>
    <div class="state-content">
        <div style="text-align: center;" v-if="Object.keys(state).length <= 1">
            <p>Use <b>Arrow keys</b> to reposition robot</p>
            <key-controls :socket="socket"></key-controls>
            <button style="font-size: 1.2em;" @click="savePosition()">Save</button>
        </div>
        <span v-if="Object.keys(state).length > 1" class="attribute" v-for="(value,key) in state">{{key}}: {{value}}</br></span>
    </div>
</div>
</template>

<script>
import KeyControls from './KeyControls.vue'

export default {
    props: ["state", "index", "active", "context", "socket"],
    components: {
        keyControls: KeyControls
    },
    data() {
        return {
            editName: false
        }
    },
    methods: {
        moveLeft(i) {
            this.$emit('moveLeft', i);
        },
        moveRight(i) {
            this.$emit('moveRight', i);
        },
        remove(i) {
            this.$emit('remove', i);
        },
        savePosition() {
            var r = this.context.roboticArmState
            this.state.basePosition = r.basePosition.toFixed(2);
            this.state.mainArmPosition = r.mainArmPosition.toFixed(2);
            this.state.secondArmPosition = r.secondArmPosition.toFixed(2);
            this.state.wristPosition = r.wristPosition.toFixed(2);
            this.state.wristRotation = r.wristRotation.toFixed(2);
            this.state.gripperPosition = r.gripperPosition.toFixed(2);
            console.log(this.state)
            this.$emit('recordPosition');
            this.$forceUpdate();
        }
    }
}
</script>
