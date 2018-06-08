<style scoped>
#keypad {
    width: 80px;
    border: 1px solid rgba(0, 0, 0, 0.1);
}
</style>
<template>
<div>
    <input type="image" src="../assets/arrow-keys.jpg" id="keypad" v-on:keyup.up="keyUpUp()" v-on:keydown.up="keyDownUp()" v-on:keyup.down="keyUpDown()" v-on:keydown.down="keyDownDown()" v-on:keyup.left="keyUpLeft()" v-on:keydown.left="keyDownLeft()" v-on:keyup.right="keyUpRight()"
        v-on:keydown.right="keyDownRight()" v-on:keyup.87="keyUpW()" v-on:keydown.87="keyDownW()" v-on:keyup.83="keyUpS()" v-on:keydown.83="keyDownS()" v-on:keyup.65="keyUpA()" v-on:keydown.65="keyDownA()" v-on:keyup.68="keyUpD()" v-on:keydown.68="keyDownD()"
        v-on:keyup.69="keyUpE()" v-on:keydown.69="keyDownE()" v-on:keyup.81="keyUpQ()" v-on:keydown.81="keyDownQ()" v-on:keyup.82="keyUpR()" v-on:keydown.82="keyDownR()" v-on:keyup.70="keyUpF()" v-on:keydown.70="keyDownF()" v-on:keyup.88="keyUpX()" v-on:keydown.88="keyDownX()"
        v-on:keyup.89="keyUpY()" v-on:keydown.89="keyDownY()" />
</div>
</template>

<script>
export default {
    name: "key-controls",
    props: ["socket"],
    data() {
        return {
            upKey: false,
            downKey: false,
            leftKey: false,
            rightKey: false,
            wKey: false,
            sKey: false,
            aKey: false,
            dKey: false,
            qKey: false,
            eKey: false,
            rKey: false,
            fKey: false,
            xKey: false,
            yKey: false
        };
    },
    watch: {
        downKey: function(newUpKey) {
            if (newUpKey === true) {
                this.sendSocketMessage("lower-main-arm");
            } else {
                this.sendSocketMessage("stop-lower-main-arm");
            }
        },
        upKey: function(newDownKey) {
            if (newDownKey === true) {
                this.sendSocketMessage("lift-main-arm");
            } else {
                this.sendSocketMessage("stop-lift-main-arm");
            }
        },
        leftKey: function(newLeftKey) {
            if (newLeftKey === true) {
                this.sendSocketMessage("left-rotate-base");
            } else {
                this.sendSocketMessage("stop-left-rotate-base");
            }
        },
        rightKey: function(newRightKey) {
            if (newRightKey === true) {
                this.sendSocketMessage("right-rotate-base");
            } else {
                this.sendSocketMessage("stop-right-rotate-base");
            }
        },
        wKey: function(newWKey) {
            if (newWKey === true) {
                this.sendSocketMessage("lift-second-arm");
            } else {
                this.sendSocketMessage("stop-lift-second-arm");
            }
        },
        sKey: function(newSKey) {
            if (newSKey === true) {
                this.sendSocketMessage("lower-second-arm");
            } else {
                this.sendSocketMessage("stop-lower-second-arm");
            }
        },
        aKey: function(newAKey) {
            if (newAKey === true) {
                this.sendSocketMessage("rotate-left-second-arm");
            } else {
                this.sendSocketMessage("stop-rotate-left-second-arm");
            }
        },
        dKey: function(newDKey) {
            if (newDKey === true) {
                this.sendSocketMessage("rotate-right-second-arm");
            } else {
                this.sendSocketMessage("stop-rotate-right-second-arm");
            }
        },
        qKey: function(newQKey) {
            if (newQKey === true) {
                this.sendSocketMessage("rotate-left-wrist");
            } else {
                this.sendSocketMessage("stop-rotate-left-wrist");
            }
        },
        eKey: function(newEKey) {
            if (newEKey === true) {
                this.sendSocketMessage("rotate-right-wrist");
            } else {
                this.sendSocketMessage("stop-rotate-right-wrist");
            }
        },
        rKey: function(newRKey) {
            if (newRKey === true) {
                this.sendSocketMessage("lift-wrist");
            } else {
                this.sendSocketMessage("stop-lift-wrist");
            }
        },
        fKey: function(newFKey) {
            if (newFKey === true) {
                this.sendSocketMessage("lower-wrist");
            } else {
                this.sendSocketMessage("stop-lower-wrist");
            }
        },
        yKey: function(newYKey) {
            if (newYKey === true) {
                this.sendSocketMessage("release-gripper");
            } else {
                this.sendSocketMessage("stop-release-gripper");
            }
        },
        xKey: function(newXKey) {
            if (newXKey === true) {
                this.sendSocketMessage("grip-gripper");
            } else {
                this.sendSocketMessage("stop-grip-gripper");
            }
        },
        messageRate: function(newValue) {
            this.sendSocketMessage("message-rate " + this.messageRate);
        }
    },
    methods: {
        sendSocketMessage: function(message) {
            this.socket.send(message);
            console.log(message);
        },
        keyDownUp: function() {
            this.upKey = true;
        },
        keyUpUp: function() {
            this.upKey = false;
        },
        keyDownDown: function() {
            this.downKey = true;
        },
        keyUpDown: function() {
            this.downKey = false;
        },
        keyDownLeft: function() {
            this.leftKey = true;
        },
        keyUpLeft: function() {
            this.leftKey = false;
        },
        keyDownRight: function() {
            this.rightKey = true;
        },
        keyUpRight: function() {
            this.rightKey = false;
        },
        keyDownW: function() {
            this.wKey = true;
        },
        keyUpW: function() {
            this.wKey = false;
        },
        keyDownS: function() {
            this.sKey = true;
        },
        keyUpS: function() {
            this.sKey = false;
        },
        keyDownA: function() {
            this.aKey = true;
        },
        keyUpA: function() {
            this.aKey = false;
        },
        keyDownD: function() {
            this.dKey = true;
        },
        keyUpD: function() {
            this.dKey = false;
        },
        keyDownE: function() {
            this.eKey = true;
        },
        keyUpE: function() {
            this.eKey = false;
        },
        keyDownQ: function() {
            this.qKey = true;
        },
        keyUpQ: function() {
            this.qKey = false;
        },
        keyDownR: function() {
            this.rKey = true;
        },
        keyUpR: function() {
            this.rKey = false;
        },
        keyDownF: function() {
            this.fKey = true;
        },
        keyUpF: function() {
            this.fKey = false;
        },
        keyDownY: function() {
            this.yKey = true;
        },
        keyUpY: function() {
            this.yKey = false;
        },
        keyDownX: function() {
            this.xKey = true;
        },
        keyUpX: function() {
            this.xKey = false;
        }
    }
};
</script>
