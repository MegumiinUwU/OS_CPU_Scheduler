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
        List<String> timeline = new ArrayList<>();

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
                timeline.add("Time " + currentTime + ": Process " + highestPriorityProcess.name + " is running");
                highestPriorityProcess.waitingTime = currentTime - highestPriorityProcess.arrivalTime;
                highestPriorityProcess.turnaroundTime = highestPriorityProcess.waitingTime + highestPriorityProcess.burstTime;
                currentTime += highestPriorityProcess.burstTime;
                highestPriorityProcess.isCompleted = true;
                completed++;
                timeline.add("Time " + currentTime + ": Process " + highestPriorityProcess.name + " is completed");
            }
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
                int maxTime = 0;

                String previousProcess = null;
                int previousTime = 0;
                // Map to track start and end times for each process
                Map<String, Integer> processStartTime = new HashMap<>();
                Map<String, Integer> processEndTime = new HashMap<>();

                for (String log : processLog) {
                    try {
                        String[] parts = log.split(":");
                        int time = Integer.parseInt(parts[0].split(" ")[1].trim());
                        String[] eventParts = parts[1].trim().split(" ", 3);
                        String processName = eventParts[1];
                        String event = eventParts[2];

                        maxTime = Math.max(maxTime, time);

                        // Assign a unique color to each process
                        if (!processColors.containsKey(processName)) {
                            processColors.put(processName, colorIndex++);
                        }

                        // Set color for the current process
                        g.setColor(getColorByIndex(processColors.get(processName)));

                        // Draw a bar segment for "is running"
                        if (event.equals("is running")) {
                            // Mark the start time for the process
                            processStartTime.put(processName, time);
                        } else if (event.equals("is completed")) {
                            // Mark the end time for the process
                            processEndTime.put(processName, time);

                            // Draw the bar for the process
                            int start = processStartTime.get(processName);
                            int end = processEndTime.get(processName);
                            int barWidth = (end - start) * 20; // Calculate width based on duration

                            g.setColor(getColorByIndex(processColors.get(processName)));
                            g.fillRect(startX + (start * 20), currentY, barWidth, barHeight);

                        }

                        previousProcess = processName;
                        previousTime = time;
                    } catch (Exception e) {
                        System.err.println("Error processing log: " + log);
                    }
                }

                // Draw time markers
                g.setColor(Color.BLACK);
                int totalTime = previousTime + 1; // Adjust for the last recorded time
                for (int i = 0; i <= totalTime; i++) {
                    int x = startX + (i * 20);
                    g.drawLine(x, 0, x, getHeight());
                    g.drawString(String.valueOf(i), x, 15);
                }

                // Draw the legend below the timeline
                int legendStartX = startX + maxTime * 20 + 40;  // X-coordinate for the legend
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

                // Update preferred size dynamically
                setPreferredSize(new Dimension(maxTime * 20 + startX + 100, currentY + spacing));
                revalidate();

            }


            private Color getColorByIndex(int index) {
                Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA};
                return colors[index % colors.length];
            }
        }


        TimelinePanel timelinePanel = new TimelinePanel(timeline);
        JScrollPane timelineScrollPane = new JScrollPane(timelinePanel);
        timelineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        timelineScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        timelineScrollPane.setPreferredSize(new Dimension(800, 200));



// Process Logs Panel
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        timeline.forEach(log -> logArea.append(log + "\n"));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(800, 150));

// Process Table
        String[] columnNames = {"Name", "Arrival", "Burst", "Priority", "Waiting", "Turnaround"};
        String[][] data = new String[processes.size()][6];
        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);
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
                (processes.stream().mapToInt(p -> p.waitingTime).average().orElse(0)), SwingConstants.CENTER));
        statsPanel.add(new JLabel("Average Turnaround Time: " +
                (processes.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0)), SwingConstants.CENTER));
        statsPanel.setBackground(new Color(240, 240, 240));

