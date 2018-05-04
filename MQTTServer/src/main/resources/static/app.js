var socket = new WebSocket("ws://127.0.0.1:8080/websocket");
socket.addEventListener("open", function (event) {
    console.log("Connected");
});
socket.addEventListener("message", function (event) {
    var msg = event.data;
    msg = JSON.parse(msg);
    if (msg.topic === "sensor") {
        vue.updateSensorData(msg.message);
    }
    if (msg.topic === "context") {
        vue.updateContextData(msg.message);
    }
    if (msg.topic === "pickupCamera") {
        vue.updatePickupImage(JSON.parse(msg.message));
    }
    if (msg.topic === "detectionCamera") {
        vue.updateDetectionImage(JSON.parse(msg.message));
    }
    if (msg.topic === "qrCode") {
        vue.updateQRCodeData(msg.message);
    }
});

plot = function (x, y, w, h, color, container, imgId, rectId, textId, text) {
    var img = document.getElementById(imgId);
    var rect;
    if (document.getElementById(rectId) === null) {
        rect = document.createElement('div');
        document.querySelector(container).appendChild(rect);
        rect.id = rectId;
        rect.classList.add('rect');
        rect.style.border = '4px solid ' + color;
    } else {
        rect = document.getElementById(rectId);
    }
    rect.style.width = (w + 2) + 'px';
    rect.style.height = (h + 2) + 'px';
    rect.style.left = (img.offsetLeft + x) + 'px';
    rect.style.top = (img.offsetTop + y) + 'px';
    if (text !== undefined) {
        if (document.getElementById(textId) !== null) {
            textElement = document.getElementById(textId);
            document.querySelector(container).removeChild(textElement);
        }
        var textElement = document.createElement('p');
        textElement.id = textId;
        document.querySelector(container).appendChild(textElement);
        var node = document.createTextNode(text);
        textElement.appendChild(node);
        textElement.classList.add('analog-text');
        textElement.style.left = (img.offsetLeft + x) + 'px';
        textElement.style.top = (img.offsetTop + y - 25) + 'px';
    }
};

truncate = function (value) {
    return Math.floor(value * 1000) / 1000;
};

