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
//
// Part 2 -
// The device has a screen that's got 6 rows of 40 pixels. The current
// value in the X register denotes the middle position of a 3-pixel sprite
// to draw. At the beginning of each clock cycle, a pixel is drawn, either
// a "#" if the pixel position is within the current X register sprite
// position, or a "." otherwise.
// Using the input, what 8 capital letters appear on the screen?

// To run:
// - Compile with 'gcc solution.c'
// - Run with './a.out'

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Part 2 global variable...
char** screen;

void increment_cycle(int* cycle, int* x, int* sum) {
  // ------ Start of cycle (if no-op)
  // Part 2
  int sprite_pos = *x;
  int row = *cycle / 40; // Int-division for the CRT row
  int col = *cycle % 40; // Modulo for the CRT column
  int diff = sprite_pos - col;
  if (diff < 0) { diff = diff * -1; }
  if (diff <= 1) {
    screen[row][col] = '#';
  }

  *cycle = *cycle + 1;
  // ------ End of cycle (if addx)

  // Part 1
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

  // Part 2 variables (note that "screen" is global for simplicity)
  int rows = 6;
  screen = malloc(sizeof(char*) * rows); // 6 rows
  for (int i = 0; i < rows; i++) {
    screen[i] = malloc(sizeof(char) * 40); // 40 pixels per row
    // Set a default value
    for (int j = 0; j < 40; j++) {
      screen[i][j] = '.';
    }
  }

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

  // Free part 2 memory (and print the output simultaneously)
  printf("Part 2:\n");
  for (int i = 0; i < rows; i++) {
    printf("%s\n", screen[i]);
    free(screen[i]);
  }
  free(screen);

  // Free part 1 memory
  free(cycle);
  free(x);
  free(signal_sum);

  fclose(f);
}