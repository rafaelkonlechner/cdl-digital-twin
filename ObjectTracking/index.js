const express = require('express')
const fileUpload = require('express-fileupload')
const app = express()
app.use(fileUpload())

app.post('/analyze', function(req, res) {
  if (!req.files)
    return res.status(400).send('No image was found in the request.');
  let imageFile = req.files.file;

  imageFile.mv('current.jpg', function(err) {
    if (err)
      return res.status(500).send(err);
    res.send('File uploaded');
  });
});

app.listen(3000, function () {
  console.log('Object Tracker listening on port 3000.')
})
