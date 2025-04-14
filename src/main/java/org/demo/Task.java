package org.demo;

class Task {
    private int id;
    private int arrivalTime;
    private int originalArrivalTime;
    private int duration;
    private int cpuRequirement;
    private double dataLoad;
    private int maxWaitTime;
    private int currentWaitTime;//how many time ticks a job has waited in the waitingQueue
    private int transferCompletionTime; // how long it took to transfer
    private int completionTimeStamp;
    private int transferStartTick;

    private int ticksElapsedSinceTransferStarted;
    private double dataTransferred;


    public Task(int id, int arrivalTime, int duration, int cpuRequirement, double dataLoad, int maxWaitTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.originalArrivalTime = arrivalTime;
        this.duration = duration;
        this.cpuRequirement = cpuRequirement;
        this.dataLoad = dataLoad;
        this.maxWaitTime = maxWaitTime;
        this.currentWaitTime = 0;
        this.transferCompletionTime = 0;
        this.completionTimeStamp = 0;
        this.transferStartTick = 0;

        this.ticksElapsedSinceTransferStarted = 0;
        this.dataTransferred = 0.0;
    }


    public int getId() {
        return this.id;
    }

    public int getDuration() {
        return duration;
    }

    public int getCpuRequirement() {
        return this.cpuRequirement;
    }
    public double getDataLoad() {return dataLoad; }
    public int getMaxWaitTime() {
        return maxWaitTime;
    }
    public void setMaxWaitTime(int tmp) {
//        this.maxWaitTime = (int)(0.125 * this.cpuRequirement * this.duration / 1.2); // transfer time

//        this.maxWaitTime = (int)(0.125 * this.cpuRequirement * this.duration / 1.2) + tmp + new Random().nextInt(5) + 1; // transfer time
        this.maxWaitTime = Math.max(0,
                (int)((0.09 * this.cpuRequirement * this.duration) - 6000) // decrease the impact of high waitTime
        );

    }

    public void incrementWaitTime() {
        this.currentWaitTime++;
    }

    public int getCurrentWaitTime() {
        return currentWaitTime;
    }

    public void setCurrentWaitTime(int currentTime) {
        this.currentWaitTime = currentTime;
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

    public int getTicksElapsedSinceTransferStarted() {
        return this.ticksElapsedSinceTransferStarted;
    }
    public void incrementTicksElapsedSinceTransferStarted() {
        this.ticksElapsedSinceTransferStarted++;
    }

    public double getDataTransferred() {
        return this.dataTransferred;
    }

    public void incrementDataTransferred(double dataTransferred) {
        this.dataTransferred += dataTransferred;
    }

    public void setDataTransferred(double dataTransferred) {
        this.dataTransferred = dataTransferred;
    }

    public int getTransferStartTick() {
        return this.transferStartTick;
    }

    public void setTransferStartTick(int currentTime) {
        this.transferStartTick = currentTime;
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

