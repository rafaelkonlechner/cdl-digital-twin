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
        objectClass: '',
        pickupObjects: 0,
        basePosition: 0.0,
        mainArmPosition: 0.0,
        secondArmPosition: 0.0,
        baseTargetPosition: 0.0,
        mainArmTargetPosition: 0.0,
        secondArmTargetPosition: 0.0,
        pickupImageBase64: "images/canvas.png",
        detectionImageBase64: "images/canvas.png",
        upKey: false,
        downKey: false,
        leftKey: false,
        rightKey: false,
        wKey: false,
        sKey: false
    },
    init: function () {

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
                        console.log(rect.x, rect.y, rect.height, rect.width, rect.color);
                        self.objectClass = rect.color;
                        plot(rect.x, rect.y, rect.width, rect.height, rect.color, '.detection-container', 'detection-camera', 'detection-rect');
                    });
                }
            });
            trackingTask = tracking.track('#detection-camera', colors);
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
                        console.log(rect.x, rect.y, rect.height, rect.width, rect.color);
                        plot(rect.x, rect.y, rect.width, rect.height, 'yellow', '.pickup-container', 'pickup-camera', 'pickup-rect');
                    });
                }
            });
            var trackingTask = tracking.track('#pickup-camera', colors);
        },
        updateSensorData: function (message) {
            var split = message.split(' ');
            var value = parseFloat(split[1]);
            value = Math.floor(value * 10000) / 10000;
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
                default:
                    console.log("Unknown message type")
            }
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
            this.sendSocketMessage("base-goto " + this.baseTargetPosition)
        },
        mainArmGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("main-arm-goto " + this.mainArmTargetPosition)
        },
        secondArmGoto: function () {
            console.log("Setting value");
            this.sendSocketMessage("second-arm-goto " + this.secondArmTargetPosition)
        }
    }
});
