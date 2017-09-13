var socket = new SockJS(':8080/stomp');

var stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    stompClient.subscribe('/topic/sensor', function (response) {
        vue.updateSensorData(response.body);
    });
    stompClient.subscribe('/topic/context', function (response) {
        vue.updateContextData(response.body);
    });
    stompClient.subscribe('/topic/pickupCamera', function (response) {
        vue.updatePickupImage(response.body);
    });
    stompClient.subscribe('/topic/detectionCamera', function (response) {
        vue.updateDetectionImage(response.body);
    });
});

stompClient.debug = null;

plot = function (x, y, w, h, color, container, imgId, rectId) {
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
};

truncate = function (value) {
    return Math.floor(value * 1000) / 1000;
};

var vue = new Vue({
    el: '#app',
    data: {
        x: 0.0,
        y: 0.0,
        autoPlay: false,
        context: null,
        savedPositions: [],
        savePositionName: "",
        objectClass: '',
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
        gripperHasContact: false,
        pickupImageBase64: "images/canvas.png",
        detectionImageBase64: "images/canvas.png",
        upKey: false,
        downKey: false,
        leftKey: false,
        rightKey: false,
        wKey: false,
        sKey: false,
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
        tracking.ColorTracker.registerColor('red', function (r, g, b) {
            return r > 100 && g < (r / 2) && b < (r / 2);
        });

        tracking.ColorTracker.registerColor('green', function (r, g, b) {
            return r < (g / 2) && g > 100 && b < (g / 2);
        });
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
    },
    mounted: function () {
        plot(35, 60, 113, 68, 'yellow', '.pickup-container', 'pickup-camera', 'pickup-window-rect');
        var time = new Date();
        var data1 = [
            {
                x: [time],
                y: [0.0],
                mode: 'lines',
                name: "Main Arm",
                line: {color: '#2196F3'}
            }];
        var data2 = [{
            x: [time],
            y: [0.0],
            mode: 'lines',
            name: "Second Arm",
            line: {color: '#4CAF50'}
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
        Plotly.plot('monitor-2', data2, layout);
    },
    watch: {
        upKey: function (newUpKey) {
            if (newUpKey === true) {
                this.sendSocketMessage("lower-main-arm")
            } else {
                this.sendSocketMessage("stop-lower-main-arm")
            }
        },
        downKey: function (newDownKey) {
            if (newDownKey === true) {
                this.sendSocketMessage("lift-main-arm")
            } else {
                this.sendSocketMessage("stop-lift-main-arm")
            }
        },
        leftKey: function (newLeftKey) {
            if (newLeftKey === true) {
                this.sendSocketMessage("left-rotate-base")
            } else {
                this.sendSocketMessage("stop-left-rotate-base")
            }
        },
        rightKey: function (newRightKey) {
            if (newRightKey === true) {
                this.sendSocketMessage("right-rotate-base")
            } else {
                this.sendSocketMessage("stop-right-rotate-base")
            }
        },
        wKey: function (newWKey) {
            if (newWKey === true) {
                this.sendSocketMessage("lift-second-arm")
            } else {
                this.sendSocketMessage("stop-lift-second-arm")
            }
        },
        sKey: function (newSKey) {
            if (newSKey === true) {
                this.sendSocketMessage("lower-second-arm")
            } else {
                this.sendSocketMessage("stop-lower-second-arm")
            }
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
        savePosition: function (name) {
            this.savedPositions.push(
                {
                    name: name,
                    basePosition: this.basePosition,
                    mainArmPosition: this.mainArmPosition,
                    secondArmPosition: this.secondArmPosition,
                    wristPosition: this.wristPosition,
                    gripperPosition: this.gripperPosition
                }
            );
        },
        gotoSavedPosition: function (index) {
            var pos = this.savedPositions[index];
            this.sendSocketMessage("goto: " + pos.name)
        },
        gotoIdlePosition: function () {
            this.sendSocketMessage("goto: Idle")
        },
        updateDetectionImage: function (image) {
            this.detectionImageBase64 = 'data:image/png;base64, ' + image;
            var colors = new tracking.ColorTracker(['red', 'green']);
            var self = this;
            colors.on('track', function (event) {
                if (event.data.length === 0) {
                    // No colors were detected in this frame.
                    var rect = document.getElementById("detection-rect");
                    if (rect !== null) {
                        document.querySelector('.detection-container').removeChild(rect);
                    }
                    self.objectClass = '';
                    self.sendCategorySocketMessage(JSON.stringify({objectCategory: "NONE"}))
                } else {
                    event.data.forEach(function (rect) {
                        self.objectClass = rect.color;
                        plot(rect.x, rect.y, rect.width, rect.height, rect.color, '.detection-container', 'detection-camera', 'detection-rect');
                        self.sendCategorySocketMessage(JSON.stringify({objectCategory: rect.color.toUpperCase()}))
                    });
                }
            });
            trackingTask = tracking.track('#detection-camera', colors);
        },
        adjustGripper: function (x, y, width, height) {
            console.log(x, y);
            console.log(this.handPosition);
        },
        updatePickupImage: function (image) {
            this.pickupImageBase64 = 'data:image/png;base64, ' + image;
            var colors = new tracking.ColorTracker(['red', 'green']);
            var self = this;
            colors.on('track', function (event) {
                if (event.data.length === 0) {
                    // No colors were detected in this frame.
                    var rect = document.getElementById("pickup-rect");
                    if (rect !== null) {
                        document.querySelector('.pickup-container').removeChild(rect);
                    }
                    self.pickupObjects = 0;
                    self.sendTrackingSocketMessage(JSON.stringify({
                        x: 0,
                        y: 0
                    }));
                } else {
                    self.pickupObjects = event.data.length;
                    event.data.forEach(function (rect) {
                        plot(rect.x, rect.y, rect.width, rect.height, 'yellow', '.pickup-container', 'pickup-camera', 'pickup-rect');
                        self.x = rect.x;
                        self.y = rect.y;
                        self.width = rect.width;
                        self.height = rect.height;
                        self.sendTrackingSocketMessage(JSON.stringify({
                            x: rect.x,
                            y: rect.y,
                            width: rect.width,
                            height: rect.height
                        }));
                    });
                }
            });
            var trackingTask = tracking.track('#pickup-camera', colors);
        },
        updateContextData: function (message) {
            this.context = JSON.parse(message);
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
                    break;
                case 'Slider':
                    this.sliderPosition = truncate(json.sliderPosition);
                    break;
                case 'Adjuster':
                    this.adjusterPosition = truncate(json.adjusterPosition);
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
            var updateBase = {
                x: [[time]],
                y: [[this.basePosition]]
            };
            var updateMainArm = {
                x: [[time]],
                y: [[this.mainArmPosition]]
            };
            var updateSecondArm = {
                x: [[time]],
                y: [[this.secondArmPosition]]
            };
            var updateGripper = {
                x: [[time]],
                y: [[this.gripperPosition]]
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
            Plotly.relayout('monitor-2', minuteView);
            Plotly.extendTraces('monitor-1', updateMainArm, [0]);
            Plotly.extendTraces('monitor-2', updateBase, [0]);
        },
        sendSocketMessage: function (message) {
            stompClient.send("/app/actuator", {}, message);
        },
        sendTrackingSocketMessage: function (message) {
            stompClient.send("/app/tracking", {}, message);
        },
        sendCategorySocketMessage: function (message) {
            stompClient.send("/app/category", {}, message);
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
            console.log("down")
        },
        keyUpW: function () {
            this.wKey = false;
            console.log("up")
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
        }
    }
});
