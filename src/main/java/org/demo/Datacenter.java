package org.demo;

import java.util.*;

class Datacenter {
    private final int id;
    private final int totalCPUs;
    private int availableCPUs;
    private final PriorityQueue<Task> eventQueue = new PriorityQueue<>(Comparator.comparingInt(j -> j.arrivalTime));
    private final Queue<Task> waitingQueue = new LinkedList<>();
    private final Map<Task, Integer> runningTasks = new HashMap<>();
    private final List<Task> transferTasks = new ArrayList<>();

    public Datacenter(int id, int totalCPUs) {
        this.id = id;
        this.totalCPUs = totalCPUs;
        this.availableCPUs = totalCPUs;
    }

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
        return transferTasks.isEmpty();
    }

    // traverse waitingTask queue, increment by 1 for all waiting tasks.  if any task has exceeded max wait time, move to outgoing Queue.
    public void moveTaskToTracker(int currentTime) {

        Iterator<Task> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Task waitingTask = iterator.next();
            waitingTask.incrementWaitTime();

            if (waitingTask.currentWaitTime > waitingTask.getMaxWaitTime()) {
                System.out.println("Task " + waitingTask.id + " has exceeded max wait time and is moved to outgoing queue.");
                // waitingTask should be moved to the transferTasks list.
                transferTasks.add(waitingTask);
                waitingTask.transferStartTick = currentTime;

                iterator.remove();
            }
        }
    }

    public List<Task> updateTransferProgress(int currentTime, double bandwidth) {
        List<Task> completedTransfers = new ArrayList<>();

        if (transferTasks.isEmpty()) return completedTransfers;

        int activeTaskCount = transferTasks.size();
        double bandwidthPerTask = bandwidth / activeTaskCount;

        Iterator<Task> iterator = transferTasks.iterator();
        while (iterator.hasNext()) {
            Task transferTask = iterator.next();

            transferTask.incrementTicksElapsedSinceTransferStarted();//update time ticks for transfer
            transferTask.incrementDataTransferred(bandwidthPerTask);//update data transferred

            if (transferTask.getDataTransferred() == transferTask.getDataLoad()) {
                transferTask.setDataTransferred(transferTask.getDataLoad()); // ensure the datatransferred do not exceed total dataLoad.
                int transferTime = currentTime - transferTask.transferStartTick;
                transferTask.setTransferCompletionTime(transferTime);
                completedTransfers.add(transferTask);
                iterator.remove();
                System.out.println("✅ Transfer complete for Task " + transferTask.id + " at tick " + currentTime +
                        ". Took " + transferTime + " ticks.");
            } else {
//                System.out.println("Transfer Progress: Task " + transferTask.id + " is at " +
//                        transferTask.dataTransferred + "/" + transferTask.dataLoad +
//                        ". Current time: " + currentTime);
            }
        }

        return completedTransfers;
    }



    public void handleTaskArrival(int currentTime, TaskMilestone milestoneTracker) {

        if (!eventQueue.isEmpty() && eventQueue.peek().arrivalTime == currentTime) {
            Task task = eventQueue.poll();
            System.out.println("Task " + task.id + " has arrived at tick " + currentTime + ", requiring " + task.cpuRequirement + " CPUs for " + task.duration + " ticks.");
            if (task.cpuRequirement <= availableCPUs) {
                System.out.println("Decision: Task " + task.id + " can run immediately.");
                startTask(task, currentTime, milestoneTracker);
            } else {
                System.out.println("Decision: Task " + task.id + " has to wait in the queue.");
                task.currentWaitTime = 0;
                waitingQueue.offer(task);
            }
        }
    }
    public void handleTaskCompletion(int currentTime, TaskMilestone milestoneTracker) {
        Iterator<Map.Entry<Task, Integer>> iterator = runningTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Task, Integer> entry = iterator.next();
            int endTime = entry.getValue();
            Task task = entry.getKey();
            if (currentTime == endTime) {
                task.setCompletionTimeStamp(currentTime);
                System.out.println("Task " + task.id + " has completed at tick " + currentTime + ", releasing " + task.cpuRequirement + " CPUs. Progress: 100.00%");

                availableCPUs += task.cpuRequirement;
                iterator.remove();
                checkWaitingQueue(currentTime, milestoneTracker);
            }
        }
    }

    private void checkWaitingQueue(int currentTime, TaskMilestone milestoneTracker) {
        Iterator<Task> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();

            if (task.cpuRequirement <= availableCPUs) {
                System.out.println("Task " + task.id + " from waiting queue is now able to run.");
                startTask(task, currentTime, milestoneTracker);
                iterator.remove();
            }
        }
    }

    private void startTask(Task task, int currentTime, TaskMilestone milestoneTracker) {

        System.out.println("Task " + task.id + " is starting execution after waiting " + task.currentWaitTime + " ticks and the transferTime is " + task.transferCompletionTime);
        runningTasks.put(task, currentTime + task.duration);
        availableCPUs -= task.cpuRequirement;
    }

    public void printSystemStatus(int currentTime) {

        System.out.println("Free CPUs: " + availableCPUs + ", Busy CPUs: " + (totalCPUs - availableCPUs));
        System.out.println("Running List:");
        for (Map.Entry<Task, Integer> entry : runningTasks.entrySet()) {
            Task task = entry.getKey();
            int endTime = entry.getValue();

            int timeElapsed = currentTime - task.getOriginalArrivalTime() - task.currentWaitTime - task.getTransferCompletionTime(); // Time since the task started

            System.out.println("Task " + task.getId() + " who supposes to finish at " + endTime + " is at progress: " + timeElapsed + "/" + task.getDuration());

        }

        System.out.println("Waiting Queue: " + waitingQueue);
        System.out.println("Transfer List: ");
        if (transferTasks.isEmpty()) {
            System.out.println("No tasks are currently being transferred.");
        }

        for (Task transferTask : transferTasks) {
            System.out.println("Task " + transferTask.getId() + " is being transferred");
//            System.out.println("");//current data transferred / total data Load
            System.out.println("Transfer Progress: Task " + transferTask.id + " is at " +
                        transferTask.dataTransferred + "/" + transferTask.dataLoad +
                        ". Current time: " + currentTime);
        }

        System.out.println("---------------------------------------------------------");
    }

    @Override
    public String toString() {
        return "Datacenter " + id + " has a total cpus of " + totalCPUs;
    }

}
