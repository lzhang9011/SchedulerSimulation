package org.demo;

import java.util.*;

class Datacenter {
    private final int id;
    private final int totalCPUs;
    private int availableCPUs;
    private final Queue<Task> eventQueue = new LinkedList<>();;
    private final Queue<Task> waitingQueue = new LinkedList<>();
    private final Map<Task, Integer> runningTasks = new HashMap<>();
    private final List<Task> transferTasks = new ArrayList<>();

    public Datacenter(int id, int totalCPUs) {
        this.id = id;
        this.totalCPUs = totalCPUs;
        this.availableCPUs = totalCPUs;
    }

    public int getTotalCPUs() {
        return totalCPUs;
    }
    public void addTask(Task task) {
        eventQueue.offer(task);
    }

    public int numOftransferTasks() {
        return transferTasks.size();
    }

    public Queue<Task> getEventQueue() {
        return eventQueue;
    }

    public boolean hasPendingTasks() {

        if (eventQueue.isEmpty() && runningTasks.isEmpty() && waitingQueue.isEmpty()) {
            // 都为空，返回false;
            return false;
        } else {
            //但凡有一个list不为空，返回true,
            return true;
        }
    }
    public boolean hasUnfinishedTransfers() {
        // 如果列表为空，代表没有未完成的任务
        if (transferTasks.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    // traverse waitingTask queue, increment by 1 for all waiting tasks.  if any task has exceeded max wait time, move to outgoing Queue.
    public void moveTaskToTracker(int currentTime) {

        Iterator<Task> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Task waitingTask = iterator.next();
            waitingTask.incrementWaitTime(); // waiting locally

            // start the transfer
            if (waitingTask.getCurrentWaitTime() > waitingTask.getMaxWaitTime()) {
//                System.out.println("Task " + waitingTask.id + " has exceeded max wait time and is moved to outgoing queue.");
                // waitingTask should be moved to the transferTasks list.
                transferTasks.add(waitingTask);
                waitingTask.setTransferStartTick(currentTime);

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

            if (transferTask.getDataTransferred() >= transferTask.getDataLoad()) {
                transferTask.setDataTransferred(transferTask.getDataLoad()); // ensure the dataTransferred do not exceed total dataLoad.

                int transferTime = currentTime - transferTask.getTransferStartTick();

                transferTask.setTransferCompletionTime(transferTime);
                completedTransfers.add(transferTask);
                iterator.remove();
//                System.out.println("✅ Transfer complete for Task " + transferTask.id + " at tick " + currentTime +
//                        ". Took " + transferTime + " ticks.");
            } else {
//                System.out.println("Transfer Progress: Task " + transferTask.id + " is at " +
//                        transferTask.dataTransferred + "/" + transferTask.dataLoad +
//                        ". Current time: " + currentTime);
            }
        }

        return completedTransfers;
    }



    public void handleTaskArrival(int currentTime, TaskMilestone milestoneTracker) {

        if (!eventQueue.isEmpty() && eventQueue.peek().getArrivalTime() <= currentTime) {
            Task task = eventQueue.poll();
//            System.out.println("Task " + task.id + " has arrived at tick " + currentTime + ", requiring " + task.cpuRequirement + " CPUs for " + task.duration + " ticks.");
            if (task.getCpuRequirement() <= availableCPUs) {
//                System.out.println("Decision: Task " + task.id + " can run immediately.");
                startTask(task, currentTime, milestoneTracker);
            } else {
//                System.out.println("Decision: Task " + task.id + " has to wait in the queue.");
                // job starts waiting
                task.setCurrentWaitTime(currentTime);
                waitingQueue.offer(task);
            }
        }
    }
    // original version of handleTaskCompletion()
//    public void handleTaskCompletion(int currentTime, TaskMilestone milestoneTracker) {
//        Iterator<Map.Entry<Task, Integer>> iterator = runningTasks.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Task, Integer> entry = iterator.next();
//            int endTime = entry.getValue();
//            Task task = entry.getKey();
//            if (currentTime == endTime) {
//                task.setCompletionTimeStamp(currentTime);
////                System.out.println("Task " + task.id + " has completed at tick " + currentTime + ", releasing " + task.cpuRequirement + " CPUs. Progress: 100.00%");
//
//                availableCPUs += task.cpuRequirement;
//                iterator.remove();
//                checkWaitingQueue(currentTime, milestoneTracker);
//            }
//        }
//    }

    // ?? could alleviate CME error?
    public void handleTaskCompletion(int currentTime, TaskMilestone milestoneTracker) {
        List<Task> completedTasks = new ArrayList<>(); // Collect tasks to remove

        for (Map.Entry<Task, Integer> entry : runningTasks.entrySet()) {
            int endTime = entry.getValue();
            Task task = entry.getKey();
            if (currentTime == endTime) {
                task.setCompletionTimeStamp(currentTime);
                availableCPUs += task.getCpuRequirement();
                completedTasks.add(task); // Store task for removal
            }
        }

        // Remove completed tasks outside the iteration loop
        for (Task task : completedTasks) {
            runningTasks.remove(task);
        }

        // Now, safely check the waiting queue
        checkWaitingQueue(currentTime, milestoneTracker);
    }


    private void checkWaitingQueue(int currentTime, TaskMilestone milestoneTracker) {
        Iterator<Task> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();

            if (task.getCpuRequirement() <= availableCPUs) {
//                System.out.println("Task " + task.id + " from waiting queue is now able to run.");
                startTask(task, currentTime, milestoneTracker);
                iterator.remove();
            }
        }
    }

    private void startTask(Task task, int currentTime, TaskMilestone milestoneTracker) {

//        System.out.println("Task " + task.id + " is starting execution after waiting " + task.currentWaitTime + " ticks and the transferTime is " + task.transferCompletionTime);
        runningTasks.put(task, currentTime + task.getDuration());
        availableCPUs -= task.getCpuRequirement();
    }

    public void printSystemStatus(int currentTime) {

        System.out.println("Free CPUs: " + availableCPUs + ", Busy CPUs: " + (totalCPUs - availableCPUs));
        System.out.println("Running List:");
        for (Map.Entry<Task, Integer> entry : runningTasks.entrySet()) {
            Task task = entry.getKey();
            int endTime = entry.getValue();

            int timeElapsed = currentTime - task.getOriginalArrivalTime() - task.getCurrentWaitTime() - task.getTransferCompletionTime(); // Time since the task started

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
            System.out.println("Transfer Progress: Task " + transferTask.getId() + " is at " +
                    transferTask.getDataTransferred() + "/" + transferTask.getDataLoad() +
                    ". Current time: " + currentTime);
        }

        System.out.println("---------------------------------------------------------");
    }

    @Override
    public String toString() {
        return "Datacenter " + id + " has a total cpus of " + totalCPUs;
    }

}
