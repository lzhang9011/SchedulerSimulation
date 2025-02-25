package org.demo;

class Task {
    int id;
    int arrivalTime;
    int originalArrivalTime;
    int duration;
    int cpuRequirement;
    int dataLoad;
    int maxWaitTime;
    int currentWaitTime;
    int transferCompletionTime;
    int completionTimeStamp;


    public Task(int id, int arrivalTime, int duration, int cpuRequirement, int dataLoad) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.originalArrivalTime = arrivalTime;
        this.duration = duration;
        this.cpuRequirement = cpuRequirement;
        this.dataLoad = dataLoad;
//        this.maxWaitTime = duration * cpuRequirement;
        this.maxWaitTime = 2;
        this.currentWaitTime = 0;
        this.transferCompletionTime = 0;
        this.completionTimeStamp = -1;

    }


    public int getId() {
        return this.id;
    }

    public int getDuration() {
        return duration;
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
    public int getOriginalArrivalTime() {
        return originalArrivalTime;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }

    public int getCompletionTimeStamp() {
        return completionTimeStamp;
    }

    public void setCompletionTimeStamp(int completionTimeStamp) {
        this.completionTimeStamp = completionTimeStamp;
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

