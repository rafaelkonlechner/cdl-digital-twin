<style scoped>
</style>
<template>
<div style="width: 320px;">
    <h6>Work Item Information</h6>
    <div>
        <table style="text-align: left; margin-left: 40px;">
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
    <img v-if="qrCode.base64" style="width: 75px; height: 75px; margin-top: 20px; border-style: solid; border-width: 3px; border-color: black;" v-bind:src="qrCode.base64" />
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
