# Hybrid Job Scheduler Simulator

This project simulates a hybrid job scheduler that dynamically balances workloads between a local datacenter and a remote datacenter. 
It models CPU-bound task scheduling, queue management, data-aware task transfers, and system load behavior based on real-world trace data.

## Features

- CPU Resource Scheduling: Simulates task arrivals, queuing, execution, and completion across local and remote datacenters.
- Wait-Time Based Offloading: Transfers tasks to the remote datacenter if they exceed a computed wait time threshold.
- Bandwidth-Aware Data Transfer: Models real-time data transfers with shared bandwidth and incremental transfer progress tracking.
- Real-World Trace Integration: Loads and simulates actual job traces from CSV to reflect realistic job patterns.
- Milestone Logging: Records key task events (arrival, transfer, completion) and outputs to CSV for post-analysis.


## Project Structure

Datacenter.java: Handles task queues, scheduling, and transfer logic.  
Task.java: Defines task attributes and state transitions.  
Scheduler.java: Coordinates simulation workflow and task loading.  
TaskMilestone.java: Records and exports per-task milestone data.  
JobSchedulerDemo.java: Entry point for running batch simulations.  

## How It Works

### Task Loading
- Tasks are read from a CSV trace (`cluster_log.csv`)
- Arrival times are scaled by a factor to simulate different system loads
- Each task includes CPU demand, duration, and calculated data load
- Tasks are initially added to the local datacenter's event queue

### Simulation Loop (per tick)
1. Check for task arrivals at the current time
2. Start executing task if there is enough resources(CPU) at this time tick
3. Increment wait time for tasks in the waiting queue
4. Transfer tasks to remote datacenter if they exceed max wait time
5. Track transfer progress using bandwidth divided per active task
6. Start execution on the remote side once transfer completes
7. Record task completions and update system status

### Post-Processing
- Task milestone data is saved to a CSV file for offline analysis
