package org.AdvSW;



import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

class Process {
    String name;
    String color;
    int arrivalTime;
    int burstTime;
    int priority;
    int remainingBurstTime;
    int waitingTime;
    int turnaroundTime;
    int quantum;
    double fcaiFactor;
    boolean isCompleted;
    int startTime ;
    int completionTime;
    private List<Integer> quantumHistory = new ArrayList<>();



    public Process(String name, String color, int arrivalTime, int burstTime, int priority,int quantum) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingBurstTime = burstTime;
        this.isCompleted = false;
        this.quantum = quantum;
        this.fcaiFactor = 0.0;
        this.startTime = -1;
    }
    public void updateQuantum(int increment) {
        this.quantum += increment;
    }


    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getRemainingBurstTime() {
        return remainingBurstTime;
    }

    public double getFCAIFactor() {
        return fcaiFactor;
    }


    public void reduceRemainingBurstTime(int time) {
        this.remainingBurstTime -= time;
    }







}

class CPUScheduler {
    // Non-Preemptive Priority Scheduling
    public static void priorityScheduling(List<Process> processes) {
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        int contextSwitchOverhead = 1;

        while (completed < n) {
            // Select the highest priority process among arrived processes
            Process highestPriorityProcess = null;
            for (Process process : processes) {
                if (!process.isCompleted && process.arrivalTime <= currentTime) {
                    if (highestPriorityProcess == null || process.priority < highestPriorityProcess.priority ||
                            (process.priority == highestPriorityProcess.priority && process.arrivalTime < highestPriorityProcess.arrivalTime)) {
                        highestPriorityProcess = process;
                    }
                }
            }

            if (highestPriorityProcess == null) {
                // No process is ready; advance time to the next arrival
                currentTime++;
            } else {
                // Apply context-switch overhead
                if (currentTime > 0) {
                    currentTime += contextSwitchOverhead;
                }

                // Execute the selected process
                highestPriorityProcess.waitingTime = currentTime - highestPriorityProcess.arrivalTime;
                highestPriorityProcess.turnaroundTime = highestPriorityProcess.waitingTime + highestPriorityProcess.burstTime;
                currentTime += highestPriorityProcess.burstTime;
                highestPriorityProcess.isCompleted = true;
                completed++;
            }
        }

        printSchedulerResults("Non-Preemptive Priority Scheduling", processes);
    }

    // Non-Preemptive Shortest Job First (SJF)
    public static void sjfScheduling(List<Process> processes) {
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();

        while (completed < n) {
            // Select the process with the shortest burst time among arrived processes
            Process shortestJob = null;
            for (Process process : processes) {
                if (!process.isCompleted && process.arrivalTime <= currentTime) {
                    if (shortestJob == null || process.burstTime < shortestJob.burstTime ||
                            (process.burstTime == shortestJob.burstTime && process.arrivalTime < shortestJob.arrivalTime)) {
                        shortestJob = process;
                    }
                }
            }

            if (shortestJob == null) {
                // No process is ready; advance time to the next arrival
                currentTime++;
            } else {
                // Execute the selected process
                shortestJob.waitingTime = currentTime - shortestJob.arrivalTime;
                shortestJob.turnaroundTime = shortestJob.waitingTime + shortestJob.burstTime;
                currentTime += shortestJob.burstTime;
                shortestJob.isCompleted = true;
                completed++;
            }
        }

        printSchedulerResults("Non-Preemptive Shortest Job First (SJF)", processes);
    }