// Layout setup
        JFrame frame = new JFrame("CPU Scheduling Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

// Add components to the frame
        frame.add(timelineScrollPane, BorderLayout.NORTH); // Add timeline panel
        frame.add(logScrollPane, BorderLayout.CENTER);
        frame.add(tableScrollPane, BorderLayout.WEST);
        frame.add(statsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);



        printSchedulerResults("Non-Preemptive Priority Scheduling", processes);
    }

    // Non-Preemptive Shortest Job First (SJF)
    public static void sjfScheduling(List<Process> processes) {
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        int contextSwitchOverhead = 1;
        List<String> timeline = new ArrayList<>();

        Map<Process, Integer> ageMap = new HashMap<>();
        for (Process process : processes) {
            ageMap.put(process, 0);
        }

        while (completed < n) {
            // Select the process with the shortest burst time among arrived processes
            Process shortestJob = null;
            int shortestEffectiveBurstTime = Integer.MAX_VALUE;
            for (Process process : processes) {
                if (!process.isCompleted && process.arrivalTime <= currentTime ) {
                    int newAge = ageMap.get(process) + 1;
                    ageMap.put(process, newAge);

                    int effectiveBurstTime = process.burstTime - newAge;

                    if (effectiveBurstTime < shortestEffectiveBurstTime ||
                            (effectiveBurstTime == shortestEffectiveBurstTime && process.arrivalTime < shortestJob.arrivalTime)) {
                        shortestJob = process;
                        shortestEffectiveBurstTime = effectiveBurstTime;

                    }
                }
            }

            if (shortestJob == null) {
                // No process is ready; advance time to the next arrival
                currentTime++;
            } else {
                // Execute the selected process
                currentTime += contextSwitchOverhead;
                timeline.add("Time " + currentTime + ": Process " + shortestJob.name + " is running");
                shortestJob.waitingTime = currentTime - shortestJob.arrivalTime;
                shortestJob.turnaroundTime = shortestJob.waitingTime + shortestJob.burstTime;
                currentTime += shortestJob.burstTime;
                shortestJob.isCompleted = true;
                completed++;
                timeline.add("Time " + currentTime + ": Process " + shortestJob.name + " is completed");
            }
        }

        for (String jojo : timeline ) {
            System.out.println(jojo);
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
                int maxTime = 0;

                String previousProcess = null;
                int previousTime = 0;
                // Map to track start and end times for each process
                Map<String, Integer> processStartTime = new HashMap<>();
                Map<String, Integer> processEndTime = new HashMap<>();

                for (String log : processLog) {
                    try {
                        String[] parts = log.split(":");
                        int time = Integer.parseInt(parts[0].split(" ")[1].trim());
                        String[] eventParts = parts[1].trim().split(" ", 3);
                        String processName = eventParts[1];
                        String event = eventParts[2];

                        maxTime = Math.max(maxTime, time);

                        // Assign a unique color to each process
                        if (!processColors.containsKey(processName)) {
                            processColors.put(processName, colorIndex++);
                        }

                        // Set color for the current process
                        g.setColor(getColorByIndex(processColors.get(processName)));

                        // Draw a bar segment for "is running"
                        if (event.equals("is running")) {
                            // Mark the start time for the process
                            processStartTime.put(processName, time);
                        } else if (event.equals("is completed")) {
                            // Mark the end time for the process
                            processEndTime.put(processName, time);

                            // Draw the bar for the process
                            int start = processStartTime.get(processName);
                            int end = processEndTime.get(processName);
                            int barWidth = (end - start) * 20; // Calculate width based on duration

                            g.setColor(getColorByIndex(processColors.get(processName)));
                            g.fillRect(startX + (start * 20), currentY, barWidth, barHeight);

                        }

                        previousProcess = processName;
                        previousTime = time;
                    } catch (Exception e) {
                        System.err.println("Error processing log: " + log);
                    }
                }

                // Draw time markers
                g.setColor(Color.BLACK);
                int totalTime = previousTime + 1; // Adjust for the last recorded time
                for (int i = 0; i <= totalTime; i++) {
                    int x = startX + (i * 20);
                    g.drawLine(x, 0, x, getHeight());
                    g.drawString(String.valueOf(i), x, 15);
                }

                // Draw the legend below the timeline
                int legendStartX = startX + maxTime * 20 + 40;  // X-coordinate for the legend
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

                // Update preferred size dynamically
                setPreferredSize(new Dimension(maxTime * 20 + startX + 100, currentY + spacing));
                revalidate();

            }


            private Color getColorByIndex(int index) {
                Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA};
                return colors[index % colors.length];
            }
        }


        TimelinePanel timelinePanel = new TimelinePanel(timeline);
        JScrollPane timelineScrollPane = new JScrollPane(timelinePanel);
        timelineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        timelineScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        timelineScrollPane.setPreferredSize(new Dimension(800, 200));



// Process Logs Panel
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        timeline.forEach(log -> logArea.append(log + "\n"));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(800, 150));

// Process Table
        String[] columnNames = {"Name", "Arrival", "Burst", "Priority", "Waiting", "Turnaround"};
        String[][] data = new String[processes.size()][6];
        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);
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
                (processes.stream().mapToInt(p -> p.waitingTime).average().orElse(0)), SwingConstants.CENTER));
        statsPanel.add(new JLabel("Average Turnaround Time: " +
                (processes.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0)), SwingConstants.CENTER));
        statsPanel.setBackground(new Color(240, 240, 240));

