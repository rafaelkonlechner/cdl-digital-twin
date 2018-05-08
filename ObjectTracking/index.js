const tracking = require('./tracking-node')
const fs = require('fs')
const express = require('express')
const fileUpload = require('express-fileupload')
const png = require('png-js')
const jimp = require('jimp');
const app = express()
app.use(fileUpload())
tracking.ColorTracker.registerColor('white', function(r, g, b) {
    return r > 253 && g > 253 && b > 253;
});
var tracker = new tracking.ColorTracker(['white']);
app.post('/analyze', function(req, res) {
    if (!req.files) return res.status(400).send('No image was found in the request.');
    let imageFile = req.files.image;
    let filename = Math.random().toString(36).substring(7) + ".png";
    let filename1 = filename + ".png";
    imageFile.mv(filename, function(err) {
        if (err) return res.status(500).send(err);
        jimp.read(filename).then(function(img) {
            img.resize(200, 200).write(filename1, function() {
                png.decode(filename1, function(pixels) {
                    tracker.once('track', function(event) {
                        var rects = []
                        event.data.forEach(function(rect) {
                            rects.push({
                                x: rect.x,
                                y: rect.y,
                                width: rect.width,
                                height: rect.height,
                                color: rect.color
                            });
                        });
                        console.log("Found " + rects.length + " items")
                        res.send(rects)
                        fs.unlink(filename, function(err) {
                            if (err) throw err;
                        })
                        fs.unlink(filename1, function(err) {
                            if (err) throw err;
                        })
                    });
                    tracker.track(pixels, 200, 200)
                });
            });
        });
    });
});
app.listen(3000);
console.log('Object Tracker listening on port 3000.')