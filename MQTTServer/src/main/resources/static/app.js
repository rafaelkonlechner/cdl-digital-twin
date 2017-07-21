var socket = new SockJS(':8080/stomp');

var stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    //TODO: Change topic name
    stompClient.subscribe('/topic/sensor', function (response) {
        vue.updateSensorData(response.body);
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

var vue = new Vue({
    el: '#app',
    data: {
        savedPositions: [
            {
                name: "Idle",
                baseTargetPosition: 0.0,
                mainArmTargetPosition: 0.0,
                secondArmTargetPosition: 0.0,
                wristTargetPosition: 0.0,
                gripperTargetPosition: 1.0
            },
            {
                name: "Approach",
                baseTargetPosition: 0.0,
                mainArmTargetPosition: 1.50,
                secondArmTargetPosition: -0.12,
                wristTargetPosition: 0.0,
                gripperTargetPosition: 1.5
            },
            {
                name: "Grip",
                baseTargetPosition: 0.0,
                mainArmTargetPosition: 1.50,
                secondArmTargetPosition: -0.12,
                wristTargetPosition: 0.0,
                gripperTargetPosition: -0.40
            },
            {
                name: "Park",
                baseTargetPosition: 3.142,
                mainArmTargetPosition: 1.40,
                secondArmTargetPosition: -1.55,
                wristTargetPosition: -1.5,
                gripperTargetPosition: -0.40
            },
            {
                name: "Release Half",
                baseTargetPosition: 3.142,
                mainArmTargetPosition: 1.36,
                secondArmTargetPosition: -1.34,
                wristTargetPosition: -1.5,
                gripperTargetPosition: -0.2
            },
            {
                name: "Release Full",
                baseTargetPosition: 3.142,
                mainArmTargetPosition: 1.36,
                secondArmTargetPosition: -1.334,
                wristTargetPosition: -1.5,
                gripperTargetPosition: 1.0
            },
            {
                name: "Standby",
                baseTargetPosition: 3.142,
                mainArmTargetPosition: 0.0,
                secondArmTargetPosition: 0.0,
                wristTargetPosition: -1.5,
                gripperTargetPosition: 1.0
            },
            {
                name: "Pickup",
                baseTargetPosition: 3.142,
                mainArmTargetPosition: 1.22,
                secondArmTargetPosition: -1.23,
                wristTargetPosition: -1.5,
                gripperTargetPosition: 1.0
            },
            {
                name: "Pickup Grip",
                baseTargetPosition: 3.142,
                mainArmTargetPosition: 1.22,
                secondArmTargetPosition: -1.23,
                wristTargetPosition: -1.5,
                gripperTargetPosition: -0.4
            },
            {
                name: "GreenDeposit",
                baseTargetPosition: -1.745,
                mainArmTargetPosition: 0.942,
                secondArmTargetPosition: -0.89,
                wristTargetPosition: 1.5,
                gripperTargetPosition: -0.4
            },
            {
                name: "GreenDepositRelease",
                baseTargetPosition: -1.745,
                mainArmTargetPosition: 0.942,
                secondArmTargetPosition: -0.89,
                wristTargetPosition: 1.5,
                gripperTargetPosition: 0.5
            },
            {
                name: "RedDeposit",
                baseTargetPosition: -1.449,
                mainArmTargetPosition: 0.942,
                secondArmTargetPosition: -0.89,
                wristTargetPosition: 1.5,
                gripperTargetPosition: -0.4
            },
            {
                name: "RedDepositRelease",
                baseTargetPosition: -1.449,
                mainArmTargetPosition: 0.942,
                secondArmTargetPosition: -0.89,
                wristTargetPosition: 1.5,
                gripperTargetPosition: 0.5
            }

        ],
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
        mainArmTargetPosition: 0.0,
        secondArmTargetPosition: 0.0,
        wristTargetPosition: 0.0,
        gripperTargetPosition: 0.0,
        sliderTargetPosition: 0.0,
        adjusterTargetPosition: 0.0,
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
                    baseTargetPosition: this.basePosition,
                    mainArmTargetPosition: this.mainArmPosition,
                    secondArmTargetPosition: this.secondArmPosition,
                    wristTargetPosition: this.wristPosition,
                    gripperTargetPosition: this.gripperPosition
                }
            );
        },
        gotoSavedPosition: function (index) {
            var pos = this.savedPositions[index];
            if (this.baseTargetPosition !== pos.baseTargetPosition) {
                this.baseTargetPosition = pos.baseTargetPosition;
                this.baseGoto();
            }
            if (this.mainArmTargetPosition !== pos.mainArmTargetPosition) {
                this.mainArmTargetPosition = pos.mainArmTargetPosition;
                this.mainArmGoto();
            }
            if (this.secondArmTargetPosition !== pos.secondArmTargetPosition) {
                this.secondArmTargetPosition = pos.secondArmTargetPosition;
                this.secondArmGoto();
            }
            if (this.wristTargetPosition !== pos.wristTargetPosition) {
                this.wristTargetPosition = pos.wristTargetPosition;
                this.wristGoto();
            }
            if (this.gripperTargetPosition !== pos.gripperTargetPosition) {
                this.gripperTargetPosition = pos.gripperTargetPosition;
                this.gripperGoto();
            }
            //this.allGoto();
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
                        this.sendTrackingSocketMessage(rect.x + ", " + rect.y + ", " + rect.width + ", " + rect.height + ", " + rect.color)
                    });
                }
            });
            trackingTask = tracking.track('#detection-camera', colors);
        },
        adjustGripper: function(x, y, width, height) {
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
                        self.adjustGripper(rect.x, rect.y, rect.width, rect.height)
                        //this.sendTrackingSocketMessage(rect.x + ", " + rect.y + ", " + rect.width + ", " + rect.height + ", " + rect.color)
                    });
                }
            });
            var trackingTask = tracking.track('#pickup-camera', colors);
        },
        updateSensorData: function (message) {
            var split = message.split(' ');
            var value = parseFloat(split[1]);
            if (!isNaN(value)) {
                value = Math.floor(value * 10000) / 10000;
            } else {
                value = message.slice(message.indexOf(' '), message.length);
            }
            switch (split[0]) {
                case 'base':
                    this.basePosition = value;
                    break;
                case 'main_arm':
                    this.mainArmPosition = value;
                    break;
                case 'second_arm':
                    this.secondArmPosition = value;
                    break;
                case 'wrist':
                    this.wristPosition = value;
                    break;
                case 'hand':
                    this.handPosition = value;
                    break;
                case 'gripper':
                    this.gripperPosition = value;
                    break;
                case 'slider':
                    this.sliderPosition = value;
                    break;
                case 'adjuster':
                    this.adjusterPosition = value;
                    break;
                case 'green-gate':
                    this.greenCount++;
                    break;
                case 'red-gate':
                    this.redCount++;
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
            this.sendSocketMessage("base-goto " + this.baseTargetPosition)
        },
        mainArmGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("main-arm-goto " + this.mainArmTargetPosition)
        },
        secondArmGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("second-arm-goto " + this.secondArmTargetPosition)
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
            this.sendSocketMessage("adjuster-goto " + this.adjusterTargetPosition)
        },
        sliderPush: function() {
            this.sliderTargetPosition = 0.42;
            this.sliderGoto();
        },
        sliderBackUp: function() {
            this.sliderTargetPosition = 0.08;
            this.sliderGoto();
        },
        adjusterPush: function() {
            this.adjusterTargetPosition = 1.889;
            this.adjusterGoto();
        },
        adjusterBackUp: function() {
            this.adjusterTargetPosition = 1.669;
            this.adjusterGoto();
        }
    }
});
