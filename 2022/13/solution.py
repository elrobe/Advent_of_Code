#!/usr/bin/env python3

# Advent of Code 2022 - Day 13
# Distress Signal
#
# Now that you're up high, you receive a distress signal from the elves.
# The input is a series of pairs of messages where each inidividual message
# is an array composed of integers or nested arrays (also of integers).
# The first message in the pair is considered the left, the second the right.
#
# Part 1 -
# Using the following rules, what is the sum of all the indices of messages
# in the set where the messages are "out of order"?
# - If both are integers and the left integer is lower, the messages 
#   are in order. If they are equal then move onto the next piece of the input.
# - If both are arrays, then the left must have less indices or have
#   each values at each index be less than the equivocal index of the right
#   to be in order.
# - If one is an integer and the other an array, convert the integer to
#   an array and proceed to evaluate it as an array.
#
# Part 2 -
# Now we want to sort all of the messages, adding in two divider packets:
# [[2]] and [[6]]
# After sorting all of the packets using the rules from part 1, what is
# the product of the indices of the two sorted divider packets?

from enum import Enum

INPUT_FILE = "input.txt"

# Convert the input into a pair of tuples
def readInput():
    input = open(INPUT_FILE, "r")
    lines = input.read().split("\n")

    i = 0
    pairs = []
    while i < len(lines):
      pairs.append((eval(lines[i]), eval(lines[i+1])))
      i = i + 3
    
    return pairs

class Evaluation(Enum):
  InOrder = 1
  OutOfOrder = 2
  Continue = 3

# Recusrively called function to compare right and left values according to the
# rules in the description of the day's problemn.
# Only gets called recursively when a list is nested.
def compareLists(left, right):
  evaluation = Evaluation.Continue # Assume in-order

  for i in range(0, len(left)):
    if i >= len(right): # Right ran out of values -> OOO
      return Evaluation.OutOfOrder
    
    lVal = left[i]
    rVal = right[i]

    # Same type -> no conversion
    if type(lVal) is type(rVal):
      if type(lVal) is list:
        evaluation = compareLists(lVal, rVal)
      else: # Integer comparison
        if lVal < rVal:
          return Evaluation.InOrder
        if lVal > rVal:
          return Evaluation.OutOfOrder
        # Else, they must be equal, so drive on
    else:
      if type(lVal) is not list:
        lVal = [lVal]
      if type(rVal) is not list:
        rVal = [rVal]
      evaluation = compareLists(lVal, rVal)
    
    # If we're not meant to drive on, then return the discrete value
    if evaluation is not Evaluation.Continue:
      return evaluation

  # Include the case where the left ran out of items first
  if len(left) < len(right):
    return Evaluation.InOrder

  # If we get this far, we ran out of stuff to evaluate, so drive on
  return evaluation
  
# Compare two pairs.
# Return true if they're in order, false otherwise.
# Yes, I chuckled at the name of the function I chose.
def comPAIR(pair):
  left = pair[0]
  right = pair[1]
  return compareLists(left, right)

# -----------------------------
# Main running code starts here
pairs = readInput()

sum = 0
for i in range(0, len(pairs)):
  # If we end on Continue, then it means we got to the end and nothing
  # was out of order, which means it's in-order
  if comPAIR(pairs[i]) != Evaluation.OutOfOrder:
    sum = sum + (i + 1)

print("Part 1: " + str(sum))

# Part 2: add the divider packets [[2]] and [[6]], sort,
# then locate [[2]] and [[6]] and multiple their indices
allPackets = [[[2]], [[6]]]
for pair in pairs:
  allPackets.append(pair[0])
  allPackets.append(pair[1])

# Bubble sort it, but hear me out:
# The sorting algo is O(n^2) time, but we have 152 elements looking at the
# input, thus 23,104 operations, which for a computer, is relatively trivial.
# Granted, the comparison function is recursive, but we'll ignore that.
for i in range(len(allPackets)):
  for j in range(0, len(allPackets) - i - 1):
    a = allPackets[j]
    b = allPackets[j + 1]

    evaluation = compareLists(a, b)
    if evaluation == Evaluation.OutOfOrder:
      allPackets[j + 1] = a
      allPackets[j] = b

# Because I'm lazy, we'll look for the divider packets iteratively
packet2 = -1
packet6 = -1
for i in range(0, len(allPackets) - 1):
  # Only look for [[2]] until we find it...
  if packet2 < 0 and compareLists([[2]], allPackets[i]) == Evaluation.Continue:
    packet2 = i + 1
  # Only look for [[6]] after we find [[2]]
  if packet2 >= 0 and packet6 < 0 and compareLists([[6]], allPackets[i]) == Evaluation.Continue:
    packet6 = i + 1
  # Don't bother going any further after we find both, duh
  if packet2 >= 0 and packet6 >= 0:
    break

print("Part 2: " + str(packet2 * packet6))