package org.demo;

import java.util.HashMap;
import java.util.Map;

class Task {
    int id;
    int arrivalTime;
    int originalArrivalTime;
    int duration;
    int cpuRequirement;
    double dataLoad;
    int maxWaitTime;
    int currentWaitTime;
    int transferCompletionTime; // how long it took to transfer
    int completionTimeStamp;
    int transferStartTick;

    int ticksElapsedSinceTransferStarted;
    double dataTransferred;
    double cost;


    public Task(int id, int arrivalTime, int duration, int cpuRequirement, double dataLoad, double cost) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.originalArrivalTime = arrivalTime;
        this.duration = duration;
        this.cpuRequirement = cpuRequirement;
        this.dataLoad = dataLoad;
        this.maxWaitTime = 0;
        this.currentWaitTime = 0;
        this.transferCompletionTime = 0;
        this.completionTimeStamp = -1;
        this.transferStartTick = 0;

        this.ticksElapsedSinceTransferStarted = 0;
        this.dataTransferred = 0.0;
        this.cost = cost;
    }


    public int getId() {
        return this.id;
    }

    public int getDuration() {
        return duration;
    }
    public double getDataLoad() {return dataLoad; }
    public int getMaxWaitTime() {
        return maxWaitTime;
    }
    public void setMaxWaitTime(int tmp) {
        this.maxWaitTime = (int)(0.125 * this.cpuRequirement * this.duration); // transfer time
    }

    public void incrementWaitTime() {
        this.currentWaitTime++;
    }

    public int getCurrentWaitTime() {
        return currentWaitTime;
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

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
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

