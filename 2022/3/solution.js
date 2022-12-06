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
//
// Part 2 -
// Each elf is in a group of three. Each group of 3 should have
// one item that resides in each of their bags. Find the item and
// sum the priority of these items for each group.

// To run:
// - Requires node.js
// - In a terminal, use the command: node solution.js
// - Voila

const fs = require('fs');

// For Part 1
// Create a map of one compartment, then iterate over the
// second compartment to see if the key exists in the map.
// Hooray of O(1) lookup
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

// For Part 2
// Same as findDuplicate, but tries to find the one item that
// resides in three entire rucksacks instead of two compartments.
const findTriplicate = (a, b, c) => {
    const mapA = {};
    const mapB = {};
    for (let i = 0; i < a.length; i++) {
        mapA[a[i]] = 1;
    }
    for (let i = 0; i < b.length; i++) {
        mapB[b[i]] = 1;
    }
    for (let i = 0; i < c.length; i++) {
        if (mapA[c[i]] === 1 && mapB[c[i]]) return c[i];
    }
    return -1; // Unexpected, but expect it anyway
}

// Reusable for both parts
const getPriority = (val) => {
    const aVal = 'a'.charCodeAt(0);
    const AVal = 'A'.charCodeAt(0);
    const curVal = val.charCodeAt(0);
    if (curVal < aVal) { // Must be uppercase
        return curVal - AVal + 27;
    } else {
        return curVal - aVal + 1;
    }
}

const checkSacks_part1 = (sackAry) => {
    let prioritySum = 0;
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
        prioritySum += getPriority(dup);
    }

    // Final output
    console.log("Part 1: " + prioritySum);
}

const checkSacks_part2 = (sackAry) => {
    let prioritySum = 0;
    // For each group of 3 rucksacks...
    for (let i = 0; i < sackAry.length; i+=3) {
        // Divide each sack into the two compartments
        const sack1 = sackAry[i];
        const sack2 = sackAry[i+1];
        const sack3 = sackAry[i+2];
        // Now find the duplicative letter in both
        const dup = findTriplicate(sack1, sack2, sack3);
        if (dup === -1) throw ("Unexpected value found in rucksack: " + sack)
        // Get the priority value
        prioritySum += getPriority(dup);
    }

    // Final output
    console.log("Part 2: " + prioritySum);
}

fs.readFile("input.txt", (err, data) => {
    if (err) throw err; // Unexpected, but expect it anyway
    
    ruckSacks = data.toString().split("\n");
    checkSacks_part1(ruckSacks);
    checkSacks_part2(ruckSacks);
});