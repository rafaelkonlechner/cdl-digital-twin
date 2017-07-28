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
    return Math.floor(value * 10000) / 10000;
};

var vue = new Vue({
    el: '#app',
    data: {
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
        handPosition: [],
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
        sKey: false
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
                } else {
                    event.data.forEach(function (rect) {
                        self.objectClass = rect.color;
                        plot(rect.x, rect.y, rect.width, rect.height, rect.color, '.detection-container', 'detection-camera', 'detection-rect');
                        //this.sendTrackingSocketMessage(rect.x + ", " + rect.y + ", " + rect.width + ", " + rect.height + ", " + rect.color)
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
                } else {
                    self.pickupObjects = event.data.length;
                    event.data.forEach(function (rect) {
                        plot(rect.x, rect.y, rect.width, rect.height, 'yellow', '.pickup-container', 'pickup-camera', 'pickup-rect');
                        //self.adjustGripper(rect.x, rect.y, rect.width, rect.height);
                        this.sendTrackingSocketMessage(JSON.stringify({
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
                    this.handPosition = truncate(json.handPosition);
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
                    console.log("Unknown message type")
            }
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
        }
    }
});
