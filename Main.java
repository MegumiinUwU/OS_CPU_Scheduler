package org.AdvSW;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
        List<String> processLog = new ArrayList<>();

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
                processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " started running");
            }
            processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " is running");

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
                    processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " completed");
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
                            processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " preempted. Remaining burst time: " + currentProcess.remainingBurstTime);

                            if (currentProcess.remainingBurstTime > 0) {
                                if (keeper < currentProcess.quantum) {
                                    currentProcess.updateQuantum(currentProcess.quantum - keeper);
                                    processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " quantum updated to " + currentProcess.quantum);
                                } else {
                                    currentProcess.updateQuantum(2);
                                    processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " quantum updated to " + currentProcess.quantum);
                                }

                            } else {
                                currentProcess.isCompleted = true;
                                currentProcess.completionTime = currentTime;
                                currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                                completedProcesses.add(currentProcess);
                                processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " completed");
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
                    processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " preempted. Remaining burst time: " + currentProcess.remainingBurstTime);

                    if (currentProcess.remainingBurstTime > 0) {
                        if (keeper < currentProcess.quantum) {
                            currentProcess.updateQuantum(currentProcess.quantum - keeper);
                            processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " quantum updated to " + currentProcess.quantum);
                            readyQueue.add(currentProcess);
                        } else {
                            currentProcess.updateQuantum(2);
                            processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " quantum updated to " + currentProcess.quantum);
                            readyQueue.add(currentProcess);
                        }

                    } else {
                        currentProcess.isCompleted = true;
                        currentProcess.completionTime = currentTime;
                        currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                        currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                        processLog.add("Time " + currentTime + ": Process " + currentProcess.name + " completed");
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
        for (String log : processLog) {
            System.out.println(log);
        }

        class TimelinePanel extends JPanel {
            private final List<String> processLog;

            public TimelinePanel(List<String> processLog) {
                this.processLog = processLog;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int barHeight = 20; // Height of each process bar
                int spacing = 30;   // Spacing between bars
                int startX = 50;    // Start x-coordinate for drawing bars
                int currentY = 20;  // Start y-coordinate for the first process


                Map<String, Integer> processColors = new HashMap<>();
                int colorIndex = 0;

                String previousProcess = null;
                int previousTime = 0;
                int maxTime = 0;

                for (String log : processLog) {
                    try {
                        String[] parts = log.split(":");
                        if (parts.length < 2) {
                            continue; // Skip invalid log entries
                        }


                        int time;
                        try {
                            time = Integer.parseInt((parts[0].split(" "))[1].trim());
                            maxTime = Math.max(maxTime, time); // Keep track of the max time
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid time format in log: " + log);
                            continue; // Skip invalid time entries
                        }

                        String processName = parts[1].trim().split(" ")[1]; // Extract process name
                        String event = parts[1].trim().split(" ", 3)[2];    // Extract event description

                        // Assign a unique color to each process
                        if (!processColors.containsKey(processName)) {
                            processColors.put(processName, colorIndex++);
                        }
                        g.setColor(getColorByIndex(processColors.get(processName)));

                        // If the process changes, draw a continuous bar from previousTime to current time
                        if (previousProcess != null && previousProcess.equals(processName)) {
                            int barWidth = (time - previousTime) * 20; // Width from previous time to current time
                            g.fillRect(startX + (previousTime * 20), currentY, barWidth, barHeight);
                        }

                        // Update the previous process and time
                        previousProcess = processName;
                        previousTime = time;




                        // Adjust Y-coordinate for spacing after "completed" events
                        if (event.equals("completed")) {
                            currentY += barHeight + spacing;
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing log: " + log);
                    }
                }
                g.setColor(Color.BLACK); // Color for the horizontal lines and labels
                for (int i = 0; i <= maxTime; i++) {
                    int lineX = startX + (i * 20); // Calculate the X position for each time line
                    g.drawLine(lineX, 0, lineX, getHeight()); // Draw the horizontal line from top to bottom
                    g.drawString(String.valueOf(i), lineX, 15); // Draw the time label just below the line
                }

                // Draw the legend below the timeline
                int legendStartX = startX + maxTime * 20 + 20;  // X-coordinate for the legend
                int legendY = 30;  // Start Y-coordinate for the legend

                // Label for the legend
                g.setColor(Color.BLACK);
                g.drawString("Process:", legendStartX, legendY-7);

                // Draw each process's color box and label
                int legendBoxSize = 20;  // Size of the color box in the legend
                int legendSpacing = 30;  // Vertical spacing between legend items
                int legendIndex = 0;

                for (Map.Entry<String, Integer> entry : processColors.entrySet()) {
                    g.setColor(getColorByIndex(entry.getValue()));
                    g.fillRect(legendStartX, legendY + (legendIndex * legendSpacing), legendBoxSize, legendBoxSize); // Draw color box
                    g.setColor(Color.BLACK);  // Set text color to black for labels
                    g.drawString(entry.getKey(), legendStartX + legendBoxSize + 5, legendY +4 + (legendIndex * legendSpacing) + legendBoxSize / 2); // Draw process name next to the color box
                    legendIndex++;
                }




            }

            private Color getColorByIndex(int index) {
                // Cycle through a set of predefined colors
                Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA};
                return colors[index % colors.length];
            }
        }


// Timeline Panel
        TimelinePanel timelinePanel = new TimelinePanel(processLog);
        timelinePanel.setPreferredSize(new Dimension(800, 200));

// Process Logs Panel
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        processLog.forEach(log -> logArea.append(log + "\n"));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(800, 150));

// Process Table
        String[] columnNames = {"Name", "Arrival", "Burst", "Priority", "Waiting", "Turnaround"};
        String[][] data = new String[completedProcesses.size()][6];
        for (int i = 0; i < completedProcesses.size(); i++) {
            Process p = completedProcesses.get(i);
            data[i] = new String[]{
                    p.name,
                    String.valueOf(p.arrivalTime),
                    String.valueOf(p.burstTime),
                    String.valueOf(p.priority),
                    String.valueOf(p.waitingTime),
                    String.valueOf(p.turnaroundTime)
            };
        }
        JTable processTable = new JTable(data, columnNames);
        processTable.setRowHeight(30);
        processTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane tableScrollPane = new JScrollPane(processTable);

// Stats Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 1, 10, 10));
        statsPanel.add(new JLabel("Average Waiting Time: " +
                (completedProcesses.stream().mapToInt(p -> p.waitingTime).average().orElse(0)), SwingConstants.CENTER));
        statsPanel.add(new JLabel("Average Turnaround Time: " +
                (completedProcesses.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0)), SwingConstants.CENTER));
        statsPanel.setBackground(new Color(240, 240, 240));

// Layout setup
        JFrame frame = new JFrame("CPU Scheduling Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

// Add components to the frame
        frame.add(timelinePanel, BorderLayout.NORTH); // Add timeline panel
        frame.add(logScrollPane, BorderLayout.CENTER);
        frame.add(tableScrollPane, BorderLayout.WEST);
        frame.add(statsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);




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


        processes.add(new Process("1", "Red", 0, 17, 4, 4));
        processes.add(new Process("2", "Gold", 3, 6, 9, 3));
        processes.add(new Process("3", "Yellow", 4, 10, 3, 5));
        processes.add(new Process("4", "Green", 29, 4, 10, 2));



        dynamicFCAIScheduler(new ArrayList<>(processes));



        scanner.close();
    }
}



