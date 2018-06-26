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
    height: 440px;
    background: white;
    border-radius: 10px;
    z-index: 11;
}

.new {
    display: inline-block;
    border: 0px solid white;
    border-radius: 5px;
    box-shadow: 1px 1px 10px grey;
    padding: 1px;
    text-align: center;
    font-size: 0.6em;
}

.new i {
    font-size: 4em;
    color: darkslategray;
}

.row {
    display: inline-block;
    vertical-align: top;
}
</style>
<template>
<div class="state-machine">
    <div v-if="showPopup" class="dim">
        <div class="popup">
            <div v-if="selectedState && !showChoiceSettings">
                <state-detail :state="selectedState" :socket="socket" :context="context" v-on:close="showPopup = false;" v-on:recordPosition="saveChanges()"></state-detail>
            </div>
            <div v-if="showChoiceSettings">
                <choice-settings :state="selectedState" :followupState="followupState" v-on:close="showPopup = false; showChoiceSettings = false;"></choice-settings>
            </div>
        </div>
    </div>
    <div>
        <div v-for="(s, index) in job.states" class="row">
            <div v-if="!s.choices" style="width: 260px; padding-top: 75px;">
                <state-preview v-on:open="showState(s)" :state="s" :index="index" :active="s.active" :context="context" :socket="socket" v-on:moveLeft="moveLeft($event)" v-on:moveRight="moveRight($event)" v-on:remove="remove($event)" v-on:recordPosition="saveChanges()">
                </state-preview>
                <div style="color: darkslategray; display: inline-block; vertical-align: top; margin-left: 20px;">
                    <i @click="toggleChoice(index)" v-if="!s.choices && index < job.states.length - 1 && !job.states[index + 1].choices" class="material-icons" style="font-size: 3em; position: relative; top: 45px;">arrow_right_alt</i>
                    <div style="display: inline-block; vertical-align: top;" v-if="index < job.states.length - 1 && job.states[index + 1].choices">
                        <div><i @click="choiceSettings(s, index)" class="material-icons" style="cursor: default; font-size: 2em; position: relative; top: 15px; left: 5px;">info</i></div>
                        <div><i @click="toggleChoice(index)" class="material-icons" style="cursor: default; transform: rotate(90deg); font-size: 3em; position: relative; top: 15px;">call_split</i></div>
                    </div>
                </div>
            </div>
            <div v-if="s.choices">
                <div style="display: inline-block; vertical-align: top; margin-right: -40px;">
                    <div>
                        <div v-for="(s1, i1) in s.choices.first" style="display: inline-block; vertical-align: top; width: 260px;">
                            <state-preview v-on:open="showState(s1)" :state="s1" :index="i1" :active="s.active" :context="context" :socket="socket" v-on:moveLeft="moveLeftChoice(s.choices.first, $event)" v-on:moveRight="moveRightChoice(s.choices.first,
                    $event)" v-on:remove="removeChoice(s, s.choices.first, $event, index)" v-on:recordPosition="saveChanges()">
                            </state-preview>
                            <div v-if="i1 < s.choices.first.length - 1" style="display: inline; position: relative; left: 20px; top: 44px;">
                                <i class="material-icons" style="font-size: 3em; color: darkslategrey;">arrow_right_alt</i>
                            </div>
                            <button class="new" style="position: relative; top: 45px; left: 20px;" v-if="i1 == s.choices.first.length - 1 && index == job.states.length - 1" @click="addStateChoice(s.choices.first)"><i class="material-icons">add</i></button>
                        </div>
                    </div>
                    <div>
                        <div v-for="(s2, i2) in s.choices.second" style="display: inline-block; vertical-align: top; width: 260px;">
                            <state-preview v-on:open="showState(s2)" :state="s2" :index="i2" :active="s.active" :context="context" :socket="socket" v-on:moveLeft="moveLeftChoice(s.choices.second, $event)" v-on:moveRight="moveRightChoice(s.choices.second,
                    $event)" v-on:remove="removeChoice(s, s.choices.second, $event, index)" v-on:recordPosition="saveChanges()">
                            </state-preview>
                            <div v-if="i2 < s.choices.second.length - 1" style="display: inline; position: relative; left: 20px; top: 44px;">
                                <i class="material-icons" style="font-size: 3em; color: darkslategrey;">arrow_right_alt</i>
                            </div>
                            <button class="new" style="position: relative; top: 45px; left: 20px;" v-if="i2 == s.choices.second.length - 1  && index == job.states.length - 1" @click="addStateChoice(s.choices.second)"><i class="material-icons">add</i></button>
                        </div>
                    </div>
                </div>
                <div style="display: inline-block; position: relative; top: 115px; left: -20px;">
                    <i class="material-icons" style="font-size: 34px; color: darkslategrey; transform: rotate(90deg);">call_merge</i>
                </div>
            </div>
        </div>
        <button @click="addState()" class="new" style="margin-top: 120px;">
        <i class="material-icons">add</i>
    </button>
    </div>