var vue = new Vue({
    el: '#app',
    data: {
        autoPlay: false,
        manualMode: false,
        recording: false,
        context: null,
        savedPositions: [],
        savePositionName: "",
        greenCount: 0,
        redCount: 0,
        pickupObjects: 0,
        basePosition: 0.0,
        mainArmPosition: 0.0,
        secondArmPosition: 0.0,
        wristPosition: 0.0,
        handPosition: {
            x: 0.0,
            y: 0.0,
            z: 0.0
        },
        gripperPosition: 0.0,
        sliderPosition: 0.0,
        adjusterPosition: 0.0,
        platformPosition: 0.0,
        platformTemperature: 0.0,
        baseTargetPosition: 0.0,
        baseTargetSpeed: 1.0,
        mainArmTargetSpeed: 1.0,
        secondArmTargetSpeed: 1.0,
        mainArmTargetPosition: 0.0,
        secondArmTargetPosition: 0.0,
        wristTargetPosition: 0.0,
        gripperTargetPosition: 0.0,
        sliderTargetPosition: 0.0,
        adjusterTargetPosition: 0.0,
        platformTargetPosition: 0.0,
        gripperHasContact: false,
        pickupImageBase64: "images/canvas.png",
        detectionImageBase64: "images/canvas.png",
        sceneImageBase64: "images/canvas.png",
        upKey: false,
        downKey: false,
        leftKey: false,
        rightKey: false,
        wKey: false,
        sKey: false,
        qrCode: {
            id: "-",
            batchId: "-",
            color: "-",
            base64: "images/code-1.png",
            url: "-"
        },
        pickupImageInitialized: false
    },
    computed: {
        idlePathIsActive: function () {
            return this.context !== null ? this.context.roboticArmState.name === "Idle" : false;
        },
        readyPathIsActive: function () {
            return this.context !== null ? this.context.conveyorState.name === "Conveyor Object In Window" : false;
        },
        approachPathIsActive: function () {
            return this.context !== null ? this.context.roboticArmState.name === "Approach" : false;
        },
        pickupPathIsActive: function () {
            return this.context !== null ? this.context.roboticArmState.name === "Pickup" : false;
        },
        parkPathIsActive: function () {
            return this.context !== null ? this.context.roboticArmState.name === "Park" : false;
        },
        releasePathIsActive: function () {
            return this.context !== null ? this.context.roboticArmState.name === "Full Release" : false;
        },
        parkGreenPathIsActive: function () {
            return this.context !== null ? this.context.roboticArmState.name === "Deposit Green" : false;
        },
        parkRedPathIsActive: function () {
            return this.context !== null ? this.context.roboticArmState.name === "Deposit Red" : false;
        },
        depositGreenPathIsActive: function () {
            return this.context !== null ? this.context.roboticArmState.name === "Release Green" : false;
        },
        depositRedPathIsActive: function () {
            return this.context !== null ? this.context.roboticArmState.name === "Release Red" : false;
        }
    },
    created: function () {
        var self = this;
        $.ajax({
            url: "http://localhost:8080/all"
        }).then(function (data) {
            data.forEach(function (x) {
                self.savedPositions.push(x);
            });
        });

        $.ajax({
            url: "http://localhost:8080/autoPlay"
        }).then(function (data) {
            self.autoPlay = data
        });

        $.ajax({
            url: "http://localhost:8080/recording"
        }).then(function (data) {
            self.recording = data
        });
    },
    mounted: function () {
        var time = new Date();
        var data1 = [{
            x: [time],
            y: [0.0],
            mode: 'lines',
            name: "Main Arm",
            line: {
                color: '#2196F3'
            }
        }];
        var data2 = [{
            x: [time],
            y: [0.0],
            mode: 'lines',
            name: "Second Arm",
            line: {
                color: '#4CAF50'
            }
        }];
        var layout = {
            yaxis: {
                nticks: 10,
                range: [-3.15, 3.15]
            },
            margin: {
                l: 15,
                r: 15,
                b: 15,
                t: 15,
                pad: 0
            },
            showlegend: false
        };
        Plotly.plot('monitor-1', data1, layout);
    },
    watch: {
        platformTargetPosition: function (newTargetPosition) {
            this.platformGoto();
        }
    },
    methods: {
        toggleAutoPlay: function () {
            console.log("Toggle AutoPlay");
            this.autoPlay = !this.autoPlay;
            var self = this;
            $.ajax({
                url: "http://localhost:8080/autoPlay",
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify(self.autoPlay)
            });
        },
        toggleRecording: function () {
            console.log("Toggle Recordindg");
            this.recording = !this.recording;
            var self = this;
            $.ajax({
                url: "http://localhost:8080/recording",
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify(self.recording)
            });
        },
        gotoIdlePosition: function () {
            this.sendSocketMessage("goto: Idle")
        },
        updateTrackingInformation: function (image, event, text) {
            if (event.length === 0) {
                // No colors were detected in this frame.
                var rectElem = document.getElementById(image + "-rect");
                var textElem = document.getElementById(image + "-text");
                if (rectElem !== null) {
                    document.querySelector('.' + image + '-container').removeChild(rectElem);
                }
                if (textElem !== null) {
                    //document.querySelector('.' + image + 'detection-container').removeChild(textElem);
                }
            } else {
                event.forEach(function (rect) {
                    plot(rect.x, rect.y, rect.width, rect.height, 'yellow', '.' + image + '-container', image + '-camera', image + '-rect', image + "-text", text);
                });
            }
        },
        updateDetectionImage: function (data) {
            console.log(data.tracking);
            this.detectionImageBase64 = 'data:image/png;base64, ' + data.image;
            this.updateTrackingInformation('detection', data.tracking, 'Item')
        },
        updatePickupImage: function (data) {
            if (!this.pickupImageInitialized) {
                plot(35, 60, 113, 68, 'yellow', '.pickup-container', 'pickup-camera', 'pickup-window-rect', "pickup-text", "Pickup Window");
                this.pickupImageInitialized = true;
            }
            this.pickupImageBase64 = 'data:image/png;base64, ' + data.image;
            this.updateTrackingInformation('pickup', data.tracking, 'Pickup Window')
            /*event.data.forEach(function (rect) {
             plot(rect.x, rect.y, rect.width, rect.height, 'yellow', '.pickup-container', 'pickup-camera', 'pickup-rect');
             });*/
        },
        updateContextData: function (message) {
            this.context = JSON.parse(message);
        },
        updateQRCodeData: function (message) {
            this.qrCode = JSON.parse(message);
            this.qrCode.base64 = 'data:image/png;base64, ' + this.qrCode.base64;
        },
        updateSensorData: function (message) {
            json = JSON.parse(message);
            switch (json.entity) {
                case 'RoboticArm':
                    this.basePosition = truncate(json.basePosition);
                    this.mainArmPosition = truncate(json.mainArmPosition);
                    this.secondArmPosition = truncate(json.secondArmPosition);
                    this.wristPosition = truncate(json.wristPosition);
                    var vector = json.handPosition;
                    vector = vector.substring(vector.indexOf('(') + 1, vector.indexOf(')'));
                    vector = vector.split(',');
                    this.handPosition.x = truncate(vector[0]);
                    this.handPosition.y = truncate(vector[1]);
                    this.handPosition.z = truncate(vector[2]);
                    this.gripperPosition = truncate(json.gripperPosition);
                    this.gripperHasContact = json.gripperHasContact;
                    break;
                case 'Slider':
                    this.sliderPosition = truncate(json.sliderPosition);
                    break;
                case 'Conveyor':
                    this.adjusterPosition = truncate(json.adjusterPosition);
                    break;
                case 'TestingRig':
                    this.platformPosition = truncate(json.platformPosition);
                    this.platformTemperature = truncate(json.heatplateTemperature);
                    break;
                case 'GreenGate':
                    this.greenCount++;
                    break;
                case 'RedGate':
                    this.redCount++;
                    break;
                case 'HandPlate':
                    this.gripperHasContact = !this.gripperHasContact;
                    break;
                default:
                    console.log("Unknown message type: " + json.entity)
            }
            var time = new Date();
            var updateMainArm = {
                x: [
                    [time]
                ],
                y: [
                    [this.mainArmPosition]
                ]
            };
            var olderTime = time.setMinutes(time.getMinutes() - 1);
            var futureTime = time.setMinutes(time.getMinutes() + 1);
            var minuteView = {
                xaxis: {
                    type: 'date',
                    range: [olderTime, futureTime]
                }
            };

            Plotly.relayout('monitor-1', minuteView);
            Plotly.extendTraces('monitor-1', updateMainArm, [0]);
        },
        sendSocketMessage: function (message) {
            stompClient.send("/app/actuator", {}, message);
        },
        keyDownUp: function () {
            this.upKey = true;
        },
        keyUpUp: function () {
            this.upKey = false;
        },
        keyDownDown: function () {
            this.downKey = true;
        },
        keyUpDown: function () {
            this.downKey = false;
        },
        keyDownLeft: function () {
            this.leftKey = true;
        },
        keyUpLeft: function () {
            this.leftKey = false;
        },
        keyDownRight: function () {
            this.rightKey = true;
        },
        keyUpRight: function () {
            this.rightKey = false;
        },
        keyDownW: function () {
            this.wKey = true;
        },
        keyUpW: function () {
            this.wKey = false;
        },
        keyDownS: function () {
            this.sKey = true;
        },
        keyUpS: function () {
            this.sKey = false;
        },
        baseGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("base-goto " + this.baseTargetPosition + " " + this.baseTargetSpeed)
        },
        mainArmGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("main-arm-goto " + this.mainArmTargetPosition + " " + this.mainArmTargetSpeed)
        },
        secondArmGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("second-arm-goto " + this.secondArmTargetPosition + " " + this.secondArmTargetSpeed)
        },
        wristGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("wrist-goto " + this.wristTargetPosition)
        },
        gripperGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("gripper-goto " + this.gripperTargetPosition)
        },
        sliderGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("slider-goto " + this.sliderTargetPosition)
        },
        adjusterGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("conveyor-goto " + this.adjusterTargetPosition)
        },
        platformGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("platform-goto " + -this.platformTargetPosition)
        },
        resetData: function () {
            $.ajax({
                url: "http://localhost:8080/resetData",
                type: "PUT",
                contentType: "application/json"
            });
        }
    }
});