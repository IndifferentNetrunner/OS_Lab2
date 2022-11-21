// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

import java.io.*;
import java.util.*;

public class Scheduling {
  private static int processnum = 5;
  private static int meanDev = 1000;
  private static int standardDev = 100;
  private static int runtime = 1000;
  private static final Vector<Process> processVector = new Vector<>();

  public static void main(String[] args) throws IOException {
    errorHandler(args);
    addProcesses(args);

    String filepath = "C:/Users/Asus/IdeaProjects/OS/Lab2/src/main/resources/Summary-Results.txt";
    final PrintStream out = new PrintStream(new FileOutputStream(filepath)) ;

    for (int i = 0; i < processVector.size(); i++) {
      processVector.get(i).id = i;
    }

    ShortestJobFirstNonPreemptive algorithm = new ShortestJobFirstNonPreemptive();
    System.out.println("Working...");

    Vector<Process> processVectorCopy = Common.processesCopy(processVector);
    Results result = algorithm.run(runtime, processVectorCopy);

    out.println("Scheduling Type: " + result.schedulingType);
    out.println("Scheduling Name: " + result.schedulingName);
    out.println("Simulation Run Time: " + result.compTime);
    out.println("Mean: " + meanDev);
    out.println("Standard Deviation: " + standardDev);
    out.println("Process #\tCPU Time\tIO Blocking\tArrival time\tCPU Completed\tCPU Blocked");

    for (var i = 0; i < processVectorCopy.size(); i++) {
      Process process = processVectorCopy.elementAt(i);
      out.print(i);
      if (i < 100) {
        out.print("\t\t\t");
      } else {
        out.print("\t");
      }
      out.print(process.cpuTime);
      if (process.cpuTime < 100) {
        out.print(" (ms)\t\t");
      } else {
        out.print(" (ms)\t");
      }
      out.print(process.withoutBlocking);
      if (process.withoutBlocking < 100) {
        out.print(" (ms)\t\t");
      } else {
        out.print(" (ms)\t");
      }
      out.print(process.arrivalTime);
      if (process.arrivalTime < 100) {
        out.print(" (ms)\t\t\t");
      } else {
        out.print(" (ms)\t\t");
      }
      out.print(process.cpuDone);
      if (process.cpuDone < 100) {
        out.print(" (ms)\t\t");
      } else {
        out.print(" (ms)\t");
      }
      out.println("\t" + process.blockedCount + " times");
    }
    out.close();

    System.out.println("Completed.");
  }

  private static void errorHandler(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: 'java Scheduling <INIT FILE>'");
      System.exit(-1);
    }
    File f = new File(args[0]);
    if (!(f.exists())) {
      System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
      System.exit(-1);
    }
    if (!(f.canRead())) {
      System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
      System.exit(-1);
    }
  }

  private static void addProcesses(String[] args) {
    Init(args[0]);
    if (processVector.size() < processnum) {
      var i = 0;
      while (processVector.size() < processnum) {
        double X = Common.R1();

        while (X == -1.0) {
          X = Common.R1();
        }

        X = X * standardDev;
        int cputime = (int) X + meanDev;
        int arrivalTime = new Random().nextInt(cputime / 4);
        processVector.addElement(new Process(cputime, i*100, arrivalTime));
        i++;
      }
    }
  }

  private static void Init(String file) {
    File f = new File(file);
    String line;
    int cputime = 0;
    int ioblocking = 0;

    try {
      DataInputStream in = new DataInputStream(new FileInputStream(f));
      while ((line = in.readLine()) != null) {
        if (line.startsWith("numprocess")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          processnum = Common.s2i(st.nextToken());
        }
        if (line.startsWith("meandev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          meanDev = Common.s2i(st.nextToken());
        }
        if (line.startsWith("standdev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          standardDev = Common.s2i(st.nextToken());
        }
        if (line.startsWith("process")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          ioblocking = Common.s2i(st.nextToken());
          double X = Common.R1();

          while (X == -1.0) {
            X = Common.R1();
          }

          X = X * standardDev;
          cputime = (int) X + meanDev;
          int arrivalTime = new Random().nextInt(cputime / 4);
          processVector.addElement(new Process(cputime, ioblocking, arrivalTime));

        }
        if (line.startsWith("runtime")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          runtime = Common.s2i(st.nextToken());
        }
      }
      in.close();
    } catch (IOException e) { /* Handle exceptions */ }
  }

  private static void debug() {
    int i = 0;

    System.out.println("processnum " + processnum);
    System.out.println("meandevm " + meanDev);
    System.out.println("standdev " + standardDev);

    int size = processVector.size();
    for (i = 0; i < size; i++) {
      Process process = processVector.elementAt(i);
      System.out.println("process " + i + " " + process.cpuTime + " " + process.withoutBlocking + " " + process.cpuDone + " " + process.blockedCount);
    }
    System.out.println("runtime " + runtime);
  }
}