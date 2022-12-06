// Advent of Code 2022 - Day 4
// Camp Cleanup
//
// Part 1:
// Elves have divided into pairs.
// Each elf has a designated range of areas to clean.
// How many of these pairs have an elf that's range is
// completely encapsulated in the other elf's range?
//
// Part 2:
// Same as Part 1, but how many pairs have ANY overlap
// in their ranges?

// Note that this file doesn't really demonstrate that I
// can actually use Java and OO Design. Oh well.

import java.io.File;
import java.util.Scanner;

public class solution {
    private static class Range {
        public int Lo;
        public int Hi;

        // We make a safe assumption here that val is a hyphen-delimited string
        public Range(String val) {
            String[] range = val.split("-");
            this.Lo = Integer.parseInt(range[0]);
            this.Hi = Integer.parseInt(range[1]);
        }

        /**
         * Checks if either range is encapsulated in (a complete subset of) the other range.
         * @param r1 Range 1
         * @param r2 Range 2
         * @return The range that encapsulates the other, null if neither encapsulates.
         */
        public static Range checkEncapsulation(Range r1, Range r2) {
            if (r1.Lo <= r2.Lo && r1.Hi >= r2.Hi) return r1;
            if (r2.Lo <= r1.Lo && r2.Hi >= r1.Hi) return r2;
            return null;
        }

        /**
         * Checks if either range overlaps with the other.
         * @param r1 Range 1
         * @param r2 Range 2
         * @return True if there's any overlap.
         */
        public static boolean rangesOverlap(Range r1, Range r2) {
            // Low of one range is inside the other range
            if (r1.Lo >= r2.Lo && r1.Lo <= r2.Hi) return true;
            if (r2.Lo >= r1.Lo && r2.Lo <= r1.Hi) return true;
            // High of one range is inside the other range
            if (r1.Hi >= r2.Lo && r1.Hi <= r2.Hi) return true;
            if (r2.Hi >= r1.Lo && r2.Hi <= r1.Hi) return true;
            return false;
        }

        // For debugging
        public String toString() {
            return this.Lo + "-" + this.Hi;
        }
    }

    /**
     * Part 1 & 2 reusable function that parses out the ranges from a range pair string
     * and returns whether or not the pair adheres to the criteria from the relevant part.
     * @param pair Pair string, expected format: <Lo1>-<Hi1>,<Lo2>-<Hi2>
     * @param partOne True if we're processing part one, false for part two.
     * @return 1 if the range qualifies for the part, 0 otherwise.
     */
    private static int checkRanges(String pair, boolean partOne) {
        String[] range = pair.split(",");
        Range r1 = new Range(range[0]);
        Range r2 = new Range(range[1]);

        if (partOne) {
            Range encapsulator = Range.checkEncapsulation(r1, r2);
            return (encapsulator != null ? 1 : 0);
        } else { // Part 2
            return (Range.rangesOverlap(r1, r2) ? 1 : 0);
        }
    }

    // To run:
    // - Make sure you have a JDK installed.
    // - In a terminal, run the command: java solution.java
    // - Voila
    public static void main(String[] args) {
        File f = new File("input.txt");
        Scanner s = null;
        try {
            s = new Scanner(f);
            int partOneCount = 0;
            int partTwoCount = 0;

            while (s.hasNextLine()) {
                String pair = s.nextLine();
                partOneCount += checkRanges(pair, true);
                partTwoCount += checkRanges(pair, false);
            }

            System.out.println("Part 1: " + partOneCount);
            System.out.println("Part 2: " + partTwoCount);

        } catch (Exception e) {
            System.err.println("Error reading input file.");
        } finally {
            // Don't forget to close the file reader
            if (s != null) s.close();
        }
    }
}