</div>
</div>
</template>

<script>
import StatePreview from "./StatePreview.vue";
import StateDetail from "./StateDetail.vue";
import ChoiceSettings from "./ChoiceSettings.vue";
import Vue from "vue";

export default {
    props: ["job", "context", "socket"],
    components: {
        statePreview: StatePreview,
        stateDetail: StateDetail,
        choiceSettings: ChoiceSettings
    },
    data() {
        return {
            editName: false,
            showPopup: false,
            showChoiceSettings: false,
            selectedState: null,
            followupState: null,
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
        toggleChoice(index) {
            var state = this.job.states[index + 1];
            if (!state.choices) {
                var choices = {
                    first: [],
                    second: []
                };
                state.choices = choices;
                Vue.set(state, 'choices', choices);
                state.choices.first.push({
                    name: state.name,
                    type: "BasicState",
                    environment: state.environment
                });
                state.choices.second.push({
                    name: "New " + (this.job.states.lengt + 1),
                    environment: {
                        roboticArmState: null,
                        conveyorState: null,
                        testingRigState: null
                    }
                });
            } else if (state.choices.first.length == 1 && state.choices.second.length == 1) {
                state.name = state.choices.first[0].name;
                state.type = state.choices.first[0].type;
                state.environment = state.choices.first[0].environment;
                delete state.choices;
            }
            this.$forceUpdate();
        },
        choiceSettings(s, i) {
            this.selectedState = s;
            this.followupState = this.job.states[i + 1];
            this.showPopup = true;
            this.showChoiceSettings = true;
        },
        addState() {
            this.job.states.push({
                name: "New " + (this.job.states.length + 1),
                type: "BasicState",
                environment: {
                    roboticArmState: null,
                    conveyorState: null,
                    testingRigState: null
                }
            })
        },
        addStateChoice(s) {
            s.push({
                name: "New " + (this.job.states.length + 1),
                type: "ChoiceState",
                environment: {
                    roboticArmState: null,
                    conveyorState: null,
                    testingRigState: null
                }
            });
            this.$forceUpdate();
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
        moveLeftChoice(s, i) {
            event.stopPropagation()
            if (i - 1 < 0) return;
            var elem = s[i];
            var right = s[i - 1];
            s.splice(i, 1, right)
            s.splice(i - 1, 1, elem)
            this.$emit('changes')
            this.saveChanges();
            this.$forceUpdate();
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
        moveRightChoice(s, i) {
            event.stopPropagation()
            if (i + 1 > s.length - 1) return;
            var elem = s[i];
            var right = s[i + 1];
            s.splice(i, 1, right)
            s.splice(i + 1, 1, elem)
            this.$emit('changes')
            this.saveChanges();
            this.$forceUpdate();
        },
        remove(i) {
            event.stopPropagation()
            this.job.states.splice(i, 1);
            this.$emit('changes')
            this.saveChanges();
        },
        removeChoice(s, choices, localIndex, globalIndex) {
            event.stopPropagation()
            choices.splice(localIndex, 1);
            if (choices.length === 0) {
                if (s.choices.first.length === 0) {
                    var c = s.choices.second
                } else {
                    var c = s.choices.first
                }
                this.job.states.splice(globalIndex, 1);
                for (var a = 0; a < c.length; a++) {
                    this.job.states.splice(globalIndex + a, 0, c[a])
                }
            }

            this.$emit('changes')
            this.saveChanges();
            this.$forceUpdate();
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
