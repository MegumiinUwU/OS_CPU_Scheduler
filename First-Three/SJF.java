import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class SJF implements Scheduler {

   private ArrayList<Process> processes;
   private ArrayList<Process> stat;
   private int time;

   public SJF() throws Exception {
      time = 0;
      this.processes = new ArrayList<>();
      this.stat = new ArrayList<>();
      BufferedReader br = new BufferedReader(new FileReader("./input.txt"));

      String line = new String();
      while ((line = br.readLine()) != null) {
         String[] data = line.split(" ");

         String name = data[0];
         int arrivalTime = Integer.parseInt(data[1]);
         int burstTime = Integer.parseInt(data[2]);

         processes.add(new Process(name, arrivalTime, burstTime));
      }

      br.close();
   }

   @Override
   public void run() throws Exception {
      Process currentProcess = new Process(null, 0, 0);
      PriorityQueue<Process> waitingProcesses = new PriorityQueue<>(
            (p1, p2) -> Integer.compare(p1.getCustomPriority(), p2.getCustomPriority()));
      PriorityQueue<Process> tmp = new PriorityQueue<>(
            (p1, p2) -> Integer.compare(p1.getCustomPriority(), p2.getCustomPriority()));

      Collections.sort(processes, (p1, p2) -> Integer.compare(p1.getArrivalTime(), p2.getArrivalTime()));

      int i = 0;
      while (i < processes.size() || currentProcess.getName() != null || !waitingProcesses.isEmpty()) {
         while (!waitingProcesses.isEmpty()) {
            Process tmpProcess = waitingProcesses.poll();
            tmpProcess.setAge(tmpProcess.getAge() + 1);
            tmp.add(tmpProcess);
         }

         waitingProcesses = new PriorityQueue<>(tmp);
         tmp = new PriorityQueue<>(
               (p1, p2) -> Integer.compare(p1.getCustomPriority(), p2.getCustomPriority()));

         while (i < processes.size() && processes.get(i).getArrivalTime() <= this.time) {
            System.out.println("process " + processes.get(i).getName() + " has arrived at " + time);
            waitingProcesses.add(new Process(processes.get(i)));
            i++;
         }

         if (currentProcess.getName() != null) {
            currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);
         }

         if (currentProcess.getName() != null && currentProcess.getBurstTime() == 0) {
            System.out.println("Process " + currentProcess.getName() + " has ended at " + time);
            currentProcess.setTurnAroundTime(this.time - currentProcess.getArrivalTime());
            currentProcess.setBurstTime(this.time - currentProcess.getArrivalTime() - currentProcess.getWaitingTime());
            stat.add(new Process(currentProcess));
            currentProcess = new Process(null, 0, 0);
         }

         if (currentProcess.getName() == null && !waitingProcesses.isEmpty()) {
            time++;
            currentProcess = waitingProcesses.poll();
            currentProcess.setAge(currentProcess.getAge() + 1);
            System.out.println("Process " + currentProcess.getName() + " has started " + time);
            currentProcess.setWaitingTime(this.time - currentProcess.getArrivalTime());
            currentProcess.setBurstTime(currentProcess.getBurstTime() + 1);
            continue;
         }
         time++;
      }
   }

   @Override
   public void plot() throws Exception {
      Collections.sort(stat, (p1, p2) -> p1.getName().compareTo(p2.getName()));
      BufferedWriter bw = new BufferedWriter(new FileWriter("./shortestJobFirst.txt"));
      for (Process p : stat) {
         bw.write("Process Name: " + p.getName() + '\n');
         bw.write("Process Arrival: " + p.getArrivalTime() + '\n');
         bw.write("Process Burst Time: " + p.getBurstTime() + '\n');
         bw.write("Process Age: " + p.getAge() + '\n');
         bw.write("Process Waiting Time: " + p.getWaitingTime() + '\n');
         bw.write("Process TurnAround Time: " + p.getTurnAroundTime() + "\n\n");
      }
      bw.close();
   }
}
