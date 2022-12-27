// Advent of Code 2022 - Day 14
// Regolith Reservoir
//
// You're in a cave and notice sand pouring in from the ceiling.
// The inputs are series of walls that block the sand on it's path to the floor.
// A grain of sand will come to rest when it can't move down, then left, then right.
//
// Part 1 -
// Using the input walls, what is the total number of grains of sand that will come to rest
// before subsequent falling grains fall into the infinite void?

// To run:
// - Make sure you have node installed and run 'npm install' from the /14 directory
// - Once everything is installed, run 'node app.js'
// - In a browser, navigate to 'localhost:8080'
// - You'll see the main page where you can then click the buttons to run the
//   sample input or run using the main input.

const TABLE_Y_MIN = 0; // Safe assumption that 0 is the absolute Y-min

let TABLE_X_MIN = Number.MAX_SAFE_INTEGER;
let TABLE_X_MAX = Number.MIN_SAFE_INTEGER;
let TABLE_Y_MAX = Number.MIN_SAFE_INTEGER;

let GRAINS = 0;

// Helper function to load a specific file from the front-end
const load = (file) => {
  $("#load1").prop("disabled", true);
  $("#load2").prop("disabled", true);
  $("#run1").prop("disabled", false);
  $("#run2").prop("disabled", false);

  fetch(file)
    .then((res) => res.text()
      .then((text) => displayInputData(text)));
}

// Call-in from the front-end, wrapper for the main function.
const start = (fastMode) => {
  $("#run1").prop("disabled", true);
  $("#run2").prop("disabled", true);

  moveGrains(fastMode);
}

// Resets the state of the application
const reset = () => {
  $("#load1").prop("disabled", false);
  $("#load2").prop("disabled", false);
  $("#run1").prop("disabled", true);
  $("#run2").prop("disabled", true);
  
  GRAINS = 0;
  $("#counter").text("0");

  $("#cave").empty();
}

// Fast mode runs up to the ending before displaying the final output.
// Not-fast mode will run it as an animation, which may take a while...
const moveGrains = (fastMode) => {
  if (!fastMode) {
    const moved = moveGrain();
    if (moved) {
      setTimeout(() => moveGrains(false), 0); // Trigger the next!
    }
  } else {
    while (moveGrain()) {}
  }
}

// Moves the current falling grain one step or initiates
// the next grain to start falling.
const moveGrain = () => {
  let currentGrain = $("[data-id^='grain']");

  // Initiate the next grain.
  if (currentGrain.length == 0) {
    GRAINS++;
    $("#counter").text(GRAINS);
    const origin = $("#x500y0");
    origin.text("o");
    origin.attr("data-id", "grain");
    currentGrain = origin;
  }
  
  // Now move the grain
  const position = currentGrain.attr("id");
  const x = parseInt(position.substr(1, position.indexOf("y")));
  const y = parseInt(position.substr(position.indexOf("y") + 1));

  let nextPosition = $("#x" + x + "y" + (y+1));
  // Edge case for we fell off the map
  if (nextPosition.length === 0) {
    GRAINS--;
    $("#counter").text(GRAINS);
    currentGrain.attr("data-id", "");
    alert(GRAINS + " have come to rest!"); // <------ FINAL OUTPUT
    return false; // This will stop running altogether
  }

  // --------------------------------------------
  // Movement logic
  // Downward move
  let moved = false;
  let next = nextPosition.text(); 
  if (next === "-") { // Always move into the next spot if it's free
    currentGrain.text("-");
    currentGrain.attr("data-id", "");
    nextPosition.text("o");
    nextPosition.attr("data-id", "grain");
    moved = true;
  } else if (next !== "-") { // We've run into a grain, so start stacking
    // First try going to the left
    nextPosition = $("#x" + (x-1) + "y" + (y+1));
    next = nextPosition.text();
    if (next === "-") {
      currentGrain.text("-");
      currentGrain.attr("data-id", "");
      nextPosition.text("o");
      nextPosition.attr("data-id", "grain");
      moved = true;
    } else {
      // Then try going to the right
      nextPosition = $("#x" + (x+1) + "y" + (y+1));
      next = nextPosition.text();
      if (next === "-") {
        currentGrain.text("-");
        currentGrain.attr("data-id", "");
        nextPosition.text("o");
        nextPosition.attr("data-id", "grain");
        moved = true;
      }
    }
  }

  // Else, the grain is stuck and we can move onto the next grain
  if (!moved) { // Grain has nowhere to go
    currentGrain.attr("data-id", "");
    currentGrain.attr("data-type", "grain");
  }
  return true; // I.e. keep running this function
}

// This function creates the base table we'll parse the lines into
const initTable = () => {
  const cave = $("#cave");
  if (!cave) {
    return;
  }
  for (let y = TABLE_Y_MIN; y < TABLE_Y_MAX; y++) {
    const row = $("<tr/>");
    for (let x = TABLE_X_MIN; x < TABLE_X_MAX; x++) {
      const title = "x" + x + "y" + y;
      const cell = $("<td/>", { id: title, title: title, text: "-" }); // Instead of '.' we'll use a space
      row.append(cell);
    }
    cave.append(row);
  }
}

// Create the line from coordinates 1 to coordinates 2
const createLine = (x1, y1, x2, y2) => {
  let s, e;
  // X positions are equal -> Y line
  if (x1 == x2) {
    s = y1
    e = y2
    if (y1 > y2) {
      s = y2
      e = y1
    }
    for (let y = s; y <= e; y++) {
      const id = "#x" + x1 + "y" + y;
      $(id).text("#");
      $(id).attr("data-type", "wall");
    }
  } else { // Else X line
    s = x1
    e = x2
    if (x1 > x2) {
      s = x2
      e = x1
    }
    for (let x = s; x <= e; x++) {
      const id = "#x" + x + "y" + y1;
      $(id).text("#");
      $(id).attr("data-type", "wall");
    }
  }
}

// We'll actually construct the lines in the table now
const parseLines = (lines) => {
  const lineAry = [];
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    const positions = line.split(" -> ");

    // Create each contiguous line, one by one
    for (let p = 0; p < positions.length - 1; p++) {
      const start = positions[p].split(",");
      const end   = positions[p+1].split(",");

      const x1 = parseInt(start[0]);
      const y1 = parseInt(start[1]);
      const x2 = parseInt(end[0]);
      const y2 = parseInt(end[1]);
      lineAry.push([x1, y1, x2, y2]);

      // Set the bounds of our grid
      TABLE_X_MIN = Math.min(x1, x2, TABLE_X_MIN);
      TABLE_X_MAX = Math.max(x1, x2, TABLE_X_MAX);
      TABLE_Y_MAX = Math.max(y1, y2, TABLE_Y_MAX);
    }
  }

  // Add one to the bounds
  TABLE_X_MIN = TABLE_X_MIN - 1;
  TABLE_X_MAX = TABLE_X_MAX + 2;
  TABLE_Y_MAX = TABLE_Y_MAX + 2;
  initTable();

  // Now go through and create the grid
  for (let l = 0; l < lineAry.length; l++) {
    const coords = lineAry[l];
    createLine(coords[0], coords[1], coords[2], coords[3]);
  }
}

const displayInputData = (text) => {
  const lines = text.split("\n");
  parseLines(lines);
}