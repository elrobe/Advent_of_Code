#!/usr/bin/env python3

# Advent of Code 2022 - Day 8
# Treetop Tree House
#
# The elves planted a bunch of trees, which are represented
# by a 2D array of tree heights.
# - 0 indicates no tree/shortest possible height
# - 9 indicates the tallest possible height
# All trees on the edge of the map are visible since they're
# on the edge. A tree inside from the edge is only visible if
# it is taller than all the trees between it and any edge.
#
# Part 1 -
# Find the number of visible trees.

# To run:
# - Make sure you have python3 installed
# - From the command line, type 'python3 solution.py'

# Reads the input file and parses it into a 2D array of trees
def readInput():
    input = open("input.txt", "r")
    treeLines = input.read().split("\n")
    trees = []
    for treeLine in treeLines:
      treeRow = []
      for tree in treeLine:
        treeRow.append(int(tree))
      trees.append(treeRow)
    return trees

# Checks north, south, east, and west from the given tree to determine
# if it's visible from the edge of the forest. Note that edge trees
# are always visible.
def checkTree(trees, row, col):
  # Check for an edge tree
  if row == 0: return True
  if row == len(trees) - 1: return True
  if col == 0: return True
  if col == len(trees[row]) - 1: return True

  height = trees[row][col]
  visible = True # Assume that it's visible until we find a taller tree
  # Check the trees to the East
  for x in range(col + 1, len(trees[row])):
    visible = visible and (height > trees[row][x])
  if visible: return True
  else: visible = True # Reset
  # Check the trees to the West
  for x in range(0, col):
    visible = visible and (height > trees[row][x])
  if visible: return True
  else: visible = True # Reset
  # Check the trees to the North
  for x in range(0, row):
    visible = visible and (height > trees[x][col])
  if visible: return True
  else: visible = True # Reset
  # Check the trees to the South
  for x in range(row + 1, len(trees)):
    visible = visible and (height > trees[x][col])
  # Just return visible at this point from the last truthy check
  return visible

# Iterate over each row and column and count the trees
# visible from the edges of the array.
def countVisible(trees):
  count = 0
  for row in range(0, len(trees)):
    treeRow = trees[row]
    for col in range(0, len(treeRow)):
      if checkTree(trees, row, col):
        count = count + 1
  return count

trees = readInput()
count = countVisible(trees)
print("Part 1: " + str(count))