// Layout setup
        JFrame frame = new JFrame("CPU Scheduling Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

// Add components to the frame
        frame.add(timelineScrollPane, BorderLayout.NORTH); // Add timeline panel
        frame.add(logScrollPane, BorderLayout.CENTER);
        frame.add(tableScrollPane, BorderLayout.WEST);
        frame.add(statsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);





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
        Map<Process, Integer> ageMap = new HashMap<>();

        for (Process p : processes) {
            ageMap.put(p, 0);
        }

        while (completedProcesses < processes.size()) {
            Process shortestProcess = null;

            int shortestEffectiveTime = Integer.MAX_VALUE;


            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingBurstTime > 0  && shortestProcess != p) {
                    int newAge = ageMap.get(p) + 1;
                    ageMap.put(p, newAge);
                }
            }

            // Find the process with the shortest remaining burst time among the ready processes
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingBurstTime > 0) {
                    int effectiveTime = p.remainingBurstTime - ageMap.get(p);
                    if (effectiveTime < shortestEffectiveTime ||
                            (effectiveTime == shortestEffectiveTime && p.arrivalTime < (shortestProcess != null ? shortestProcess.arrivalTime : Integer.MAX_VALUE))) {
                        shortestProcess = p;
                        shortestEffectiveTime = effectiveTime;
                    }
                }
            }

            if (shortestProcess != null) {

                if (lastExecutedProcess != shortestProcess) {
                    currentTime += contextSwitchOverhead;
                    lastExecutedProcess = shortestProcess;
                }


                shortestProcess.remainingBurstTime--;
                timeline.add("Time " + currentTime + ": Process " + shortestProcess.name + " is running");
                currentTime++;


                if (shortestProcess.remainingBurstTime == 0) {
                    completedProcesses++;
                    shortestProcess.turnaroundTime = currentTime - shortestProcess.arrivalTime;
                    shortestProcess.waitingTime = shortestProcess.turnaroundTime - shortestProcess.burstTime;
                    shortestProcess.isCompleted = true;
                    ageMap.remove(shortestProcess);
                }
            } else {

                currentTime++;
            }
        }

        printSchedulerResults("Shortest Remaining Time First (SRTF)", processes);


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
                int maxTime = 0;

                String previousProcess = null;
                int previousTime = 0;

                for (String log : processLog) {
                    try {
                        String[] parts = log.split(":");
                        int time = Integer.parseInt(parts[0].split(" ")[1].trim());
                        String[] eventParts = parts[1].trim().split(" ", 3);
                        String processName = eventParts[1];
                        String event = eventParts[2];

                        maxTime = Math.max(maxTime, time);

                        // Assign a unique color to each process
                        if (!processColors.containsKey(processName)) {
                            processColors.put(processName, colorIndex++);
                        }

                        // Set color for the current process
                        g.setColor(getColorByIndex(processColors.get(processName)));

                        // Draw a bar segment for "is running"
                        if (event.equals("is running")) {
                            int barWidth = 20; // Fixed width for one unit of time
                            g.fillRect(startX + (time * barWidth), currentY, barWidth, barHeight);
                        }

                        // Move down for "is completed" events
                        if (event.equals("is completed")) {
                            currentY += barHeight + spacing;
                        }

                        previousProcess = processName;
                        previousTime = time;
                    } catch (Exception e) {
                        System.err.println("Error processing log: " + log);
                    }
                }

                // Draw time markers
                g.setColor(Color.BLACK);
                int totalTime = previousTime + 1; // Adjust for the last recorded time
                for (int i = 0; i <= totalTime; i++) {
                    int x = startX + (i * 20);
                    g.drawLine(x, 0, x, getHeight());
                    g.drawString(String.valueOf(i), x, 15);
                }

                // Draw the legend below the timeline
                int legendStartX = startX + maxTime * 20 + 40;  // X-coordinate for the legend
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

                // Update preferred size dynamically
                setPreferredSize(new Dimension(maxTime * 20 + startX + 100, currentY + spacing));
                revalidate();

            }


            private Color getColorByIndex(int index) {
                Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA};
                return colors[index % colors.length];
            }
        }


        TimelinePanel timelinePanel = new TimelinePanel(timeline);
        JScrollPane timelineScrollPane = new JScrollPane(timelinePanel);
        timelineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        timelineScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        timelineScrollPane.setPreferredSize(new Dimension(800, 200));



// Process Logs Panel
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        timeline.forEach(log -> logArea.append(log + "\n"));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(800, 150));

