<style scoped>
.container {
    display: inline-block
}

.signal {
    width: 150px;
    height: 150px;
}
</style>
<template>
<div class="container">
    <div v-bind:id="containerId" style="position: relative;">
        <img v-bind:id="imageId" class="signal" v-bind:src="imageBase64" />
    </div>
</div>
</template>
<script>
export default {
    props: ["title", "topic", "fixedBox", "socket"],
    data() {
        return {
            imageBase64: "assets/no-signal.png",
            containerId: null,
            imageId: null
        }
    },
    created() {
        this.containerId = Math.random().toString(36).substring(2, 15)
        this.imageId = Math.random().toString(36).substring(2, 15)
    },
    mounted() {
        if (this.fixedBox !== undefined && this.fixedBox.length == 6) {
            var f = this.fixedBox
            this.plotBox(f[0], f[1], f[2], f[3], f[4], f[5], true);
        }
        this.socket.addEventListener("message", event => {
            let msg = JSON.parse(event.data);
            if (msg.topic === this.topic) {
                let data = JSON.parse(msg.message)
                this.imageBase64 = 'data:image/png;base64, ' + data.image;
                this.updateTrackingInformation(data.tracking, 'Item')
            }
        });
    },
    methods: {
        updateTrackingInformation(event, text) {
            var container = document.getElementById(this.containerId);
            var transients = container.getElementsByClassName('transient');
            if (transients !== null) {
                for (var i = 0; i < transients.length; i++) {
                    container.removeChild(transients[i]);
                }
            }
            for (var i = 0; i < event.length; i++) {
                let rect = event[i]
                this.plotBox(rect.x, rect.y, rect.width, rect.height, 'yellow', text, false);
            }
        },
        plotBox(x, y, w, h, color, text, permanent) {
            var rect = document.createElement('div');
            document.getElementById(this.containerId).appendChild(rect);
            var img = document.getElementById(this.imageId);
            if (permanent) {
                rect.classList.add('permanent');
            } else {
                rect.classList.add('transient');
            }
            rect.style.border = '4px solid ' + color;
            rect.style.position = 'absolute';
            rect.style.width = (w + 2) + 'px';
            rect.style.height = (h + 2) + 'px';
            rect.style.left = (img.offsetLeft + x) + 'px';
            rect.style.top = (img.offsetTop + y) + 'px';
            if (text !== "") {
                var textElement = document.createElement('p');
                if (permanent) {
                    textElement.classList.add('permanent')
                } else {
                    textElement.classList.add('transient')
                }
                document.getElementById(this.containerId).appendChild(textElement);
                var node = document.createTextNode(text);
                textElement.appendChild(node);
                textElement.style.position = 'absolute';
                textElement.style.color = color;
                textElement.style.fontFamily = 'Roboto Mono';
                textElement.style.fontSize = '0.8 em';
                textElement.style.left = (img.offsetLeft + x) + 'px';
                textElement.style.top = (img.offsetTop + y - 25) + 'px';
            }
        }
    }
}
</script>
