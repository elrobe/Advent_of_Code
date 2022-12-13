// Advent of Code 2022 - Day 9
// Rope Bridge
//
// To distract yourself from a sketchy rope bridge, you came up
// with a scenario where if you move a rope head, the tail follows.
// The tail must always be adjacent to the head, while the head can
// also move over the tail.
//
// Part 1 -
// How many unique spots does the tail visit when following the head?

// To run:
// I used .NET 7.0 x64 for macOS and Mono (https://www.mono-project.com/)
// - Use the command 'csc solution.cs' to compile
// - Then use 'mono solution.exe' to run

using System;
using System.Collections.Generic;

// This class represents a specific "tile" that the head or tail
// is over in space where the rows and columns are "infinite":
// Up = negative row
// Down = positive row
// Right = positive col
// Left = negative col
//
// Row- Col- | Row+ Col-
// ---------------------
// Row- Col+ | Row+ Col+
// (Note that this emulates a 2D array)
public class Tile {
    // Current tile position
    public int Row { get; private set; }
    public int Col { get; private set; }
    // Unique ID to keep track of visited tiles for part 1
    public string ID { get { return TileID(this); } }

    public Tile(int r, int c) {
        this.Row = r;
        this.Col = c;
    }

    public static string TileID(Tile t) {
        return t.Row + "," + t.Col;
    }

    // Move a tile in a specified direction over to the next adjacent tile.
    // I should wrap the direction in an enumeration, but oh well.
    public void Move(string direction) {
        if (direction == "U") {
            this.Row--;
        } else if (direction == "D") {
            this.Row++;
        } else if (direction == "L") {
            this.Col--;
        } else { // direction == "R"
            this.Col++;
        }
    }
}

// Collection of both the head and tail pieces of the string and
// what tiles in space they're currently on.
public class TileString {
    public Tile Head { get; private set; }
    public Tile Tail { get; private set; }

    public TileString(Tile h, Tile t) {
        this.Head = h;
        this.Tail = t;
    }

    // Given a head position Tile, sets the Head location and
    // then sets the Tail in the appropriate following location.
    public void MoveHead(string direction) {
        // First, move the head
        this.Head.Move(direction);

        // Now we compare the tail to the head...
        // If they're on the same spot, do nothing
        if (this.Head.Row == this.Tail.Row && this.Head.Col == this.Tail.Col) {
            return; // No-op
        }
        // If the head is no more than 1 space away in any direction, then also do nothing
        else if (Math.Abs(this.Head.Row - this.Tail.Row) <= 1 &&
            Math.Abs(this.Head.Col - this.Tail.Col) <= 1) {
            return;
        }
        // Same row, but the head is further right
        else if (this.Head.Row == this.Tail.Row && this.Head.Col > this.Tail.Col) {
            this.Tail.Move("R");
        }
        // Same row, but head is further left
        else if (this.Head.Row == this.Tail.Row && this.Head.Col < this.Tail.Col) {
            this.Tail.Move("L");
        }
        // Same row, but the head is further up
        else if (this.Head.Row < this.Tail.Row && this.Head.Col == this.Tail.Col) {
            this.Tail.Move("U");
        }
        // Same row, but head is further down
        else if (this.Head.Row > this.Tail.Row && this.Head.Col == this.Tail.Col) {
            this.Tail.Move("D");
        }
        // And now where it gets tricky: the head is diagonal from the tail
        else {
            // Diagonal up
            if (this.Head.Row < this.Tail.Row) {
                this.Tail.Move("U");
            } else { // Diagonal down
                this.Tail.Move("D");
            }
            // Diagonal left
            if (this.Head.Col < this.Tail.Col) {
                this.Tail.Move("L");
            } else {
                this.Tail.Move("R");
            }
        }
    }
}

// Main method/class wrapper
public class Solution {
    public static void Main(string[] args) {
        Tile head = new Tile(0, 0); // Both start at the origin
        Tile tail = new Tile(0, 0);
        Dictionary<string, bool> map = new Dictionary<string, bool>();
        map.Add(head.ID, true); // We just need to make the unique spots

        TileString headTail = new TileString(head, tail);

        // Process as we parse
        string[] lines = System.IO.File.ReadAllLines("input.txt");
        foreach (string line in lines) {
            string[] input = line.Split(" ");
            string direction = input[0];
            int spaces = int.Parse(input[1]); // We could use TryParse, but I'm confident in the input
            
            // Here we'll do the same Head move for the total number of spaces
            // and recalculate the Tail every time, adding any potentially new spaces
            // to our dictionary of unique space keys
            for (int i = 0; i < spaces; i++) {
                headTail.MoveHead(direction);
                if (!map.ContainsKey(headTail.Tail.ID)) {
                    map.Add(headTail.Tail.ID, true);
                }
            }
        }

        // For part one, the total number of keys in the dictionary is the unique spaces
        // the tail visited during its journey
        System.Console.WriteLine("Part 1: " + map.Keys.Count);
    }
}