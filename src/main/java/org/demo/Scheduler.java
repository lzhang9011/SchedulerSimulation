package org.demo;

import java.util.ArrayList;
import java.util.List;

class Scheduler {
    private final Datacenter localDatacenter = new Datacenter(16);
    private final Datacenter remoteDatacenter = new Datacenter(64);
    private final TaskMilestone milestoneTracker = new TaskMilestone();
    private int currentTime = 0;
    private int bandwidth = 1;
    private List<Task> tasksEverExisted = new ArrayList<>();

    public void addTask(Task task) {
        // scheduler's addTask is to localDatacenter's eventQueue. This is the very INITIAL add task.
        localDatacenter.addTask(task);
        tasksEverExisted.add(task);
    }


    public void runSimulation() {
        System.out.println("Simulation started.\n---------------------------------------------------------");

        while (localDatacenter.hasPendingTasks() || !localDatacenter.isTransferComplete() || remoteDatacenter.hasPendingTasks() || !remoteDatacenter.isTransferComplete()) {
            System.out.println("Current Time is: " + currentTime);
            List<Task> completedTransfers = localDatacenter.updateTransferProgress(currentTime, bandwidth);
            for (Task task : completedTransfers) {
                task.setArrivalTime(currentTime);
//                milestoneTracker.recordMilestone(task, currentTime, true);
                remoteDatacenter.addTask(task);
                System.out.println("Task " + task.id + " has arrived at remoteDatacenter's eventQueue.");
            }
            localDatacenter.moveTaskToTracker(currentTime);//increment waitTime for waiting Tasks.
            /*
             * add milestoneTracker instance
             * because both methods --handleTaskArrival() and handleTaskCompletion()
             * will call startTask(), which will check if the task is transferred or not
             * */
            localDatacenter.handleTaskArrival(currentTime, milestoneTracker);
            localDatacenter.handleTaskCompletion(currentTime, milestoneTracker);
            localDatacenter.printSystemStatus(currentTime);

            remoteDatacenter.handleTaskArrival(currentTime, milestoneTracker);
            remoteDatacenter.handleTaskCompletion(currentTime, milestoneTracker);
            remoteDatacenter.printSystemStatus(currentTime);

            currentTime++;
        }

        // recording milestones of tasks
        for (Task task : tasksEverExisted) {
            if (task.getOriginalArrivalTime() + task.getMaxWaitTime() >= currentTime) {
                milestoneTracker.recordMilestone(task, task.getArrivalTime(), false);
            } else {
                // remote task milestone tracking:
                milestoneTracker.recordMilestone(task, task.getArrivalTime(), true);
            }
        }


        // Write milestones to CSV
        milestoneTracker.writeToCSV("task_milestones.csv");
        System.out.println(remoteDatacenter.getEventQueue());

        System.out.println("\nSimulation complete.");
    }

}
