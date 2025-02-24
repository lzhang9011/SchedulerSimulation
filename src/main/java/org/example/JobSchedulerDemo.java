package org.example;

import java.util.*;

class TransferInfo {
    int progress;
    int startTick;

    public TransferInfo(int startTick) {
        this.progress = 0;
        this.startTick = startTick;
    }
}

class Task {
    int id;
    int arrivalTime;
    int duration;
    int cpuRequirement;
    int dataLoad;
    int maxWaitTime;
    int currentWaitTime;
    int transferCompletionTime;

    public Task(int id, int arrivalTime, int duration, int cpuRequirement, int dataLoad) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.cpuRequirement = cpuRequirement;
        this.dataLoad = dataLoad;
//        this.maxWaitTime = duration * cpuRequirement;
        this.maxWaitTime = 2;
        this.currentWaitTime = 0;
        this.transferCompletionTime = 0;

    }


    public int getId() {
        return this.id;
    }
    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public void incrementWaitTime() {
        this.currentWaitTime++;
    }

    public int getTransferCompletionTime(){
        return this.transferCompletionTime;
    }
    public void setTransferCompletionTime(int transferCompletionTime){
        this.transferCompletionTime = transferCompletionTime;
    }

    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }
    @Override
    public String toString() {
        return "Task " + id + " [Arrival: " + arrivalTime
                + ", Duration: " + duration
                + ", CPU: " + cpuRequirement
                + ", DataLoad: " + dataLoad
                + ", Waited: " + currentWaitTime + "/" + maxWaitTime + " ticks]";
    }
}

class Datacenter {
    private final int totalCPUs = 16;
    private int availableCPUs = totalCPUs;
    private final PriorityQueue<Task> eventQueue = new PriorityQueue<>(Comparator.comparingInt(j -> j.arrivalTime));
    private final Queue<Task> waitingQueue = new LinkedList<>();
    private final Map<Task, Integer> runningTasks = new HashMap<>();
    private final Map<Task, TransferInfo> transferProgressTracker = new HashMap<>();

    public void addTask(Task task) {
        eventQueue.offer(task);
    }

    public PriorityQueue<Task> getEventQueue() {
        return eventQueue;
    }

    public boolean hasPendingTasks() {
        return !eventQueue.isEmpty() || !runningTasks.isEmpty();
    }
    public boolean isTransferComplete() {
        return transferProgressTracker.isEmpty();
    }

