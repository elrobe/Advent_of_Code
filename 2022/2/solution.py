#!/usr/bin/env python3

# Advent of Code 2022 - Day 2
# Rock, Paper, Scissors
#
# Given a list of inputs:
# [A|B|C] [X|Y|Z]
# Figure out what your final score is given:
# (For part 1)
# A and X = Rock
# B and Y = Paper
# C and Z = Scissors
# (For part 2)
# X = you need to lose
# Y = you need to draw
# Z = you need to win
#
# 1 point if you use rock
# 2 points if you use paper
# 3 points if you use scissors
#
# 6 points for a win
# 3 points for a draw

def readInput():
    input = open("input.txt", "r")
    commands = input.read().split("\n")
    return commands

# Solution code for part 1
def rockPaperScissors_part1(them, me):
    if them == "A": # Rock
        if me == "X": return 1 + 3 # Draw
        if me == "Y": return 2 + 6 # Win
        if me == "Z": return 3 + 0 # Loss
    if them == "B": # Paper
        if me == "X": return 1 + 0 # Loss
        if me == "Y": return 2 + 3 # Draw
        if me == "Z": return 3 + 6 # Win
    if them == "C": # Scissors
        if me == "X": return 1 + 6 # Win
        if me == "Y": return 2 + 0 # Loss
        if me == "Z": return 3 + 3 # Draw
    # Unexpected edge case:
    return 0

# Solution code for part 2
def rockPaperScissors_part2(them, me):
    if them == "A": # Rock
        if me == "X": return 3 + 0 # Lose (Scissors)
        if me == "Y": return 1 + 3 # Draw (Rock)
        if me == "Z": return 2 + 6 # Win  (Paper)
    if them == "B": # Paper
        if me == "X": return 1 + 0 # Lose (Rock)
        if me == "Y": return 2 + 3 # Draw (Paper)
        if me == "Z": return 3 + 6 # Win  (Scissors)
    if them == "C": # Scissors
        if me == "X": return 2 + 0 # Lose (Paper)
        if me == "Y": return 3 + 3 # Draw (Scissors)
        if me == "Z": return 1 + 6 # Win  (Rock)
    # Unexpected edge case:
    return 0

def processMoves(ary):
    score = 0
    for x in range(0, len(ary)):
        moves = ary[x].split(" ")
        them = moves[0]
        me = moves[1]
        points = rockPaperScissors_part2(them, me)
        score += points
    return score

ary = readInput()
output = processMoves(ary)
print(output)