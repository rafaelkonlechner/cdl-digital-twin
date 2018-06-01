<style scoped>
.state-machine {
    font-size: 0.8em;
}

.dim {
    position: fixed;
    padding: 0;
    margin: 0;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    z-index: 10;
}

.popup {
    position: fixed;
    padding: 20px;
    top: 20vw;
    left: 30vw;
    width: 40vw;
    height: 40vh;
    background: white;
    border-radius: 20px;
    z-index: 11;
}

.new {
    border: 0px solid white;
    padding: 2px;
    width: 64px;
    height: 64px;
}

.new i {
    font-size: 4em;
}
</style>
<template>
<div class="state-machine">
    <div v-if="showPopup" @click="showPopup = false" class="dim">
        <div class="popup">
            <h1>{{selected.name}}</h1>
        </div>
    </div>
    <div class="mdl-grid">
        <div v-for="(s, index) in job.states" class="mdl-cell--3-col">
            <!--<div @click="showPopup = true; selected = s;" class="state">-->
            <state-preview :state="s" :index="index" :active="s.active" v-on:moveLeft="moveLeft($event)" v-on:moveRight="moveRight($event)" v-on:remove="remove($event)"></state-preview>
            <svg v-if="index < job.states.length - 1" @click="showPopup = true;" xmlns="http://www.w3.org/2000/svg" style="position: relative; bottom: 35%; left: 10%;" width="24" height="24">
                <path v-show="!s.active" d="M12 8V4l8 8-8 8v-4H4V8z" fill="#888"/>
                <path v-show="s.active" d="M12 8V4l8 8-8 8v-4H4V8z" fill="#1E88E5"/>
                <path d="M0 0h24v24H0z" fill="none"/>
            </svg>
        </div>
        <div class="mdl-cell--3-col">
            <button @click="addState" class="new" style="margin-top: 26%;">
                    <i class="material-icons">add</i>
                </button>
        </div>
    </div>
</div>
</template>

<script>
import StatePreview from "./StatePreview.vue";

export default {
    props: ["job"],
    components: {
        statePreview: StatePreview
    },
    data() {
        return {
            editName: false,
            showPopup: false,
            selected: null
        }
    },
    methods: {
        addState() {
            this.job.states.push({
                name: "New"
            })
        },
        moveLeft(i) {
            if (i - 1 < 0) return;
            var elem = this.job.states[i];
            var right = this.job.states[i - 1];
            this.job.states.splice(i, 1, right)
            this.job.states.splice(i - 1, 1, elem)
            this.$emit('changes')
        },
        moveRight(i) {
            if (i + 1 > this.job.states.length - 1) return;
            var elem = this.job.states[i];
            var right = this.job.states[i + 1];
            this.job.states.splice(i, 1, right)
            this.job.states.splice(i + 1, 1, elem)
            this.$emit('changes')
        },
        remove(i) {
            this.job.states.splice(i, 1);
            this.$emit('changes')
        }
    }
}
</script>
