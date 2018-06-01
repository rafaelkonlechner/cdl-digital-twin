<style scoped>
.state {
    width: 70%;
    min-height: 210px;
    display: inline-block;
    margin-bottom: 30px;
    border-radius: 10px;
    box-shadow: 2px 2px 10px grey;
}

.active {
    box-shadow: 2px 2px 10px #1E88E5;
}

.state-content {
    padding: 10px 20px;
}

.attribute {
    font-family: 'Roboto Mono' monospace;
}
</style>
<template>
<div v-bind:class="{ 'active': active, 'state': true }">
    <div class="state-content">
        <i @click="remove(index)" style="float: right;" class="material-icons">close</i>
        <i @click="moveRight(index)" style="float: right;" class="material-icons">arrow_right</i>
        <i @click="moveLeft(index)" style="float: right;" class="material-icons">arrow_left</i>
        <!--<i v-if="index === 0" style="float: right;" class="material-icons">check_circle</i>
        <i v-if="index === 1" style="float: right;" class="material-icons">cached</i>
        <i v-if="index >= 2" style="float: right;" class="material-icons">schedule</i>
        <i v-if="false" style="float: right;" class="material-icons">offline_bolt</i>-->
        <h3 v-if="!editName" @click="editName=true">{{state.name}}</h3>
        <input style="color: darkslategray; font-family: 'Roboto'; font-weight: 400;" v-if="editName" v-on:keyup.enter="editName=false" v-model="state.name" />
        <div style="margin-top: 30%; text-align: center;" v-if="Object.keys(state).length <= 1">
            <button style="font-size: 1.2em;">Snapshot</button>
        </div>
        <span v-if="Object.keys(state).length > 1" class="attribute" v-for="(value,key) in state">{{key}}: {{value}}</br></span>
    </div>
</div>
</template>

<script>
export default {
    props: ["state", "index", "active"],
    data() {
        return {
            editName: false
        }
    },
    mounted() {
        console.log(this.state.name)
        console.log(this.state.active)
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
        }
    }
}
</script>
