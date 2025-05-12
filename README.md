# Hybrid Job Scheduler Simulator

In my project, I explored and extended ideas from Starburst, a cost-aware scheduler designed for hybrid cloud environments.
It models task scheduling, queue management, data-aware task transfers, and system load changes based on real-world trace data.

## Features

- CPU Resource Scheduling: Simulates task arrivals, queuing, execution, and completion across local and remote datacenters.
- Wait Time Based Offloading: Transfers tasks to the remote datacenter if they exceed a computed wait time threshold.
- Bandwidth-related Data Transfer: Models real-time data transfers with shared bandwidth and incremental transfer progress tracking.
- Real World Trace Integration: Loads and simulates actual job traces to reflect realistic job patterns.
- Milestone Logging: Records key task events (arrival, transfer, completion) and outputs to CSV for post analysis.


## Project Structure

Datacenter.java: Handles task queues, scheduling, and transfer logic.  
Task.java: Defines task attributes and state transitions.  
Scheduler.java: manages simulation workflow and task loading.  
JobSchedulerDemo.java: Entry point for running batch simulations.  

## How It Works

### Task Loading
- Tasks are read from a CSV trace (`cluster_log.csv`)
- Arrival times are scaled by a factor to simulate different system loads
- Each task includes resource demand, duration, and a heuristic computed data load
- Tasks are initially added to the local datacenter's event queue

### Simulation Loop (per tick)
1. Check for task arrivals at the current system time
2. Start executing task if there is enough resources(CPU) at this time tick
3. Increment wait time for tasks in the waiting queue
4. Transfer tasks to remote datacenter if they exceed max wait time
5. Track transfer progress with bandwidth equally divided for each transfer task
6. Start execution on the remote datacenter if any transfer task completes transferring all dataload (assuming cloud has unlimited resources)
7. Record task completions and update system status

### Post-Processing
- Task milestone data is saved to a CSV file for offline analysis

## How to Run in IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Navigate to `JobSchedulerDemo.java`.
3. Click the green **Run** button (▶️).

## Visualization

A Python script (`plot.py`) is included to visualize key metrics from the simulation output.  
Make sure `task_milestones_*.csv` files are generated first by running the simulator.

To use:
```bash
python3 plot.py
```
## Configuration Note

The Python script (`plot.py`) reads simulation output files from a directory defined in the code:

```python
# In plot.py
base_dir = '/Users/lushazhang/Projects/SchedulerSimulation/'
```
you would need to change to your own directory for it to work. 





