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
//
// Part 2 -
// Now we won't divide the worry level by 3 anymore, but need
// to find a different way to manage the increasing worry level
// after each inspection.
// What is the level of monkey business after 10,000 rounds of
// inspections in this case?

// To run:
// - 'java solution.java'

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class solution {
  // Represents the "tuple" of the tossed item value and the monkey it gets thrown to
  public class TossedItem {
    public long WorryValue;
    public int MonkeyNumber;
    public TossedItem(long worryValue, int monkeyNumber) {
      this.WorryValue = worryValue;
      this.MonkeyNumber = monkeyNumber;
    }
  }

  // A class to parse the monkey input lines with a function to process
  // the items the monkey has.
  public class Monkey implements Comparable {
    public static boolean PART_ONE = true; // We'll set this to false later

    private int InspectionsDone;
    private LinkedList<Long> Items;
    private char Operation;
    private String OperationValue;
    private int TestValue;
    private int TMonkey;
    private int FMonkey;
  
    public Monkey(String[] monkeyInfo) {
      this.InspectionsDone = 0;
      this.Items = new LinkedList<Long>();
  
      // We don't care to parse the first row since it's just the monkey number.
      String itemsLine = monkeyInfo[1];
      // Strip off "Starting items: "
      String[] items = itemsLine.substring(itemsLine.indexOf(':') + 2).split(", ");
      for (int i = 0; i < items.length; i++) {
        this.Items.add(Long.parseLong(items[i]));
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
    public void AddItem(long worryValue) {
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
      long worryValue = 0, opValue;
      // Take the current item in the front of the items
      worryValue = this.Items.removeFirst(); // Feel free to crash here, it would mean the upstream code is bad

      // Operation value is either "old" or an actual number
      if (this.OperationValue.equals("old")) {
        opValue = worryValue;
      } else {
        opValue = Long.parseLong(this.OperationValue);
      }

      // Then apply the operation
      if (this.Operation == '*') {
        worryValue *= opValue;
      } else { // We're making an assumption that the only other possible Operation is '+'
        worryValue += opValue;
      }

      // Part 1 only: always divide by 3 (post-inspection worry-decrease)
      if (PART_ONE) {
        worryValue = worryValue / 3; // Making sure we do int-division
      }

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

    // For debugging
    @Override
    public String toString() {
      String items = "";
      for (Long i : this.Items) {
        items += "[" + i + "]";
      }
      return "Monkey is holding: " + items;
    }
  }

  /**
   * Does the operations for part 1 or part 2.
   * Reusable since they're mostly the same.
   * @param monkeys Part's list of monkeys, since the references should be different.
   * @param partOne Whether or not we're processing part 1 (true) or part 2 (false).
   */
  public static void DoPart(ArrayList<Monkey> monkeys, boolean partOne) {
    Monkey.PART_ONE = partOne; // There's a more elegant way to do this, but oh well
    int rounds = partOne ? 20 : 10000;

    int modulo = 1;
    if (!partOne) {
      for (Monkey m : monkeys) {
        modulo = modulo * m.TestValue;
      }
    }

    for (int i = 0; i < rounds; i++) {
      for (int m = 0; m < monkeys.size(); m++) {
        TossedItem[] tossed = monkeys.get(m).InspectItems();
        
        for (int t = 0; t < tossed.length; t++) {
          long worryValue = tossed[t].WorryValue;
          if (!partOne) {
            worryValue = worryValue % modulo;
          }

          monkeys.get(tossed[t].MonkeyNumber).AddItem(worryValue);
        }
      }
    }
    monkeys.sort(null);
    
    // Now output the monkey business!
    System.out.println("Part " + (partOne ? "1" : "2") + ": " + 
      ((long)monkeys.get(0).InspectionsDone() * 
      (long)monkeys.get(1).InspectionsDone()));
  }

  // Our main method to run the code
  public static void main(String[] args) {
    solution solution = new solution(); // We need this to invoke the inner classes
    ArrayList<Monkey> monkeys1 = new ArrayList<Monkey>();
    ArrayList<Monkey> monkeys2 = new ArrayList<Monkey>();
    
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
            monkeys1.add(solution.new Monkey(monkeyInfo));
            monkeys2.add(solution.new Monkey(monkeyInfo));
            lineNum = 0;
            monkeyInfo = new String[6];
            continue; // Skip to next iteration
          }
          monkeyInfo[lineNum] = line;
          lineNum++;
        }
        // Don't forget the last monkey!
        monkeys1.add(solution.new Monkey(monkeyInfo));
        monkeys2.add(solution.new Monkey(monkeyInfo));
    } catch (Exception e) {
        System.err.println(e);
    } finally {
        // Don't forget to close the file reader
        if (s != null) s.close();
    }

    // Process the monkey business
    DoPart(monkeys1, true);
    DoPart(monkeys2, false);
  }
}