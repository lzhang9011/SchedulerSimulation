package org.example;

import java.util.*;

class Task {
    int id;
    int arrivalTime;
    int duration;
    int cpuRequirement;
    int dataLoad;
    int maxWaitTime;
    int currentWaitTime;

    public Task(int id, int arrivalTime, int duration, int cpuRequirement, int dataLoad) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.cpuRequirement = cpuRequirement;
        this.dataLoad = dataLoad;
//        this.maxWaitTime = duration * cpuRequirement;
        this.maxWaitTime = 2;
        this.currentWaitTime = 0;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public void incrementWaitTime() {
        this.currentWaitTime++;
    }

    @Override
    public String toString() {
        return "Task " + id + " [Arrival: " + arrivalTime + ", Duration: " + duration + ", CPU: " + cpuRequirement + ", Waited: " + currentWaitTime + "/" + maxWaitTime + " ticks]";
    }
}

class Datacenter {
    private final int totalCPUs = 16;
    private int availableCPUs = totalCPUs;
    private int bandwidth = 1;
    private final PriorityQueue<Task> eventQueue = new PriorityQueue<>(Comparator.comparingInt(j -> j.arrivalTime));
    private final Queue<Task> waitingQueue = new LinkedList<>();
    private final Map<Integer, Task> runningTasks = new HashMap<>();
    private final List<Task> outgoingQueue = new ArrayList<>();



    public void addTask(Task task) {
        eventQueue.offer(task);
    }

    public boolean hasPendingTasks() {
        return !eventQueue.isEmpty() || !runningTasks.isEmpty();
    }

    private void handleTaskMovingToOutgoingQueue(int currentTime) {
        Iterator<Task> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Task waitingTask = iterator.next();
            waitingTask.incrementWaitTime();

            if (waitingTask.currentWaitTime > waitingTask.getMaxWaitTime()) {
                System.out.println("Task " + waitingTask.id + " has exceeded max wait time and is moved to outgoing queue.");
                outgoingQueue.add(waitingTask);
                iterator.remove();
                printSystemStatus(currentTime);
            }
        }
    }

    public void handleTaskArrival(int currentTime) {
        handleTaskMovingToOutgoingQueue(currentTime);

        if (!eventQueue.isEmpty() && eventQueue.peek().arrivalTime == currentTime) {
            Task task = eventQueue.poll();
            System.out.println("Task " + task.id + " has arrived at tick " + currentTime + ", requiring " + task.cpuRequirement + " CPUs for " + task.duration + " ticks.");
            if (task.cpuRequirement <= availableCPUs) {
                System.out.println("Decision: Task " + task.id + " can run immediately.");
                startTask(task, currentTime);
            } else {
                System.out.println("Decision: Task " + task.id + " has to wait in the queue.");
                task.currentWaitTime = 0;
                waitingQueue.offer(task);
            }
            printSystemStatus(currentTime);
        }
    }
    public void handleTaskCompletion(int currentTime) {
        Iterator<Map.Entry<Integer, Task>> iterator = runningTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Task> entry = iterator.next();
            int endTime = entry.getKey();
            Task task = entry.getValue();

            if (currentTime == endTime) {
                System.out.println("Task " + task.id + " has completed at tick " + currentTime + ", releasing " + task.cpuRequirement + " CPUs.");
                availableCPUs += task.cpuRequirement;
                iterator.remove();
                checkWaitingQueue(currentTime);
                printSystemStatus(currentTime);
            }
        }
    }

    private void checkWaitingQueue(int currentTime) {
        handleTaskMovingToOutgoingQueue(currentTime);
        Iterator<Task> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();

            if (task.cpuRequirement <= availableCPUs) {
                System.out.println("Task " + task.id + " from waiting queue is now able to run.");
                startTask(task, currentTime);
                iterator.remove();
            }
        }
    }

    private void startTask(Task task, int currentTime) {
        System.out.println("Task " + task.id + " is starting execution after waiting " + task.currentWaitTime + " ticks.");

        runningTasks.put(currentTime + task.duration, task);
        availableCPUs -= task.cpuRequirement;
    }

    private void printSystemStatus(int currentTime) {
        System.out.println("Tick " + currentTime);
        System.out.println("Free CPUs: " + availableCPUs + ", Busy CPUs: " + (totalCPUs - availableCPUs));
        System.out.println("Running Tasks: " + runningTasks.values());
        System.out.println("Waiting Queue: " + waitingQueue);
        System.out.println("Outgoing Queue: " + outgoingQueue);
        System.out.println("---------------------------------------------------------");
    }
}

class Scheduler {
    private final Datacenter localDatacenter = new Datacenter();
    private final Datacenter remoteDatacenter = new Datacenter();
    private int currentTime = 0;

    public void addTask(Task task) {
        // add task to localDatacenter's eventQueue.
        localDatacenter.addTask(task);
    }

    public void runSimulation() {
        System.out.println("Simulation started.\n---------------------------------------------------------");

        while (localDatacenter.hasPendingTasks() || remoteDatacenter.hasPendingTasks()) {
            // Handle arrivals and completions in both datacenters
            localDatacenter.handleTaskArrival(currentTime);
//            remoteDatacenter.handleTaskArrival(currentTime);

            localDatacenter.handleTaskCompletion(currentTime);
//            remoteDatacenter.handleTaskCompletion(currentTime);

            currentTime++;
        }

        System.out.println("\nSimulation complete.");
    }





}

public class JobSchedulerDemo {
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        scheduler.addTask(new Task(1, 1, 5, 8, 2));
        scheduler.addTask(new Task(2, 2, 2, 12,4));
        scheduler.addTask(new Task(3, 3, 2, 12,1));
//        scheduler.addTask(new Task(4, 4, 2, 12,2));

        scheduler.runSimulation();
    }
}