    public static void printSchedulerResults(String schedulerName, List<Process> processes) {
        System.out.println(schedulerName);
        System.out.println("Process\tArrival\tBurst\tWaiting\tTurnaround");
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (Process process : processes) {
            System.out.println(process.name + "\t" + process.arrivalTime + "\t" +
                    process.burstTime + "\t" + process.waitingTime + "\t" +
                    process.turnaroundTime);
            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;
        }

        System.out.println("Average Waiting Time: " + (double) totalWaitingTime / processes.size());
        System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / processes.size());
    }


    // Shortest Remaining Time First (SRTF)
    public static void srtfScheduling(List<Process> processes) {
        List<String> timeline = new ArrayList<>();
        int currentTime = 0;
        int completedProcesses = 0;
        int contextSwitchOverhead = 1;
        Process lastExecutedProcess = null;

        while (completedProcesses < processes.size()) {
            Process shortestProcess = null;
            int shortestRemainingTime = Integer.MAX_VALUE;

            // Find the process with the shortest remaining burst time among the ready processes
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingBurstTime > 0) {
                    if (p.remainingBurstTime < shortestRemainingTime ||
                            (p.remainingBurstTime == shortestRemainingTime && p.arrivalTime < (shortestProcess != null ? shortestProcess.arrivalTime : Integer.MAX_VALUE))) {
                        shortestProcess = p;
                        shortestRemainingTime = p.remainingBurstTime;
                    }
                }
            }

            if (shortestProcess != null) {
                // Handle context switch if a new process is selected
                if (lastExecutedProcess != shortestProcess) {
                    currentTime += contextSwitchOverhead;
                    lastExecutedProcess = shortestProcess;
                }

                // Execute the selected process for 1 time unit
                shortestProcess.remainingBurstTime--;
                timeline.add("P" + shortestProcess.name + ":" + currentTime); // Track process execution
                currentTime++;

                // If the process completes execution
                if (shortestProcess.remainingBurstTime == 0) {
                    completedProcesses++;
                    shortestProcess.turnaroundTime = currentTime - shortestProcess.arrivalTime;
                    shortestProcess.waitingTime = shortestProcess.turnaroundTime - shortestProcess.burstTime;
                    shortestProcess.isCompleted = true;
                }
            } else {
                // No process is ready; advance time
                currentTime++;
            }
        }

        printSchedulerResults("Shortest Remaining Time First (SRTF)", processes);
        drawGanttChart(timeline);
    }

    public static void drawGanttChart(List<String> timeline) {
        JFrame frame = new JFrame("CPU Scheduling Graph");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int x = 50; // Starting x position
                int y = 100; // y position
                int width = 50; // Width of each block
                int height = 50; // Height of each block

                Map<String, Color> processColors = new HashMap<>();
                int colorIndex = 0;

                for (String event : timeline) {
                    String[] parts = event.split(":");
                    String processName = parts[0];
                    int time = Integer.parseInt(parts[1]);

                    // Assign colors for each process
                    processColors.putIfAbsent(processName, new Color((int) (Math.random() * 0x1000000)));

                    // Draw process block
                    g.setColor(processColors.get(processName));
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, width, height);

                    // Draw process name and time
                    g.drawString(processName, x + 10, y + 20);
                    g.drawString(String.valueOf(time), x + 10, y + height + 15);

                    x += width; // Move to the next block
                }
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }









    // Calculate FCAI factors for the processes
    private static double calculateFCAIFactor (Process process,double v1, double v2){
        return Math.ceil((10 - process.priority) + ((double) process.arrivalTime / v1) + ((double) process.remainingBurstTime / v2));
    }

    // Scheduler implementation
    public static void dynamicFCAIScheduler(List<Process> processes) {
        int context = 0;
        int lastArrivalTime = processes.stream().max(Comparator.comparingInt(p -> p.arrivalTime)).get().arrivalTime;
        int maxBurstTime = processes.stream().max(Comparator.comparingInt(p -> p.burstTime)).get().burstTime;
        double v1 =  ((double) lastArrivalTime / 10);
        double v2 =  ((double) maxBurstTime / 10);

        Queue<Process> readyQueue = new LinkedList<>();
        List<Process> completedProcesses = new ArrayList<>();

        int currentTime = 0;
        Process currentProcess = null;
        double bestFactor = Double.MAX_VALUE;

        while (readyQueue.size() == 0) {
            for (Process process : processes) {
                if (process.arrivalTime <= currentTime && !readyQueue.contains(process) && !process.isCompleted) {
                    readyQueue.add(process);
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
            }




            for (Process process : readyQueue) {
                double factor = calculateFCAIFactor(process, v1, v2);
                if (factor < bestFactor) {
                    bestFactor = factor;
                    currentProcess = process;
                }
            }
        }





        while (completedProcesses.size() < processes.size()) {




            readyQueue.remove(currentProcess);

            if (currentProcess.startTime == -1) {
                currentProcess.startTime = currentTime;
            }

            int executionTime = (int) Math.ceil(currentProcess.quantum * 0.4);
            executionTime = Math.min(executionTime, currentProcess.remainingBurstTime);
            currentTime += executionTime + context;


            boolean flag = true;
            while (flag){
                for (Process process : processes) {
                    if (process.arrivalTime <= currentTime && process!=currentProcess) {
                         flag = false;
                         break;
                    }

                }
                if (!flag) {break;}
                currentTime++;
                executionTime++;

            }
            int keeper = executionTime;
            for (Process process : processes) {
                if (process.arrivalTime <= currentTime && !readyQueue.contains(process) && !process.isCompleted && process!=currentProcess) {
                    readyQueue.add(process);
                }
            }


            boolean flag2 = true;
            while (flag2){
                currentProcess.remainingBurstTime -= executionTime;
                if (currentProcess.remainingBurstTime <= 0) {
                    currentProcess.isCompleted = true;
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    completedProcesses.add(currentProcess);
                    for (Process process : processes) {
                        if (process.arrivalTime <= currentTime && !readyQueue.contains(process) && !process.isCompleted && process!=currentProcess) {
                            readyQueue.add(process);
                        }
                    }
                    currentProcess = null;
                    break;
                }
                executionTime = 0;
                bestFactor = calculateFCAIFactor(currentProcess, v1, v2);

                for (Process process : processes) {
                    if (process.arrivalTime <= currentTime && process!=currentProcess && !process.isCompleted) {


                        double factor = calculateFCAIFactor(process, v1, v2);

                        if (factor <= bestFactor) {

                            if (currentProcess.remainingBurstTime > 0) {
                                if (keeper < currentProcess.quantum) {
                                    currentProcess.updateQuantum(currentProcess.quantum - keeper);
                                } else {
                                    currentProcess.updateQuantum(2);
                                }

                            } else {
                                currentProcess.isCompleted = true;
                                currentProcess.completionTime = currentTime;
                                currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                                completedProcesses.add(currentProcess);
                            }
                            flag2 = false;


                            currentProcess = process;
                            break;
                        }
                    }

                }
                if (!flag2) {break;}



                currentTime++;
                executionTime++;
                keeper++;
                if (keeper >= currentProcess.quantum) {
                    currentProcess.remainingBurstTime -= executionTime;

                    if (currentProcess.remainingBurstTime > 0) {
                        if (keeper < currentProcess.quantum) {
                            currentProcess.updateQuantum(currentProcess.quantum - keeper);
                            readyQueue.add(currentProcess);
                        } else {
                            currentProcess.updateQuantum(2);
                            readyQueue.add(currentProcess);
                        }

                    } else {
                        currentProcess.isCompleted = true;
                        currentProcess.completionTime = currentTime;
                        currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                        currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                        completedProcesses.add(currentProcess);
                    }
                    flag2 = false;
                    currentProcess = null;
                    break;

                }


            }

            if (currentProcess==null){
            currentProcess = readyQueue.poll();}





        }
        // Calculate average times
        double avgWaitingTime = completedProcesses.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double avgTurnaroundTime = completedProcesses.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);

        // Print results
        System.out.println("\nProcesses execution order:");
        completedProcesses.forEach(p -> System.out.println(p.name));

        System.out.println("\nProcess Details:");
        for (Process p : completedProcesses) {
            System.out.println("Process " + p.name + " -> Waiting Time: " + p.waitingTime + ", Turnaround Time: " + p.turnaroundTime);
        }

        System.out.println("\nAverage Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
    }

















    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

//        System.out.print("Enter number of processes: ");
//        int numProcesses = scanner.nextInt();

        List<Process> processes = new ArrayList<>();

//        for (int i = 0; i < numProcesses; i++) {
//            System.out.println("Process " + (i + 1) + " details:");
//            System.out.print("Process Name: ");
//            String name = scanner.next();
//
//            System.out.print("Process Color: ");
//            String color = scanner.next();
//
//            System.out.print("Arrival Time: ");
//            int arrivalTime = scanner.nextInt();
//
//            System.out.print("Burst Time: ");
//            int burstTime = scanner.nextInt();
//
//            System.out.print("Priority: ");
//            int priority = scanner.nextInt();
//
//            System.out.print("Quantum: ");
//            int quantum = scanner.nextInt();
//
//            processes.add(new Process(name, color, arrivalTime, burstTime, priority, quantum));
//        }

        // Make copies of processes for different scheduling algorithms


        processes.add(new Process("1", "1", 0, 17, 4, 4));
        processes.add(new Process("2", "2", 3, 6, 9, 3));
        processes.add(new Process("3", "3", 4, 10, 3, 5));
        processes.add(new Process("4", "4", 29, 4, 10, 2));



        srtfScheduling(new ArrayList<>(processes));


        scanner.close();
    }
}