    // traverse waitingTask queue, increment by 1 for all waiting tasks.  if any task has exceeded max wait time, move to outgoing Queue.
    public void moveTaskToTracker(int currentTime) {

        Iterator<Task> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Task waitingTask = iterator.next();
            waitingTask.incrementWaitTime();

            if (waitingTask.currentWaitTime > waitingTask.getMaxWaitTime()) {
                System.out.println("Task " + waitingTask.id + " has exceeded max wait time and is moved to outgoing queue.");
//                outgoingQueue.add(waitingTask);
                transferProgressTracker.put(waitingTask, new TransferInfo(currentTime)); // then as soon as it was put into the transfer list, 1 portion of the job get transferred.
                iterator.remove();
            }
        }
    }

    public List<Task> updateTransferProgress(int currentTime, int bandwidth) {
        List<Task> completedTransfers = new ArrayList<>();

        if (transferProgressTracker.isEmpty()) return completedTransfers;
        int currentJobsInTransfer = transferProgressTracker.size();

        Iterator<Map.Entry<Task, TransferInfo>> iterator = transferProgressTracker.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Task, TransferInfo> entry = iterator.next();
            Task transferTask = entry.getKey();
            TransferInfo info = entry.getValue();
            int dataLoad = transferTask.dataLoad;

            if (info.progress == dataLoad) {
                int transferTime = currentTime - info.startTick;
                transferTask.setTransferCompletionTime(transferTime);
                System.out.println("✅ Transfer complete for Task " + transferTask.id + " at tick " + currentTime +
                        ". Took " + transferTime + " ticks.");
                completedTransfers.add(transferTask);
                iterator.remove(); // ✅ Remove completed transfers
            } else {
                info.progress += bandwidth;
                System.out.println("Transfer Progress: Task " + transferTask.id + " is at " +
                        info.progress + "/" + transferTask.dataLoad +
                        ". Current time: " + currentTime);
            }
        }
        return completedTransfers;
    }



    public void handleTaskArrival(int currentTime) {


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
        }
    }
    public void handleTaskCompletion(int currentTime) {

        Iterator<Map.Entry<Task, Integer>> iterator = runningTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Task, Integer> entry = iterator.next();
            int endTime = entry.getValue();
            Task task = entry.getKey();

            if (currentTime == endTime) {
                System.out.println("Task " + task.id + " has completed at tick " + currentTime + ", releasing " + task.cpuRequirement + " CPUs.");
                availableCPUs += task.cpuRequirement;
                iterator.remove();
                checkWaitingQueue(currentTime);
            }
        }
    }

    private void checkWaitingQueue(int currentTime) {
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

        runningTasks.put(task, currentTime + task.duration);
        availableCPUs -= task.cpuRequirement;
    }

    public void printSystemStatus(int currentTime) {
        System.out.println("Free CPUs: " + availableCPUs + ", Busy CPUs: " + (totalCPUs - availableCPUs));
        for (Map.Entry<Task, Integer> entry : runningTasks.entrySet()) {
            Task task = entry.getKey();
            int endTime = entry.getValue();
            System.out.println("Running List: Task " + task.id + " with End Time of: " + endTime);
        }
        System.out.println("Waiting Queue: " + waitingQueue);
        System.out.println("Transfer List: ");
        if (transferProgressTracker.isEmpty()) {
            System.out.println("No tasks are currently being transferred.");
        }

        for (Map.Entry<Task, TransferInfo> entry : transferProgressTracker.entrySet()) {
            Task task = entry.getKey();
            TransferInfo info = entry.getValue();

            System.out.println("Task " + task.getId() + "'s progress: " + info.progress + "/" + task.dataLoad);
        }

        System.out.println("---------------------------------------------------------");
    }
}

class Scheduler {
    private final Datacenter localDatacenter = new Datacenter();
    private final Datacenter remoteDatacenter = new Datacenter();
    private int currentTime = 0;
    private int bandwidth = 1;

    public void addTask(Task task) {
        // scheduler's addTask is to localDatacenter's eventQueue. This is the very INITIAL add task.
        localDatacenter.addTask(task);
    }

    public void runSimulation() {
        System.out.println("Simulation started.\n---------------------------------------------------------");

        while (localDatacenter.hasPendingTasks() || !localDatacenter.isTransferComplete()) {
            System.out.println("Current Time is: " + currentTime);
            List<Task> completedTransfers = localDatacenter.updateTransferProgress(currentTime, bandwidth);
            for (Task task : completedTransfers) {
                task.setArrivalTime(currentTime);
                remoteDatacenter.addTask(task);
                System.out.println("Task " + task.id + " has arrived at remoteDatacenter's eventQueue.");
            }

            localDatacenter.moveTaskToTracker(currentTime);//increment waitTime for waiting Tasks.
            localDatacenter.handleTaskArrival(currentTime);
            localDatacenter.handleTaskCompletion(currentTime);
            localDatacenter.printSystemStatus(currentTime);
            currentTime++;
        }

        System.out.println(remoteDatacenter.getEventQueue());
        System.out.println("\nSimulation complete.");
    }

}

public class JobSchedulerDemo {
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        Task task1 = new Task(1,1,5,8,2);
        Task task2 = new Task(2,2,2,12,4);
//        Task task3 = new Task(3,3,2,12,1);

        //add to local datacenter's eventQueue.
        scheduler.addTask(task1);
        scheduler.addTask(task2);
//        scheduler.addTask(task3);

        scheduler.runSimulation();
    }
}
