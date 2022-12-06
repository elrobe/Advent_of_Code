// Advent of Code 2022 - Day 3
// Rucksack Reorganization
//
// Each line in the input is a rucksack.
// Each rucksack is divided into two compartments.
// Each compartment is a series of letters indicating an item.
// Each item has a priority value:
// a: 1, b: 2, ..., z: 26, A: 27, B: 28, ..., Z: 52
//
// Part 1 -
// Find the item that appears in both compartments and sum up
// all of the priority values for the duplicative item in each sack.

// To run:
// - Requires node.js
// - In a terminal, use the command: node solution.js
// - Voila

const fs = require('fs');

const findDuplicate = (a, b) => {
    const map = {};
    for (let i = 0; i < a.length; i++) {
        map[a[i]] = 1;
    }
    for (let i = 0; i < b.length; i++) {
        if (map[b[i]] === 1) return b[i];
    }
    return -1; // Unexpected, but expect it anyway
}

const checkSacks = (sackAry) => {
    let prioritySum = 0;
    const a_val = 'a'.charCodeAt(0);
    const A_val = 'A'.charCodeAt(0);
    // For each rucksack...
    for (let i = 0; i < sackAry.length; i++) {
        // Divide each sack into the two compartments
        const sack = sackAry[i];
        const comp1 = sack.substr(0, sack.length / 2);
        const comp2 = sack.substr(sack.length / 2);
        // Now find the duplicative letter in both
        const dup = findDuplicate(comp1, comp2);
        if (dup === -1) throw ("Unexpected value found in rucksack: " + sack)
        // Get the priority value
        if (dup > 'Z') { // Must be capital
            priority = dup.charCodeAt(0) - a_val + 1;
        } else {
            priority = dup.charCodeAt(0) - A_val + 27;
        }
        prioritySum += priority;
    }

    // Final output
    console.log(prioritySum);
}

fs.readFile("input.txt", (err, data) => {
    if (err) throw err; // Unexpected, but expect it anyway
    
    ruckSacks = data.toString().split("\n");
    checkSacks(ruckSacks);
});