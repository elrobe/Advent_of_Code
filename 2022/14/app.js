const express = require('express');
const path = require('path');

const app = express();
const port = 8080;

app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, '/index.html'));
});

app.get('/solution.js', (req, res) => {
  res.sendFile(path.join(__dirname, '/solution.js'));
});

app.get('/sample_input.txt', (req, res) => {
  res.sendFile(path.join(__dirname, '/sample_input.txt'))
});

app.get('/input.txt', (req, res) => {
  res.sendFile(path.join(__dirname, '/input.txt'))
});

app.listen(port, () => {
  console.log(`Listening on port ${port}`)
});