// Process Table
        String[] columnNames = {"Name", "Arrival", "Burst", "Priority", "Waiting", "Turnaround"};
        String[][] data = new String[processes.size()][6];
        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);
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
                (processes.stream().mapToInt(p -> p.waitingTime).average().orElse(0)), SwingConstants.CENTER));
        statsPanel.add(new JLabel("Average Turnaround Time: " +
                (processes.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0)), SwingConstants.CENTER));
        statsPanel.setBackground(new Color(240, 240, 240));

// Layout setup
        JFrame frame = new JFrame("CPU Scheduling Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

// Add components to the frame
        frame.add(timelineScrollPane, BorderLayout.NORTH); // Add timeline panel
        frame.add(logScrollPane, BorderLayout.CENTER);
        frame.add(tableScrollPane, BorderLayout.WEST);
        frame.add(statsPanel, BorderLayout.SOUTH);

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
            private int jojoTime;

            public TimelinePanel(List<String> processLog) {
                this.processLog = processLog;
                this.jojoTime = 0;
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
                // Update preferred size dynamically
                setPreferredSize(new Dimension(maxTime * 20 + startX + 100, currentY + spacing));
                revalidate();



            }

            private Color getColorByIndex(int index) {
                // Cycle through a set of predefined colors
                Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA};
                return colors[index % colors.length];
            }
        }


// Timeline Panel
        TimelinePanel timelinePanel = new TimelinePanel(processLog);
        JScrollPane timelineScrollPane = new JScrollPane(timelinePanel);
        timelineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        timelineScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        timelineScrollPane.setPreferredSize(new Dimension(800, 300));



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
        frame.add(timelineScrollPane, BorderLayout.NORTH); // Add timeline panel
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
        List<Process> processes = new ArrayList<>();
        boolean running = true;


        while (running) {
            System.out.println("=== CPU Scheduling Menu ===");
            System.out.println("1. Add Process");
            System.out.println("2. View Processes");
            System.out.println("3. Priority Scheduling");
            System.out.println("4. Dynamic FCAI Scheduler");
            System.out.println("5. SJF Scheduling");
            System.out.println("6. SRTF Scheduling");
            System.out.println("7. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    // Add Process
                    System.out.print("Process Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Arrival Time: ");
                    int arrivalTime = scanner.nextInt();

                    System.out.print("Burst Time: ");
                    int burstTime = scanner.nextInt();

                    System.out.print("Priority: ");
                    int priority = scanner.nextInt();

                    System.out.print("Quantum (Enter 0 if not needed): ");
                    int quantum = scanner.nextInt();

                    processes.add(new Process(name, name, arrivalTime, burstTime, priority, quantum));
                    System.out.println("Process added successfully!");
                    break;

                case 2:
                    // View Processes
                    if (processes.isEmpty()) {
                        System.out.println("No processes available.");
                    } else {
                        System.out.println("=== Process List ===");
                        for (Process p : processes) {
                            System.out.printf("Name: %s, Color: %s, Arrival: %d, Burst: %d, Priority: %d, Quantum: %d%n",
                                    p.name, p.color, p.arrivalTime, p.burstTime, p.priority, p.quantum);
                        }
                    }
                    break;

                case 3:
                    // Priority Scheduling
                    if (processes.isEmpty()) {
                        System.out.println("No processes to schedule.");
                    } else {
                        priorityScheduling(new ArrayList<>(processes));
                    }
                    break;

                case 4:
                    // Dynamic FCAI Scheduler
                    if (processes.isEmpty()) {
                        System.out.println("No processes to schedule.");
                    } else {
                        dynamicFCAIScheduler(new ArrayList<>(processes));
                    }
                    break;

                case 5:
                    // SJF Scheduling
                    if (processes.isEmpty()) {
                        System.out.println("No processes to schedule.");
                    } else {
                        sjfScheduling(new ArrayList<>(processes));
                    }
                    break;

                case 6:
                    // SRTF Scheduling
                    if (processes.isEmpty()) {
                        System.out.println("No processes to schedule.");
                    } else {
                        srtfScheduling(new ArrayList<>(processes));
                    }
                    break;

                case 7:
                    // Exit
                    System.out.println("Exiting the program.");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
        scanner.close();
    }
 }
//        processes.add(new Process("1", "Red", 0, 17, 4, 4));
//        processes.add(new Process("2", "Gold", 3, 6, 9, 3));
//        processes.add(new Process("3", "Yellow", 4, 10, 3, 5));
//        processes.add(new Process("4", "Green", 29, 4, 10, 2));
//
//
//
//        priorityScheduling(new ArrayList<>(processes));
//        dynamicFCAIScheduler(new ArrayList<>(processes));
//        sjfScheduling(new ArrayList<>(processes));
//        srtfScheduling(new ArrayList<>(processes));
//
//
//
//
//        scanner.close();
//    }
//}

