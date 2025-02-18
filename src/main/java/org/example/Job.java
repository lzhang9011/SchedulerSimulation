package org.example;

public class Job implements Comparable<Job> {
    private int jobID;
    private int duration;
    private final int resourceRequirement;
    private int elapsedTime;
    private int arrivalTime;
    private final double dataLoad;
    private int maxWaitTime;
    private int currentWaitTime;

    public Job(int jobID, int duration, int resourceRequirement, int arrivalTime, double dataLoad) {
        this.jobID = jobID;
        this.duration = duration;
        this.resourceRequirement = resourceRequirement;
        this.elapsedTime = 0;
        this.arrivalTime = arrivalTime;
        this.dataLoad = dataLoad;
        this.maxWaitTime = 2; // TODO change to Starburst's formula
        this.currentWaitTime = 0;
    }

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getResourceRequirement() {
        return resourceRequirement;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void incrementElapsedTime() {
        this.elapsedTime++;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getDataLoad() {
        return dataLoad;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }
    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public int getCurrentWaitTime() {
        return currentWaitTime;
    }
    public void setCurrentWaitTime(int currentWaitTime) {
        this.currentWaitTime = currentWaitTime;
    }
    public void incrementWaitTime() { this.currentWaitTime++; }

    @Override
    public int compareTo(Job other) {
        return Integer.compare(this.arrivalTime, other.arrivalTime);
    }

    @Override
    public String toString() {
        return "Job " + jobID + " [Arrival: " + arrivalTime + ", Duration: " + duration + ", CPU: " + resourceRequirement + ", Waited: " + currentWaitTime + "/" + maxWaitTime + " ticks]";
    }
}
