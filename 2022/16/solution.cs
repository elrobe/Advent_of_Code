// Advent of Code 2022 - Day 16
// Proboscidea Volcanium
//
// You come across the device emitting the distress signal. Turns out
// it's surrounded by elephants, not elves. Weird. Anyway, you scan the
// caves and find out you're actually in a volcano and the tunnels are lines
// with pipes connecting valves.
//
// Part 1 -
// You have 30 minutes to get out of the volcano, but want to open as many valves
// as possible. It takes one minute to open a valve and one minute to traverse to
// the next valve. Given the input of valves, their flow rate, and each adjacent valve,
// what is the max flow you can release in 30 minutes?

// To run:
// I used .NET 7.0 x64 for macOS and Mono (https://www.mono-project.com/)
// - Use the command 'csc solution.cs' to compile
// - Then use 'mono solution.exe' to run

using System;
using System.Collections.Generic;

// I.e. the Graph for the problem itself
public class Cave {
  public Dictionary<string, Valve> Valves { get; private set; }
  public List<string> ValueValves { get; private set; }

  public Cave(string[] lines) {
    Valves = new Dictionary<string, Valve>();
    ValueValves = new List<string>();

    // Construct the cave from all the lines
    foreach (string line in lines) {
      int nameIdx = "Valve ".Length;
      string name = line.Substring(nameIdx, 2);
      string valveInfo = line.Split(";")[0];
      int flow = int.Parse(valveInfo.Substring(line.IndexOf("rate=") + "rate=".Length));
      // In case there's a valve that has a tunnel to only one other valve
      string tunnels;
      if (line.IndexOf("valves ") == -1) {
        tunnels = line.Substring(line.IndexOf("valve ") + "valve ".Length);
      } else {
        tunnels = line.Substring(line.IndexOf("valves ") + "valves ".Length);
      }
      Valves.Add(name, new Valve(name, flow, tunnels.Split(", ")));

      // We'll use this list later to construct a proper graph of how to
      // get between each value valve.
      if (flow > 0) {
        ValueValves.Add(name);
      }
    }
  }

  /**
   * For debugging purposes when parsing the input.
   */
  public override string ToString()
  {
    string s = "";
    foreach (Valve v in Valves.Values) {
      s += v.Name + ":" + v.FlowRate + " -> " + String.Join(", ", v.Tunnels) + "\n";
    }
    return s;
  }
}

// Wrapper around a valve (name) and the time to get to it.
public class NextValve {
  public string Name;
  public int Weight;
  public NextValve(string name, int weight) {
    this.Name = name;
    this.Weight = weight;
  }
}

// I.e. a Vertex in the graph
public class Valve {
  // The total flow is across all valves
  public static int TotalFlow = 0;

  public string Name { get; private set; }
  public bool IsFlowing { get; private set; } // I.e. "Visited?"
  public int FlowRate { get; private set; }

  // Note that this is only used for simplication of the cave into a graph
  public string[] Tunnels { get; private set; } // Reference to the Rooms by ID

  public Dictionary<string, NextValve> OtherValves; // References to the next valves of value

  public Valve(string name, int flow, string[] tunnels) {
    this.Name = name;
    this.IsFlowing = false;
    this.FlowRate = flow;
    this.Tunnels = tunnels;
    // We'll populate this later
    this.OtherValves = new Dictionary<string, NextValve>();
  }

  // Helper function to open a valve and increment the total flow.
  public void OpenValve() {
    if (this.IsFlowing) { // Sanity check
      return;
    }
    this.IsFlowing = true;
    Valve.TotalFlow += this.FlowRate;
  }

  // Helper function to close a valve and decrement the total flow.
  public void CloseValve() {
    if (!this.IsFlowing) { // Sanity check
      return;
    }
    this.IsFlowing = false;
    Valve.TotalFlow -= this.FlowRate;
  }

  // For debugging
  public string ValvesString() {
    List<string> valves = new List<string>();
    foreach (NextValve valve in this.OtherValves.Values) {
      valves.Add(valve.Name + ":" + valve.Weight);
    }
    return this.Name + " -> " + String.Join(", ", valves.ToArray());
  }
}

public class Solution {
  private static int MaxFlow = 0; // This big boy is our answer to Part 1

