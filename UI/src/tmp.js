/*
stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    stompClient.subscribe('/topic/sensor', function (response) {
        vue.updateSensorData(response.body);
    });
    stompClient.subscribe('/topic/context', function (response) {
        vue.updateContextData(response.body);
    });
    stompClient.subscribe('/topic/pickupCamera', function (response) {
        vue.updatePickupImage(response.body);
    });
    stompClient.subscribe('/topic/detectionCamera', function (response) {
        vue.updateDetectionImage(response.body);
    });
    stompClient.subscribe('/topic/qrCode', function (response) {
        vue.updateQRCodeData(response.body);
    });
});*/

// Simple Path
window.onload = function() {
    var canvas = document.getElementById('canvas');
    paper.setup(canvas);
    var path = new paper.Path();
    path.strokeColor = 'black';
    var start = new paper.Point(100, 100);
    path.moveTo(start);
    path.lineTo(start.add([ 200, -50 ]));
    paper.view.draw();
};

// Rotate Path
window.onload = function() {
    var canvas = document.getElementById('canvas');
    paper.setup(canvas);
    var path = new paper.Path();
    path.strokeColor = 'black';
    var start = new paper.Point(100, 100);
    path.moveTo(start);
    path.lineTo(start.add([ 200, -50 ]));
    paper.view.draw();
    paper.view.onFrame = function(event) {
        path.rotate(1);
    };
};

// Tool
window.onload = function() {
    var canvas = document.getElementById('canvas');
    paper.setup(canvas);
    var path;
    var tool = new paper.Tool();
    tool.onMouseDown = function(event) {
        path = new paper.Path();
        path.strokeColor = 'black';
        path.add(event.point);
    };
    tool.onMouseDrag = function(event) {
        path.add(event.point);
    }
};




/*
stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    stompClient.subscribe('/topic/sensor', function (response) {
        vue.updateSensorData(response.body);
    });
    stompClient.subscribe('/topic/context', function (response) {
        vue.updateContextData(response.body);
    });
    stompClient.subscribe('/topic/pickupCamera', function (response) {
        vue.updatePickupImage(response.body);
    });
    stompClient.subscribe('/topic/detectionCamera', function (response) {
        vue.updateDetectionImage(response.body);
    });
    stompClient.subscribe('/topic/qrCode', function (response) {
        vue.updateQRCodeData(response.body);
    });
});*/

paper.install(window);
window.onload = function() {
    var canvas = document.getElementById('canvas');
    paper.setup(canvas);

// The amount of segment points we want to create:
    var amount = 5;

// The maximum height of the wave:
    var height = 40;

// Create a new path and style it:
    var path = new Path();
    path.strokeColor = 'black';
    path.onClick = function(event) {
        console.log("click")
    };
    // Add 5 segment points to the path spread out
    // over the width of the view:
    for (var i = 0; i <= amount; i++) {
        var p = new paper.Point(i * 1000 / amount, 1);
        p.onClick = function(event) {
            console.log("click");
        };
        path.add(p);
    }

    // Select the path, so we can see how it is constructed:
    path.selected = true;

    var tool = new paper.Tool();
    var selected = null;

    /*tool.onMouseDown = function(event) {
        console.log(event.currentTarget);
    };*/

    /*tool.onMouseDrag = function(event) {

    };*/

    /*tool.onMouseUp = function(event) {
    };*/


    /*paper.view.onFrame = function(event) {
        // Loop through the segments of the path:
        for (var i = 0; i <= amount; i++) {
            var segment = path.segments[i];

            // A cylic value between -1 and 1
            var sinus = Math.sin(event.time * 3 + i);

            // Change the y position of the segment point:
            segment.point.y = sinus * height + 100;
        }
        path.smooth();
    }*/
};
