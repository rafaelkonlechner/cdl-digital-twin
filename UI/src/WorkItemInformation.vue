<style scoped>
.description {
    margin-top: 10px;
    color: #757575;
    font-size: 0.9em;
}
</style>
<template>
<div style="max-width: 320px;">
    <h2>Work Item Information</h2>
    <div style="display: inline-block;">
        <table>
            <tr>
                <td width="100">
                    <strong>ID</strong>
                </td>
                <td>{{qrCode.id}}</td>
            </tr>
            <tr>
                <td>
                    <strong>Batch-ID</strong>
                </td>
                <td>{{qrCode.batchId}}</td>
            </tr>
            <tr>
                <td>
                    <strong>Color</strong>
                </td>
                <td>{{qrCode.color}}</td>
            </tr>
            <tr>
                <td>
                    <strong>URL</strong>
                </td>
                <td>
                    <span v-if="qrCode.id === '-'">{{qrCode.id}}</span>
                    <a v-if="qrCode.id !== '-'" href="#">http://cdl.io/{{qrCode.id}}</a>
                </td>
            </tr>
        </table>
    </div>
    <div style="display: inline-block; float: right;">
        <img v-if="qrCode.base64" style="width: 75px; height: 75px; margin: 0;" v-bind:src="qrCode.base64" />
    </div>
    <p class="description">Read current item info contained in the QR code, currently scanned by the testing rig.
    </p>
</div>
</template>
<script>
export default {
    props: ["socket"],
    mounted() {
        var self = this;
        self.socket.addEventListener("message", function(event) {
            let msg = JSON.parse(event.data);
            if (msg.topic === "qrCode") {
                let data = JSON.parse(msg.message)
                self.qrCode = data;
                self.qrCode.base64 = 'data:image/png;base64, ' + self.qrCode.base64;
            }
        });
    },
    data() {
        return {
            qrCode: {
                id: "-",
                batchId: "-",
                color: "-",
                base64: "assets/code-1.png",
                url: "-"
            }
        }
    }
}
</script>
