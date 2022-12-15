// Advent of Code 2022 - Day 11
// Monkey in the Middle
//
// Monkeys have been stealing stuff out of your bag.
// They take turns inspecting items in the order they appear
// in the input before tossing the items to another monkey.
//
// Part 1 -
// Count the number of times each monkey inspects your items,
// but focus on the two monkeys that have inspected the most
// items. Multiplying the number of items they've inspected
// gives a level of monkey business.
// What is the level of monkey business after 20 rounds of
// inspections?

// To run:
// - 'java solution.java'

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class solution {
  // Handy wrapper error
  public class InvalidMonkeyBusiness extends Error {
    public InvalidMonkeyBusiness(String errorInfo) {
      super("Invalid monkey business input:" + errorInfo);
    }
  }

  // Represents the "tuple" of the tossed item value and the monkey it gets thrown to
  public class TossedItem {
    public int WorryValue;
    public int MonkeyNumber;
    public TossedItem(int worryValue, int monkeyNumber) {
      this.WorryValue = worryValue;
      this.MonkeyNumber = monkeyNumber;
    }
  }

  // A class to parse the monkey input lines with a function to process
  // the items the monkey has.
  public class Monkey implements Comparable {
    private int InspectionsDone;
    private LinkedList<Integer> Items;
    private char Operation;
    private String OperationValue;
    private int TestValue;
    private int TMonkey;
    private int FMonkey;
  
    public Monkey(String[] monkeyInfo) {
      this.InspectionsDone = 0;
      this.Items = new LinkedList<Integer>();
  
      // We don't care to parse the first row since it's just the monkey number.
      String itemsLine = monkeyInfo[1];
      // Strip off "Starting items: "
      String[] items = itemsLine.substring(itemsLine.indexOf(':') + 2).split(", ");
      for (int i = 0; i < items.length; i++) {
        this.Items.add(Integer.parseInt(items[i]));
      }

      // Now strip off "Operation: new = old"
      String operationLine = monkeyInfo[2];
      int opIdx = operationLine.indexOf('*');
      opIdx = opIdx > -1 ? opIdx : operationLine.indexOf('+');
      this.Operation = operationLine.charAt(opIdx);
      this.OperationValue = operationLine.substring(opIdx + 2); // Strip the space

      // Get the number at the end of the Test line, True line, and False line
      this.TestValue = ParseLastNumber(monkeyInfo[3]);
      this.TMonkey = ParseLastNumber(monkeyInfo[4]);
      this.FMonkey = ParseLastNumber(monkeyInfo[5]);
    }

    /**
     * Given a line where we expect the last thing after the last space to be an
     * integer, parse said integer.
     * @return The number at the end of the line.
     */
    private int ParseLastNumber(String line) {
      return Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1));
    }

    /**
     * Add an item to the end of this monkey's list of items.
     * @param worryValue Newly added item's worry value
     */
    public void AddItem(int worryValue) {
      this.Items.addLast(worryValue);
    }
  
    /**
     * Public interface to process all of the items the monkey currently has.
     * @return Array of tossed items to pass to other monkeys.
     */
    public TossedItem[] InspectItems() {
      int numTossed = this.Items.size();
      TossedItem[] tossed = new TossedItem[numTossed];
      for (int i = 0; i < numTossed; i++) {
        tossed[i] = DoInspection();
      }
      this.InspectionsDone += numTossed;
      return tossed;
    }

    /**
     * Processes one single item in the Items list, removing it from
     * the array and returning the monkey it should be thrown to.
     * @return the Monkey # that the item will be tossed to
     */
    private TossedItem DoInspection() {
      int worryValue = 0, opValue;
      // Take the current item in the front of the items
      try {
        worryValue = this.Items.removeFirst();
      } catch (NoSuchElementException e) {
        throw new InvalidMonkeyBusiness("Tried to remove from empty list."); // Unexpected edge-case
      }

      // Operation value is either "old" or an actual number
      if (this.OperationValue.equals("old")) {
        opValue = worryValue;
      } else {
        opValue = Integer.parseInt(this.OperationValue);
      }

      // Then apply the operation
      if (this.Operation == '*') {
        worryValue *= opValue;
      } else { // We're making an assumption that the only other possible Operation is '+'
        worryValue += opValue;
      }

      // Always divide by 3 (post-inspection worry-decrease)
      worryValue = worryValue / 3; // Making sure we do int-division

      // Now test it
      if (worryValue % this.TestValue == 0) {
        return new TossedItem(worryValue, this.TMonkey);
      } else {
        return new TossedItem(worryValue, this.FMonkey);
      }
    }

    /**
     * Wrapper around the internal count of inspections done by this monkey.
     * @return Total number of inspections the monkey has done.
     */
    public int InspectionsDone() {
      return this.InspectionsDone;
    }

    // For sorting - we want to sort with the highest first, so we're
    // actually swapping the 1 and -1 values
    @Override
    public int compareTo(Object o)
    {
        Monkey m = (Monkey)o;
        if (this.InspectionsDone() > m.InspectionsDone()) {
            return -1;
        }
        if (this.InspectionsDone() < m.InspectionsDone()) {
            return 1;
        }
        // Otherwise equal
        return 0;
    }
  }

  // Our main method to run the code
  public static void main(String[] args) {
    solution solution = new solution(); // We need this to invoke the inner classes
    ArrayList<Monkey> monkeys = new ArrayList<Monkey>();
    
    File f = new File("input.txt");
    Scanner s = null;
    try {
        s = new Scanner(f);

        // Each monkey is 6 lines:
        // 1) Monkey #
        // 2) Starting items
        // 3) Operation
        // 4) Test condition
        // 5) If "Test" is true
        // 6) If "Test" is false
        String[] monkeyInfo = new String[6];
        int lineNum = 0;
        while (s.hasNextLine()) {
          String line = s.nextLine();
          if (lineNum == 6) { // Could also check for "\n"
            monkeys.add(solution.new Monkey(monkeyInfo));
            lineNum = 0;
            monkeyInfo = new String[6];
            continue; // Skip to next iteration
          }
          monkeyInfo[lineNum] = line;
          lineNum++;
        }
        // Don't forget the last monkey!
        monkeys.add(solution.new Monkey(monkeyInfo));
    } catch (Exception e) {
        System.err.println(e);
    } finally {
        // Don't forget to close the file reader
        if (s != null) s.close();
    }

    // Part 1
    for (int i = 0; i < 20; i++) {
      for (int m = 0; m < monkeys.size(); m++) {
        TossedItem[] tossed = monkeys.get(m).InspectItems();
        for (int t = 0; t < tossed.length; t++) {
          monkeys.get(tossed[t].MonkeyNumber).AddItem(tossed[t].WorryValue);
        }
      }
    }
    monkeys.sort(null);
    // Now output the monkey business!
    System.out.println("Part 1: " + (monkeys.get(0).InspectionsDone() * monkeys.get(1).InspectionsDone()));
  }
}