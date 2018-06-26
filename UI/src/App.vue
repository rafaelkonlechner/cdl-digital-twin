<style scoped>
header {
    text-align: center;
    padding: 2vh;
}

header h1 {
    line-height: 1em;
}

footer {
    text-align: center;
    padding: 3vh;
    background-color: #f5f5f5;
    color: #9e9e9e;
    box-shadow: inset 0 0 1px #bdbdbd;
}

footer h5 {
    font-family: "Roboto Mono", monospace;
    line-height: 1.4;
}

footer h5 small {
    font-size: 0.7em;
}

main {
    width: 100%;
    height: 60vh;
}

article {
    width: 40vw;
    margin: 0 auto 5vh;
    text-align: justify;
}

.mdl-grid {
    width: 90vw;
}

.control-button {
    border: 0px solid white;
    padding: 2px;
}

.selected-job {
    border-radius: 5px;
    background: #1E88E5;
}

.selected-job h3 {
    color: white;
}

.job {
    width: 50%;
}

.job-controls,
.job-controls i,
.job-controls em {
    color: darkslategray;
}

.new-job {
    border: 0px solid white;
    margin-top: 20px;
    border-radius: 5px;
    box-shadow: 1px 1px 5px grey;
    padding: 2px;
    width: 64px;
}
</style>
<template>
<div id="app">
    <header>
        <h1>6-Axis Robotic Arm Twin
        <small>Kuka KR5-R850</small>
      </h1>
    </header>
    <blueprint :socket="socket" :context="context" :data="data"></blueprint>
    <main style="height: 100%;">
        <div class="mdl-grid">
            <div class="mdl-cell--2-col">
                <h2>Saved Jobs</h2>
                <div v-for="job in jobs" @click="selectJob(job)" v-bind:class="{'job': true, 'selected-job': job === selectedJob}">
                    <h3>{{job.name}}</h3>
                </div>
                <div class="job" style="text-align: center;">
                    <button @click="newJob()" class="new-job"><i class="material-icons" style="color: darkslategrey;">add</i></button>
                    <div style="margin-top: 40px; color: darkslategray;">
                        <label style="font-size: 12px;">Import Job</label>
                        <input type="file" value="Import Job" accept="application/json" @change="importJob($event)">
                        <button v-if="file" @click="uploadJob()">Import</button>
                    </div>
                </div>
            </div>
            <div class="mdl-cell--10-col">
                <div class="job-controls">
                    <div style="display: inline-block;" v-if="selectedJob">
                        <h2 v-if="!editTitle" @click="editTitle=true">{{selectedJob.name}}</h2>
                        <input style="margin: 18.4px 0; font-size: 1.5em;" v-if="editTitle" v-on:keyup.enter="updateTitle()" v-model="selectedJob.name" />
                    </div>
                    <div style="display: inline-block; margin-left: 70px;">
                        <button class="control-button" @click="toggleAutoPlay()"><i class="material-icons" v-if="!autoPlay">play_arrow</i><i class="material-icons" v-if="autoPlay">pause</i></button>
                        <button class="control-button" @click="reset()"><i class="material-icons">replay</i></button>
                        <button class="control-button" @click="exportJob()"><i class="material-icons">move_to_inbox</i></button>
                        <a @mouseleave="downloadLink = ''" v-if="downloadLink" :download="selectedJob.name + '.json'" :href="downloadLink" style="margin-left: 20px;">Download Job</a>
                    </div>
                </div>
                <state-machine :job="selectedJob" :context="context" :socket="socket"></state-machine>
            </div>
        </div>
    </main>
    <hr/>
    <footer>
        <h5>
        <strong>Christian Doppler Laboratory</strong><br/>
        <small>for Model-Integrated Smart Production</small>
      </h5>
    </footer>
</div>
</template>

<script>
import Inputs from "./Inputs.vue";
import Controls from "./Controls.vue";
import StateMachine from "./StateMachine.vue";
import SensorMonitor from "./SensorMonitor.vue";
import StateChart from "./StateChart.vue";
import BluePrint from "./Blueprint.vue";
import CameraSignal from "./CameraSignal.vue";

