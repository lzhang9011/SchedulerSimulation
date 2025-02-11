package org.example;

import java.util.*;

class Task {
    int id;
    int arrivalTime;
    int duration;
    int cpuRequirement;

    public Task(int id, int arrivalTime, int duration, int cpuRequirement) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.cpuRequirement = cpuRequirement;
    }

    @Override
    public String toString() {
        return "Task " + id + " [Arrival: " + arrivalTime + ", Duration: " + duration + ", CPU: " + cpuRequirement + "]";
    }
}

class Scheduler {
    private final int totalCPUs = 16;
    private int availableCPUs = totalCPUs;
    private final PriorityQueue<Task> eventQueue = new PriorityQueue<>(Comparator.comparingInt(j -> j.arrivalTime));
    private final Queue<Task> waitingQueue = new LinkedList<>();
    private final Map<Integer, Task> runningTasks = new HashMap<>();
    private int currentTime = 0;

    public void addTask(Task task) {
        eventQueue.offer(task);
    }

    public void runSimulation() {
        System.out.println("Simulation started.\n---------------------------------------------------------");
        printSystemStatus();

        while (!eventQueue.isEmpty() || !runningTasks.isEmpty()) {
            if (!eventQueue.isEmpty()) {
                Task task = eventQueue.peek();
                if (task.arrivalTime == currentTime) {
                    eventQueue.poll();
                    handleTaskArrival(task);
                }
            }

            handleTaskCompletion();

            currentTime++;
        }

        System.out.println("\nSimulation complete.");
    }

    private void handleTaskArrival(Task task) {
        System.out.println("Task " + task.id + " has arrived at tick " + currentTime + ", requiring " + task.cpuRequirement + " CPUs for " + task.duration + " ticks.");
        if (task.cpuRequirement <= availableCPUs) {
            System.out.println("Decision: Task " + task.id + " can run immediately.");
            startTask(task);
        } else {
            System.out.println("Decision: Task " + task.id + " has to wait in the queue.");
            waitingQueue.offer(task);
        }
        printSystemStatus();
    }

    private void handleTaskCompletion() {
        Iterator<Map.Entry<Integer, Task>> iterator = runningTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Task> entry = iterator.next();
            int endTime = entry.getKey();
            Task job = entry.getValue();

            if (currentTime == endTime) {
                System.out.println("Task " + job.id + " has completed at tick " + currentTime + ", releasing " + job.cpuRequirement + " CPUs.");
                availableCPUs += job.cpuRequirement;
                iterator.remove();
                checkWaitingQueue();
                printSystemStatus();
            }
        }
    }

    private void checkWaitingQueue() {
        Iterator<Task> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.cpuRequirement <= availableCPUs) {
                System.out.println("Task " + task.id + " from waiting queue is now able to run.");
                startTask(task);
                iterator.remove();
            } else {
                break; // If the first waiting job can't be scheduled, others won't be either
            }
        }
    }

    private void startTask(Task task) {
        runningTasks.put(currentTime + task.duration, task);
        availableCPUs -= task.cpuRequirement;
    }

    private void printSystemStatus() {
        System.out.println("Tick " + currentTime);
        System.out.println("Free CPUs: " + availableCPUs + ", Busy CPUs: " + (totalCPUs - availableCPUs));
        System.out.println("Running Tasks: " + runningTasks.values());
        System.out.println("Waiting Queue: " + waitingQueue);
        System.out.println("---------------------------------------------------------");
    }
}

public class JobSchedulerDemo {
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        scheduler.addTask(new Task(1, 1, 4, 8));
        scheduler.addTask(new Task(2, 2, 2, 12));
        scheduler.addTask(new Task(3, 3, 2, 12));
        scheduler.addTask(new Task(4, 4, 2, 12));

        scheduler.runSimulation();
    }
}
