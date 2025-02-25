package org.demo;

import java.util.*;

class Datacenter {
    private final int totalCPUs;
    private int availableCPUs;
    private final PriorityQueue<Task> eventQueue = new PriorityQueue<>(Comparator.comparingInt(j -> j.arrivalTime));
    private final Queue<Task> waitingQueue = new LinkedList<>();
    private final Map<Task, Integer> runningTasks = new HashMap<>();
    private final Map<Task, TransferInfo> transferProgressTracker = new HashMap<>();

    public Datacenter(int totalCPUs) {
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
                System.out.println("Task " + task.id + " has completed at tick " + currentTime + ", releasing " + task.cpuRequirement + " CPUs. Progress: 100.00% (" + task.dataLoad + "/" + task.dataLoad + ")");

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

        for (Map.Entry<Task, Integer> entry : runningTasks.entrySet()) {
            Task task = entry.getKey();
            int endTime = entry.getValue();

            int timeElapsed = currentTime - task.arrivalTime; // Time since the task started
            double progress = (double) timeElapsed / task.duration; // Progress ratio

            progress = Math.max(0, Math.min(progress, 1));

            System.out.printf("Running List: Task %d with End Time of: %d | Progress: %.2f%% (%d/%d ticks)\n",
                    task.id, endTime, progress * 100, timeElapsed, task.duration);
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
