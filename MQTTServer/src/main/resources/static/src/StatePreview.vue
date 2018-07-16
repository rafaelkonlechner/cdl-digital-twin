<style scoped>
.state {
    display: inline-block;
    vertical-align: top;
    width: 180px;
    height: 120px;
    margin-bottom: 30px;
    border-radius: 10px;
    box-shadow: 1px 1px 10px grey;
}

.active {
    border: 2px solid #1E88E5;
    box-shadow: 2px 2px 10px grey;
}

.state-head {
    padding: 10px 10px 0 10px;
}

.state-content {
    padding: 0px 12px 10px 12px;
}

.material-icons {
    font-size: 1.5em;
    color: darkslategrey;
}

.state-content p {
    font-size: 1.1em;
    color: darkslategray;
}

h3 {
    line-height: 1.2em;
}
</style>
<template>
<div v-bind:class="{ 'active': active, 'state': true }">
    <div class="state-head">
        <i @click="remove(index)" style="float: right; cursor: default;" class="material-icons">delete</i>
        <i @click="moveRight(index)" style="float: right; cursor: default;" class="material-icons">arrow_right</i>
        <i @click="moveLeft(index)" style="float: right; cursor: default;" class="material-icons">arrow_left</i>
        <i @click="open(index)" style="float: right; cursor: default;" class="material-icons">info</i>
        <h3 v-if="!editName" @click="editName=true;">{{state.name}}</h3>
        <input style="color: darkslategray; font-family: 'Roboto'; font-size: 14px; width: 75px;" v-if="editName" v-on:keyup.enter="saveName()" v-model="state.name" />
    </div>
    <div class="state-content">
        <div style="text-align: center;" v-if="state.environment.roboticArmState == null">
            <key-controls :socket="socket"></key-controls>
            <button style="font-size: 1.2em;" @click="savePosition()">Save</button>
        </div>
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
        saveName() {
            this.editName = false;
            this.$emit('recordPosition');
        },
        moveLeft(i) {
            this.$emit('moveLeft', i);
        },
        moveRight(i) {
            this.$emit('moveRight', i);
        },
        remove(i) {
            this.$emit('remove', i);
        },
        open(i) {
            this.$emit('open', i);
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
        }
    }
}
</script>
