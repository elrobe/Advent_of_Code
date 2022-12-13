// Advent of Code 2022 - Day 10
// Cathode-Ray Tube
//
// You fell off the bridge from day 9 and your communication device
// got all wet. So now we're trying to decipher its signal strength
// based on certain clock cyles.
// We know there are two operations it can perform and only one
// register, X:
// noop = 1 clock cycle to complete
// addx # = 2 clock cycles to complete, adds # to the register
//
// Part 1 -
// Given the commands, return the sum of the X register at the
// devices clock cycles: 20, 60, 100, 140, 180, and 220.

// To run:
// - Compile with 'gcc solution.c'
// - Run with './a.out'

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void increment_cycle(int* cycle, int* x, int* sum) {
  *cycle = *cycle + 1;
  int c = *cycle; // For easier writing
  if (c == 20 || c == 60 || c == 100 || c == 140 || c == 180 || c == 220) {
    *sum = *sum + (*x * *cycle);
  }
}

void part_one(int* cycle, int* x, int* sum, char* command, size_t command_len) {
  // First, we need to parse the command out
  if (command_len == 4) { // Inherent "noop"
    increment_cycle(cycle, x, sum);
  } else { // 
    int val_len = command_len - 4; // Number of characters of the numeric value to add to X
    char* add_val = malloc(val_len);
    // My non-clever way to copy the addx value into a "string"
    for (int i = 0; i < val_len; i++) {
      add_val[i] = command[4 + i];
    }
    int val = atoi(add_val);
    free(add_val);
    // Now we have to increment the cycle, then add to X before incrementing the cycle
    // again to simulate X being updated at the end of the cycle.
    increment_cycle(cycle, x, sum);
    *x = *x + val;
    increment_cycle(cycle, x, sum);
  }
}

int main() {
  // File-reading variables
  FILE* f = fopen("input.txt", "r");
  size_t len;
  char* line;

  // Part 1 variables
  int* cycle = malloc(sizeof(int));
  int* x = malloc(sizeof(int));
  int *signal_sum = malloc(sizeof(int));
  *cycle = 1;
  *x = 1;
  *signal_sum = 0;

  line = fgetln(f, &len);
  while (!feof(f)) {
    if (line[len - 1] == '\n') {
      len = len - 1; // "Strip off" the newline
    }

    part_one(cycle, x, signal_sum, line, len);

    // Get the next line
    line = fgetln(f, &len);
  }

  printf("Part 1: %d\n", *signal_sum);

  free(cycle);
  free(x);
  free(signal_sum);

  fclose(f);
}