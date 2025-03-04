# OS CPU Scheduler

## Overview
This project implements multiple CPU scheduling algorithms, including traditional and adaptive techniques, to optimize process execution efficiency while addressing issues like starvation.

## Implemented Scheduling Algorithms

### 1. Non-Preemptive Priority Scheduling
- Uses context switching for process execution.  
- Higher-priority processes execute first.  

### 2. Non-Preemptive Shortest Job First (SJF)
- Selects the process with the shortest burst time.  
- Implements starvation prevention mechanisms.  

### 3. Shortest Remaining Time First (SRTF)
- A preemptive variant of SJF.  
- Dynamically switches processes based on remaining burst time.  
- Addresses starvation issues.  

### 4. FCAI Scheduling (Custom Algorithm)
A dynamic scheduling algorithm that optimizes execution order based on a composite metric.  
#### Key Features:

- **Dynamic FCAI Factor:**  
  ```
  FCAI Factor = (10 - Priority) + (Arrival Time / V1) + (Remaining Burst Time / V2)
  ```
  Where:  
  - `V1 = last arrival time of all processes / 10`  
  - `V2 = max burst time of all processes / 10`  

- **Quantum Allocation Rules:**  
  - Each process starts with a unique quantum.  
  - Quantum dynamically updates:  
    - `Q = Q + 2` (if a process completes its quantum but still has work remaining).  
    - `Q = Q + unused quantum` (if a process is preempted).  

- **Preemptive & Non-Preemptive Execution:**  
  - A process runs non-preemptively for the first **40%** of its quantum.  
  - Preemption is allowed after **40%** execution.  

## Graphical User Interface (GUI)
We developed a GUI to visualize the scheduling results on a timeline. The GUI also displays the **average waiting time** and **average turnaround time** for better analysis of scheduling performance.
