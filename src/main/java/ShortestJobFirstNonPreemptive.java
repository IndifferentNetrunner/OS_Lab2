// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Comparator;
import java.util.Vector;
import java.io.*;
import java.util.stream.Collectors;

public class ShortestJobFirstNonPreemptive {
  public Results run(int runtime, Vector<Process> processVector) throws FileNotFoundException {
    var comptime = 0;
    var completed = 0;
    var result = new Results(null, null, 0);

    String filepath = "C:/Users/Asus/IdeaProjects/OS/Lab2/src/main/resources/Summary-Processes.txt";
    final PrintStream out = new PrintStream(new FileOutputStream(filepath));

    result.schedulingType = "Non-preemptive";
    result.schedulingName = "Shortest Job First";

    processVector.sort(Comparator.comparingInt(o -> o.withoutBlocking));

    Process currentProcess = null;
    while (comptime < runtime) {
      if (currentProcess != null) {
        if (currentProcess.cpuDone == currentProcess.cpuTime) {
          completed++;
          out.printf("Process: %d \"completed\"... %s\n", currentProcess.id, currentProcess);

          if (completed == processVector.size()) {
            result.compTime = comptime;
            return result;
          }

          var sortedProcesses = processVector.stream()
                  .filter(p -> p.isArrived)
                  .sorted(Comparator.comparingInt(o -> o.withoutBlocking - o.withoutBlockingDone))
                  .collect(Collectors.toCollection(Vector::new));

          var currentId = getNextProcessId(sortedProcesses, -1);

          if (currentId == -1) {
            currentProcess = null;
            continue;
          }

          currentProcess = processVector.stream().filter(p -> p.id == currentId).findFirst().get();
        }
        if (currentProcess.withoutBlocking == currentProcess.withoutBlockingDone) {
          out.printf("Process: %d \"blocked\"..... %s\n", currentProcess.id, currentProcess);
          currentProcess.blockedCount++;
          currentProcess.withoutBlockingDone = 0;

          var sortedProcesses = processVector.stream()
                  .filter(p -> p.isArrived)
                  .sorted(Comparator.comparingInt(o -> o.withoutBlocking - o.withoutBlockingDone))
                  .collect(Collectors.toCollection(Vector::new));

          var currentId = getNextProcessId(sortedProcesses, currentProcess.id);

          currentProcess = processVector.stream().filter(p -> p.id == currentId).findFirst().get();
        }
        currentProcess.cpuDone++;

        if (currentProcess.withoutBlocking > 0) {
          currentProcess.withoutBlockingDone++;
        }
      }
      comptime++;
      for (var process : processVector) {
        if (process.arrivalTime == comptime) {
          process.isArrived = true;
          out.printf("Process: %d \"arrived\"..... %s\n", process.id, process);

          if (currentProcess == null) {
            currentProcess = process;
          }
        }
      }
    }

    result.compTime = comptime;
    out.close();
    return result;
  }

  private int getNextProcessId(Vector<Process> processVector, int previousId) {
    for (var i = 0; i < processVector.size(); i++) {
      Process process = processVector.elementAt(i);
      if (process.cpuDone < process.cpuTime && previousId != process.id) {
        return process.id;
      }
    }
    return previousId;
  }
}
