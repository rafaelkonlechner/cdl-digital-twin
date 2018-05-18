<style scoped>
.state {
  width: 155px;
  height: 150px;
  background-color: white;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  padding: 5px 10px;
  font-size: 9px;
}

.shadow {
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
}

.state .active {
  border: 1px solid deepskyblue;
}

.transition-detail {
  text-align: center;
  width: 250px;
  height: 150px;
  background-color: white;
}
</style>
<template>
  <svg width="1000" height="400">
    <defs>
      <marker id="arrow" markerWidth="10" markerHeight="10" refX="0" refY="3" orient="auto" markerUnits="strokeWidth" viewBox="0 0 20 20">
        <path d="M0,0 L0,6 L9,3 z" fill="#00C8FF" />
      </marker>
    </defs>
    <foreignObject v-for="(state, index) in states" :key="state.id" :id="state.id" :x="state.x" :y="state.y">
      <div class="state shadow">
        <h3>{{state.name}}</h3>
        <div>Base: {{state.basePosition}}</div>
        <div>Upper Arm: {{state.upperArmPosition}}</div>
        <div>Lower Arm: {{state.lowerArmPosition}}</div>
      </div>
    </foreignObject>
    <line v-for="(transition, index) in transitions" :key="transition.id + 'l1'" :id="transition.id" class="shadow" :x1="index * 500 + 222" y1="350" :x2="(index+1) * 500 - 20" y2="350" style="stroke:rgb(0,200,255); stroke-width:5" marker-end="url(#arrow)" />
    <line v-for="(transition, index) in transitions" :key="transition.id + 'l2'" :id="transition.id" :x1="index * 500 + 372" y1="330" :x2="index * 500 + 372" y2="350" style="stroke:rgb(0,200,255); stroke-width:5" />
  </svg>
</template>

<script>
let states = [
  {
    id: "idle",
    name: "Idle",
    basePosition: 1000,
    upperArmPosition: 210,
    lowerArmPosition: 200,
    x: 100,
    y: 250
  },
  {
    id: "pickup",
    name: "Pickup",
    basePosition: 1000,
    upperArmPosition: 2000,
    lowerArmPosition: 1380,
    x: 600,
    y: 250
  },
  {
    id: "move",
    name: "Move",
    basePosition: 2000,
    upperArmPosition: 1250,
    lowerArmPosition: 2000,
    x: 1100,
    y: 250
  }
];
let transitions = [
  {
    id: "t1",
    from: states[0],
    to: states[1]
  },
  {
    id: "t2",
    from: states[1],
    to: states[2]
  }
];

export default {
  name: "state-chart",
  props: [],
  data() {
    return {
      states: states,
      transitions: transitions
    };
  }
};
</script>