export default {
    name: "app",
    components: {
        inputs: Inputs,
        controls: Controls,
        stateMachine: StateMachine,
        sensorMonitor: SensorMonitor,
        blueprint: BluePrint,
        cameraSignal: CameraSignal
    },
    data() {
        return {
            socket: new WebSocket("ws://127.0.0.1:8080/websocket"),
            messageRate: 0,
            autoPlay: false,
            newJobTitle: "New Job",
            editTitle: false,
            jobs: [],
            selectedJob: null,
            downloadLink: "",
            file: null,
            context: {
                roboticArmState: {
                    name: "Snapshot",
                    basePosition: 0.0,
                    mainArmPosition: 0.0,
                    secondArmPosition: 0.0,
                    headPosition: 0.0,
                    headMountRotation: 0.0,
                    gripperPosition: 0.0,
                    gripperHasContact: false,
                    handPosition: {
                        x: 0.0,
                        y: 0.0,
                        z: 0.0
                    }
                },
                testingRigState: {
                    name: "Snapshot",
                    objectCategory: "Empty",
                    platformPosition: 0.0,
                    heatplateTemperature: 0.0,
                },
                conveyorState: {
                    name: "Snapshot",
                    adjusterPosition: 0.0,
                    detected: false,
                    inPickupWindow: false
                },
                sliderState: {
                    name: "",
                    sliderPosition: 0.0,
                }
            },
            data: {
                count: 0,
                basePosition: [],
                mainArmPosition: [],
                secondArmPosition: [],
                headPosition: [],
                headMountPosition: [],
                gripperPosition: []
            }
        };
    },
    watch: {
        messageRate: function(newValue) {
            this.sendSocketMessage("message-rate " + this.messageRate);
        }
    },
    created() {
        let self = this;
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            try {
                self.jobs = JSON.parse(this.responseText)
            } catch (err) {
                console.log("error")
            }
            self.selectedJob = self.jobs[0]
        }
        xhr.open("GET", 'http://localhost:8080/jobs', true);
        xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
        xhr.send();
        this.socket.addEventListener("open", function(event) {
            console.log("Connected");
        });
        self.socket.addEventListener("message", function(event) {
            let msg = JSON.parse(event.data);
            if (msg.topic === "sensor") {
                let data = JSON.parse(msg.message)
                if (data.entity === "RoboticArm") {
                    self.context.roboticArmState.basePosition = data.basePosition;
                    self.data.basePosition.push({
                        x: self.data.count,
                        y: data.basePosition
                    });
                    self.context.roboticArmState.mainArmPosition = data.mainArmPosition;
                    self.data.mainArmPosition.push({
                        x: self.data.count,
                        y: data.mainArmPosition
                    });
                    self.context.roboticArmState.secondArmPosition = data.secondArmPosition;
                    self.data.secondArmPosition.push({
                        x: self.data.count,
                        y: data.secondArmPosition
                    });
                    self.context.roboticArmState.headPosition = data.headPosition;
                    self.data.headPosition.push({
                        x: self.data.count,
                        y: data.headPosition
                    });
                    self.context.roboticArmState.headMountPosition = data.headMountPosition;
                    self.data.headMountPosition.push({
                        x: self.data.count,
                        y: data.headMountPosition
                    });
                    self.context.roboticArmState.gripperPosition = data.gripperPosition;
                    self.data.gripperPosition.push({
                        x: self.data.count,
                        y: data.gripperPosition
                    });
                    self.data.count = self.data.count + 1;
                    self.context.roboticArmState.handPosition = data.handPosition;
                }
                if (data.entity === "TestingRig") {
                    self.context.testingRigState.objectCategory = data.objectCategory;
                    self.context.testingRigState.platformPosition = data.platformPosition;
                    self.context.testingRigState.heatplateTemperature = data.heatplateTemperature;
                }
            }
        });
    },
    methods: {
        newJob() {
            var job = {
                name: "New Job",
                states: []
            }
            this.jobs.push(job);
            var xhr = new XMLHttpRequest();
            var self = this;
            xhr.onreadystatechange = function() {
                console.log(this.responseText)
                job.id = this.responseText.slice(1, -1)
                console.log(job)
            }
            xhr.open("POST", 'http://localhost:8080/jobs', true);
            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
            xhr.send(JSON.stringify(job));
        },
        updateTitle() {
            this.editTitle = false;
            var xhr = new XMLHttpRequest();
            xhr.open("PUT", 'http://localhost:8080/jobs/' + this.selectedJob.id, true);
            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
            xhr.send(JSON.stringify(this.selectedJob));
        },
        toggleAutoPlay: function() {
            console.log("Toggle AutoPlay");
            this.autoPlay = !this.autoPlay;
            var xhr = new XMLHttpRequest();
            xhr.open("PUT", 'http://localhost:8080/autoPlay', true);
            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
            xhr.send(this.autoPlay);
        },
        reset() {
            var xhr = new XMLHttpRequest();
            xhr.open("PUT", 'http://localhost:8080/reset', true);
            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
            xhr.send();
        },
        selectJob(job) {
            this.selectedJob = job;
            console.log(job)
            console.log(this.selectedJob.id)
            var xhr = new XMLHttpRequest();
            xhr.open("PUT", 'http://localhost:8080/selectedJob/', true);
            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
            xhr.send(job.id);
        },
        gotoIdle() {
            this.sendSocketMessage("idle");
        },
        exportJob() {
            var json = JSON.stringify(this.selectedJob)
            this.downloadLink = 'data:application/json;charset=utf-8,' + encodeURIComponent(json);
        },
        importJob(e) {
            this.file = e.target.files[0];
            console.log(e);
        },
        uploadJob() {
            var xhr = new XMLHttpRequest();
            var self = this;
            xhr.open("POST", 'http://localhost:8080/jobFile/', true);
            var formData = new FormData();
            formData.append("job", this.file)
            console.log(formData)
            xhr.send(formData);
        }
    }
};
</script>