  // Modularized for ease of reading Part 1 code.
  // This is a BFS way of finding all valves.
  private static void SetShortestDistanceToOtherValueValves(Valve v, Cave c) {
    Dictionary<string, bool> visited = new Dictionary<string, bool>();
    Queue<NextValve> q = new Queue<NextValve>();
    visited.Add(v.Name, true);

    foreach (string tunnel in v.Tunnels) {
      Valve nextValve = null;
      c.Valves.TryGetValue(tunnel, out nextValve);
      // We should normally check if nextValve=null but that would indicate
      // something seriously wrong with the preceding code
      NextValve nv = new NextValve(nextValve.Name, 1);
      q.Enqueue(nv);
      visited.Add(nv.Name, true);
    }

    // Engage the BFS!
    while (q.Count > 0) {
      NextValve cur = q.Dequeue();
      Valve curValve = null;
      c.Valves.TryGetValue(cur.Name, out curValve);

      // Only add value valves to the valve we're evaluating
      if (curValve.FlowRate > 0) {
        v.OtherValves.Add(cur.Name, cur);
      }

      foreach (string tunnel in curValve.Tunnels) {
        if (!visited.ContainsKey(tunnel)) { // Don't visit the same valve more than once
          Valve nextValve = null;
          c.Valves.TryGetValue(tunnel, out nextValve);
          NextValve nv = new NextValve(nextValve.Name, cur.Weight + 1);
          q.Enqueue(nv);
          visited.Add(nv.Name, true);
        }
      }
    }
  }

  // Our recursive function that will update the flow and attempt to move to the next valve!
  private static void TryFlowSequence(int minutesLeft, int time, Cave c, string curValve, int curFlow) {
    int newFlow, newMinutesLeft;

    // Base case
    if (minutesLeft - time <= 0) {
      newFlow = curFlow + (Valve.TotalFlow * minutesLeft);
      if (newFlow > Solution.MaxFlow) {
        Solution.MaxFlow = newFlow;
      }
      return;
    }
    
    // Otherwise, increase the total flow by the amount of time it took to get here
    newMinutesLeft = minutesLeft - time;
    newFlow = curFlow + (Valve.TotalFlow * time);

    Valve v = null;
    c.Valves.TryGetValue(curValve, out v);

    // Open the valve, which costs 1 minute
    newFlow = newFlow + Valve.TotalFlow; // But we add the existing flow while we open the valve
    v.OpenValve();
    newMinutesLeft = newMinutesLeft - 1;

    // Now try to open the next valve
    foreach (NextValve nv in v.OtherValves.Values) {
      Valve next = null;
      c.Valves.TryGetValue(nv.Name, out next);
      // Don't be going to any valves that are flowing
      if (!next.IsFlowing) {
        TryFlowSequence(newMinutesLeft, nv.Weight, c, nv.Name, newFlow);
      }
    }
    // At this point, we may have just hit a point where there are no valves left to open,
    // so also check what happens if we sit and wait
    newFlow = newFlow + (Valve.TotalFlow * newMinutesLeft);
    if (newFlow > Solution.MaxFlow) {
      Solution.MaxFlow = newFlow;
    }

    v.CloseValve(); // Remember to close upon returning up the stack
  }

  // My strategy for part 1 is as follows:
  // - Simplify the cave into a graph of valves
  // - The time between each valve is the number of empty-flow rooms between them.
  // - Try every combination in a recursive function to find the max flow in 30 min
  public static void Part1(Cave cave) {
    // Construct the graph! We want each valve that has a flow value to contain
    // a reference to all the other valves and the minute-cost (weight) to get there.
    Valve v;
    cave.ValueValves.Add("AA"); // Add the starting point (always has a rate of 0)
    foreach (string vName in cave.ValueValves) {
      v = null;
      cave.Valves.TryGetValue(vName, out v);
      SetShortestDistanceToOtherValueValves(v, cave);
      //System.Console.WriteLine(v.ValvesString());
    }
    cave.ValueValves.RemoveAt(cave.ValueValves.Count - 1); // Remove "AA" in O(1)

    // Now try and get the max flow in 30 minutes!
    v = null;
    cave.Valves.TryGetValue("AA", out v);
    // Starting at AA, recurse through the list to find the max flow.
    foreach (NextValve nv in v.OtherValves.Values) {
      TryFlowSequence(30, nv.Weight, cave, nv.Name, 0);
    }

    // Output it already!
    System.Console.WriteLine("Part 1: " + Solution.MaxFlow);
  }

  public static void Main(string[] args) {
        string[] lines = System.IO.File.ReadAllLines("input.txt");
        Cave cave = new Cave(lines);
        //System.Console.WriteLine(cave.ToString());

        Part1(cave);
    }
}