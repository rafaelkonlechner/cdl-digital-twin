<style scoped>
.state-machine {
    font-size: 0.8em;
    min-height: 50vh;
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
    border-radius: 10px;
    z-index: 11;
}

.new {
    border: 0px solid white;
    border-radius: 10px;
    box-shadow: 1px 1px 10px grey;
    padding: 2px;
    width: 64px;
    height: 64px;
}

.new i {
    font-size: 4em;
    color: darkslategray;
}
</style>
<template>
<div class="state-machine">
    <div v-if="showPopup" @click="showPopup = false;" class="dim">
        <div class="popup">
            <div v-if="selectedState">
                <state-detail :state="selectedState"></state-detail>
            </div>
            <div v-if="selectedTransition">
                <h1>{{selectedTransition.first.name}} &rarr; {{selectedTransition.second.name}}</h1>
            </div>
        </div>
    </div>
    <div class="mdl-grid">
        <div v-for="(s, index) in job.states" class="mdl-cell--3-col" style="position: relative;">
            <svg @click="showTransition(index)" xmlns="http://www.w3.org/2000/svg" style="position: absolute; top: 25%; left: 82%;" width="24" height="24">
                <path v-show="!s.active" d="M12 8V4l8 8-8 8v-4H4V8z" fill="#888"/>
                <path v-show="s.active" d="M12 8V4l8 8-8 8v-4H4V8z" fill="#1E88E5"/>
                <path d="M0 0h24v24H0z" fill="none"/>
            </svg>
            <state-preview v-on:click.native="showState(s)" :state="s" :index="index" :active="s.active" :context="context" :socket="socket" v-on:moveLeft="moveLeft($event)" v-on:moveRight="moveRight($event)" v-on:remove="remove($event)" v-on:recordPosition="saveChanges()">
            </state-preview>
        </div>
        <div class="mdl-cell--3-col">
            <button @click="addState" class="new" style="position: relative; top: 32%;">
                    <i class="material-icons">add</i>
            </button>
        </div>
    </div>
</div>
</template>

<script>
import StatePreview from "./StatePreview.vue";
import StateDetail from "./StateDetail.vue";

export default {
    props: ["job", "context", "socket"],
    components: {
        statePreview: StatePreview,
        stateDetail: StateDetail
    },
    data() {
        return {
            editName: false,
            showPopup: false,
            selectedState: null,
            selectedTransition: null
        }
    },
    mounted() {
        var self = this;
        this.socket.addEventListener("message", function(event) {
            let msg = JSON.parse(event.data);
            if (msg.topic === "state") {
                let name = msg.message
                self.job.states.forEach(function(x) {
                    if (x.name === name) {
                        console.log(name)
                        x.active = true;
                    } else {
                        x.active = false;
                    }
                    self.$forceUpdate();
                });
            }
        });
    },
    methods: {
        addState() {
            this.job.states.push({
                name: "New"
            })
        },
        moveLeft(i) {
            event.stopPropagation()
            if (i - 1 < 0) return;
            var elem = this.job.states[i];
            var right = this.job.states[i - 1];
            this.job.states.splice(i, 1, right)
            this.job.states.splice(i - 1, 1, elem)
            this.$emit('changes')
            this.saveChanges();
        },
        moveRight(i) {
            event.stopPropagation()
            if (i + 1 > this.job.states.length - 1) return;
            var elem = this.job.states[i];
            var right = this.job.states[i + 1];
            this.job.states.splice(i, 1, right)
            this.job.states.splice(i + 1, 1, elem)
            this.$emit('changes')
            this.saveChanges();
        },
        remove(i) {
            event.stopPropagation()
            this.job.states.splice(i, 1);
            this.$emit('changes')
            this.saveChanges();
        },
        saveChanges() {
            var xhr = new XMLHttpRequest();
            xhr.open("PUT", 'http://localhost:8080/jobs/' + this.job.id, true);
            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
            xhr.send(JSON.stringify(this.job));
        },
        showState(s) {
            this.showPopup = true;
            this.selectedTransition = null;
            this.selectedState = s;
        },
        showTransition(index) {
            this.showPopup = true;
            var first = this.job.states[index]
            var second = this.job.states[index + 1]
            this.selectedState = null;
            this.selectedTransition = {
                first: first,
                second: second
            }
        }
    }
}
</script>
