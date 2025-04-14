package org.demo;
class Milestone {
    int taskId;
    int originalArrivalTime;
    int eventualArrivalTime;
    int cpuRequired;
    boolean transferred;
    int actualCompletionTimeStamp;
    int originalDuration;
    int actualDuration;
    double dataTransferred;
    double dataLoad;
    int transferCompletionTime;
    int actualWaitedTime;

    Milestone(Task task, int eventualArrivalTime, boolean transferred, double dataTransferred) {
        this.taskId = task.getId();
        this.originalArrivalTime = task.getOriginalArrivalTime();
        this.eventualArrivalTime = eventualArrivalTime;
        this.cpuRequired = task.getCpuRequirement();
        this.transferred = transferred;
        this.actualCompletionTimeStamp = task.getCompletionTimeStamp();
        this.originalDuration = task.getDuration();
        this.actualDuration = task.getCompletionTimeStamp() - task.getOriginalArrivalTime();
        this.dataTransferred = dataTransferred;
        this.dataLoad = task.getDataLoad();
        this.transferCompletionTime = task.getTransferCompletionTime();
        this.actualWaitedTime = task.getCurrentWaitTime();
    }
}
