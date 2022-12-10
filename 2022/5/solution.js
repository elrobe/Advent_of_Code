// Advent of Code 2022 - Day 5
// Supply Stacks
//
// Here we're given a bunch of supply stacks and instructions on
// where to move the top of each stack. The first half of the input
// is the stack configuration and the second half (after the blank
// line) is all of the movement commands.
//
// Part 1 -
// Return what the top of each stack is after all the moves. The
// crates each get moved over one-by-one. What is the top crate
// of each stack?
//
// Part 2 -
// Now the crates are moved together to different stacks, preserving the order.
// What is the top crate of each stack?

// To run:
// - node solution.js

const fs = require('fs');

// Debugging function
const printStacks = (stacks) => {
  for (let i = 0; i < stacks.length; i++) {
    console.log((i+1) + ": " + stacks[i].join("-"));
  }
}

const parseStacks = (config) => {
  // We're going to go from the bottom up since the last number of the
  // last line is the total number of stacks there are
  const lastLine = config[config.length - 1];
  const lastLineTrim = lastLine.replaceAll("   ", "-").trim();
  const stackNums = lastLineTrim.split("-");
  const numStacks = parseInt(stackNums[stackNums.length - 1]);
  
  const stacks = [];
  for (let i = 0; i < numStacks; i++) { stacks.push([]); }

  // We have the number of stacks now, so we'll just parse through each line
  // to get the actual stacks themselves
  for (let row = config.length - 2; row >= 0; row--) {
    let curStack = 0; // Always start at stack column 1 (0-indexed)
    const line = config[row];
    for (let i = 0; i < line.length; i+=4) {
      const crate = line.substr(i, i+3);
      if (crate[0] === "[") {
        stacks[curStack].push(crate[1]);
      }
      curStack += 1; // Onto the next stack!
    }
  }

  return stacks;
}

// Parses out a single move line and returns a length-3 array
// of the number to move, source stack #, and target stack #
const parseMove = (move) => {
  // The moves are in the expected format:
  // move # from stack# to stack#
  // So we'll parse out just the #s
  const moveAry = move.split(" ");
  return [
    parseInt(moveAry[1]),
    parseInt(moveAry[3]) - 1, // Minus 1 since it's 0-indexed
    parseInt(moveAry[5]) - 1
  ];
}

// Part 1 stacking code!
const parseMoves_part1 = (stacks, moves) => {
  moves.forEach((move) => {
    const [num, source, target] = parseMove(move);
    for (let x = 0; x < num; x++) {
      const crate = stacks[source].pop();
      if (crate) {
        stacks[target].push(crate);
      }
    }
  })
}

// Part 2 stacking code! Mostly the same...
const parseMoves_part2 = (stacks, moves) => {
  moves.forEach((move) => {
    const [num, source, target] = parseMove(move);
    // The catch here is that now I have to move them all in order,
    // so I'm going to put them into a temporary array
    const tempStack = [];
    for (let x = 0; x < num; x++) {
      const crate = stacks[source].pop();
      if (crate) {
        tempStack.push(crate);
      }
    }
    tempStack.reverse(); // So we pull from the "bottom"
    // Now move them from the tempStack onto the actual target
    tempStack.forEach((crate) => stacks[target].push(crate));
  })
}

// This is effectively our "main" method...
fs.readFile("input.txt", (err, data) => {
  if (err) throw err; // Unexpected, but expect it anyway
  
  allLines = data.toString().split("\n");
  // Split the lines into two parts:
  // 1) The stack configuration
  // 2) The moves to make
  const config = [];
  const moves = [];
  let foundBreak = false;
  for (let i = 0; i < allLines.length; i++) {
    let line = allLines[i];
    if (foundBreak) {
      moves.push(line);
    }
    else {
      if (line.length === 0) { foundBreak = true; continue; }
      config.push(line);
    }
  }
  
  // Now we need to construct the 2D array of configuration
  const stacks1 = parseStacks(config);
  // Normally I'd create a copy than re-parse, but I'm not coding for a client,
  // so I'm taking the easy way out. Sorry if this makes you cringe!
  const stacks2 = parseStacks(config);

  // Part 1 output
  parseMoves_part1(stacks1, moves);
  console.log(stacks1.map((stack) => stack[stack.length - 1]).join(""));

  // Part 2 output
  parseMoves_part2(stacks2, moves);
  console.log(stacks2.map((stack) => stack[stack.length - 1]).join(""));
});