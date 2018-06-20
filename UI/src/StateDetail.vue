<style scoped>
</style>
<template>
<div>
    <h1>{{state.name}}</h1>
    <span v-if="Object.keys(state).length > 1" class="attribute" v-for="(value,key) in state">{{key}}: {{value}}</br></span>
</div>
</template>

<script>
export default {
    props: ["state", "context", "socket"],
}
</script>
