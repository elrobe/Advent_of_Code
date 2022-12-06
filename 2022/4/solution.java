// Advent of Code 2022 - Day 4
// Camp Cleanup
//
// Part 1:
// Elves have divided into pairs.
// Each elf has a designated range of areas to clean.
// How many of these pairs have an elf that's range is
// completely encapsulated in the other elf's range?

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
         * Checks if one range is encapsulated in (a complete subset of) the other range.
         * @param r1 Range 1
         * @param r2 Range 2
         * @return The range that encapsulates the other, null if neither encapsulates.
         */
        public static Range checkEncapsulation(Range r1, Range r2) {
            if (r1.Lo <= r2.Lo && r1.Hi >= r2.Hi) return r1;
            if (r2.Lo <= r1.Lo && r2.Hi >= r1.Hi) return r2;
            return null;
        }

        public String toString() {
            return this.Lo + "-" + this.Hi;
        }
    }

    // For Part 1
    private static int checkRanges_part1(String pair) {
        String[] range = pair.split(",");
        Range r1 = new Range(range[0]);
        Range r2 = new Range(range[1]);

        Range encapsulator = Range.checkEncapsulation(r1, r2);
        return (encapsulator != null ? 1 : 0);
    }

    public static void main(String[] args) {
        File f = new File("input.txt");
        Scanner s = null;
        try {
            s = new Scanner(f);
            int partOneCount = 0;

            while (s.hasNextLine()) {
                String pair = s.nextLine();
                partOneCount += checkRanges_part1(pair);
            }

            System.out.println("Part 1: " + partOneCount);

        } catch (Exception e) {
            System.err.println("Error reading input file.");
        } finally {
            // Don't forget to close the file reader
            if (s != null) s.close();
        }
    }
}