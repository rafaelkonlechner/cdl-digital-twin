import 'material-design-lite/material.min.css'
import 'material-design-lite/material.min.js'
import './styles.css'
import Vue from 'vue'
import App from './App.vue'
window.addEventListener("keydown", function(e) {
    // space and arrow keys
    if ([32, 37, 38, 39, 40].indexOf(e.keyCode) > -1) {
        e.preventDefault();
    }
}, false);
new Vue({
    el: '#app',
    render: h => h(App)
});