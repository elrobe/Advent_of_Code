// Advent of Code 2022 - Day 12
// Hill Climbing Algorithm
//
// Trying to reunite with the elves, you can't seem to get a clear enough
// singal from your current location. You see t higher location you'd like
// to get to with elevation represented by 'z'. The lowest elevation, which
// includes your current location, is represented by 'a'. In order to preserve
// your energy, you can only go up in elevation one letter, yet you can go
// from any higher letter to any letter below it.
//
// Part 1 -
// With the above constraints, what is the fewest number of climbs you have to
// make to get from S to E in the input (where S == 'a' and E == 'z')?

// To run:
// - Make sure you have Go installed on your machine
// - Use the command: 'go run solution.go'

package main

import (
	"fmt"
	"math"
	"os"
)

// A struct representing a mountain node in our graph.
type Node struct {
	row     int
	col     int
	adj     []*Node
	value   byte
	visited bool
	isStart bool
	isEnd   bool
	dist    int // For Dijkstra's algorithm
}

// Helper function to create a node from a given 'char' value.
func createNode(char byte) *Node {
	newNode := new(Node)
	newNode.value = char
	newNode.dist = math.MaxInt // "Infinite" distance
	if char == 'S' {
		newNode.value = 'a'
		newNode.isStart = true
	}
	if char == 'E' {
		newNode.value = 'z'
		newNode.isEnd = true
	}
	return newNode
}

// Helper to set the sibling of the current node if the
// sibling is either lower or only one elevation higher
func checkSetSibling(node *Node, sibling *Node) {
	nodeVal := node.value
	sibVal := sibling.value

	if nodeVal+1 >= sibVal {
		node.adj = append(node.adj, sibling)
	}
}

// If we wanted to be more performant, we would change
// the Q to be a Min Heap. But this should do for now.
func findSmalledDistIdx(Q []*Node) int {
	smallest := 0 // Start at index 0
	for i := 1; i < len(Q); i++ {
		if Q[i].dist < Q[smallest].dist {
			smallest = i
		}
	}
	return smallest
}

// Dijkstra's distance algorithm wrapped in a handy little function
// to set the distances each node is from the start node.
func Dijkstra(graph [][]*Node) {
	var Q []*Node
	for r := 0; r < len(graph); r++ {
		for c := 0; c < len(graph[r]); c++ {
			Q = append(Q, graph[r][c])
			// Mark the start
			if graph[r][c].isStart {
				graph[r][c].dist = 0
			}
		}
	}

	var curNode *Node
	var nextNode *Node
	for len(Q) > 0 {
		smallest := findSmalledDistIdx(Q)
		curNode = Q[smallest]
		curNode.visited = true

		// Pop it off the queue by copying the last entry into the entry to remove
		// and then chopping off the end of the queue
		Q[smallest] = Q[len(Q)-1]
		Q[len(Q)-1] = nil
		Q = Q[:len(Q)-1] // Truncate slice

		for i := 0; i < len(curNode.adj); i++ {
			nextNode = curNode.adj[i]
			if nextNode != nil && !nextNode.visited {
				// Calculate the distance
				curDist := curNode.dist + 1
				if curDist < nextNode.dist {
					nextNode.dist = curDist
				}
			}
		}
	}
}

func main() {
	// File reading
	data, error := os.ReadFile("input.txt")
	if error != nil {
		fmt.Println(error.Error())
		return
	}

	var graph [][]*Node
	var endNode *Node

	// File parsing - generate the graph of all the nodes/vertices
	row := 0
	col := 0
	var curRow []*Node
	for i := 0; i < len(data); i++ {
		if data[i] == '\n' {
			row++
			col = 0
			graph = append(graph, curRow)
			curRow = nil // This clears the slice
		} else {
			curNode := createNode(data[i])
			curNode.row = row
			curNode.col = col
			curRow = append(curRow, curNode)
			if curNode.isEnd {
				endNode = curNode
			}
			col++
		}
	}
	graph = append(graph, curRow) // Last row

	// Set all of the siblings after we parse out the entire graph.
	// Yeah, this is another O(N) loop where N is the total number of
	// vertices, but this way we have the entire graph available.
	for r := 0; r < len(graph); r++ {
		for c := 0; c < len(graph[r]); c++ {
			curNode := graph[r][c]
			if r != 0 {
				checkSetSibling(curNode, graph[r-1][c])
			}
			if r+1 < len(graph) {
				checkSetSibling(curNode, graph[r+1][c])
			}
			if c != 0 {
				checkSetSibling(curNode, graph[r][c-1])
			}
			if c+1 < len(graph[r]) {
				checkSetSibling(curNode, graph[r][c+1])
			}
		}
	}

	// Run Dijkstra's Algorithm
	Dijkstra(graph)

	fmt.Printf("Part 1: %d\n", endNode.dist)
}
