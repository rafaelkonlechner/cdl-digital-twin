<template>
<div style="background-color: #1E88E5;">
    <div id="webgl"></div>
</div>
</template>
<script>
import * as THREE from "three";
var OrbitControls = require("three-orbit-controls")(THREE);
var Loaders = require("three-loaders-collada")(THREE);

export default {
    name: "webgl",
    props: ["width", "height"],
    data() {
        return {
            x: new THREE.Vector3(1, 0, 0),
            y: new THREE.Vector3(0, 1, 0),
            z: new THREE.Vector3(0, 0, 1),
            loader: new THREE.ColladaLoader(),
            scene: new THREE.Scene(),
            camera: new THREE.PerspectiveCamera(
                75,
                window.innerWidth / window.innerHeight,
                1,
                10000
            ),
            renderer: new THREE.WebGLRenderer({
                antialias: true,
                clearColor: 0x1E88E5,
                alpha: 1
            }),
            controls: null,
            raycaster: new THREE.Raycaster(),
            mouse: new THREE.Vector2(),
            selectableObjects: [],
            selectedObjectBox: null,
            plane: null
        };
    },
    mounted() {

        let self = this;
        var light = new THREE.AmbientLight(0x404040);
        self.scene.add(light);
        var light1 = new THREE.PointLight(0x808080, 1);
        var light2 = new THREE.PointLight(0x808080, 1);
        var light3 = new THREE.PointLight(0x808080, 1);
        var light4 = new THREE.PointLight(0x808080, 1);

        light1.position.set(-5, 15, -5);
        light2.position.set(-5, 15, 5);
        light3.position.set(5, 15, -5);
        light4.position.set(5, 15, 5);
        self.scene.add(light1);
        self.scene.add(light2);
        self.scene.add(light3);
        self.scene.add(light4);

        self.camera.position.set(0, 15, 15);
        self.camera.lookAt(0, 0, 0)
        self.renderer.setSize(self.width, self.height);
        self.controls = new OrbitControls(self.camera, self.renderer.domElement);

        self.scene.add(new THREE.GridHelper(100, 10, 0xFFFFFF, 0xFFFFFF));

        var geometry = new THREE.PlaneGeometry(100, 100, 1, 1);
        var material = new THREE.MeshBasicMaterial({
            color: 0x1E88E5,
            side: THREE.DoubleSide
        });
        var plane = new THREE.Mesh(geometry, material);
        plane.rotateOnAxis(self.x, THREE.Math.degToRad(-90));
        self.scene.add(plane);

        window.addEventListener("click", self.onMouseClick, false);
        document.getElementById("webgl").appendChild(self.renderer.domElement);
        self.loader.load("assets/kuka.dae", function(collada) {
            var child = collada.scene;
            var children = [];
            child.traverse(function(x) {
                if (x instanceof THREE.Mesh) {
                    x.rotateOnAxis(self.x, THREE.Math.degToRad(-90));
                    x.scale.set(10, 10, 10);
                    children.push(x);
                    self.selectableObjects.push(x);
                }
            });
            children.forEach(function(x) {
                self.scene.add(x);
            })
            self.animate();
        });
    },
    methods: {
        render: function() {
            this.renderer.render(this.scene, this.camera);
        },
        animate: function() {
            requestAnimationFrame(this.animate);
            this.render();
        },
        getMousePosition: function(dom, x, y) {
            var rect = dom.getBoundingClientRect();
            return [(x - rect.left) / rect.width, (y - rect.top) / rect.height];
        },
        getIntersects: function(point, objects) {
            this.mouse.set(point.x * 2 - 1, -(point.y * 2) + 1);
            this.raycaster.setFromCamera(this.mouse, this.camera);
            return this.raycaster.intersectObjects(objects);
        },
        onMouseClick: function(event) {
            var mousePosition = this.getMousePosition(
                this.renderer.domElement,
                event.clientX,
                event.clientY
            );
            this.mouse.x = mousePosition[0];
            this.mouse.y = mousePosition[1];
            var intersects = this.getIntersects(this.mouse, this.selectableObjects);
            if (intersects.length > 0) {
                var first = intersects[0].object;
                this.scene.remove(this.selectedObjectBox);
                this.selectedObjectBox = new THREE.BoxHelper(first, 0xffff00);
                this.scene.add(this.selectedObjectBox);
            } else {
                this.scene.remove(this.selectedObjectBox);
            }
        }
    }
};
</script>
