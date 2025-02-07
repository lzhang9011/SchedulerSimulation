package org.example;

public class Job implements Comparable<Job> {
    private int duration;
    private final int resourceRequirement;
    private int elapsedTime;
    private final int arrivalTime;
    private final double dataLoad;

    public Job(int duration, int resourceRequirement, int arrivalTime, double dataLoad) {
        this.duration = duration;
        this.resourceRequirement = resourceRequirement;
        this.elapsedTime = 0;
        this.arrivalTime = arrivalTime;
        this.dataLoad = dataLoad;
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

    public double getDataLoad() {
        return dataLoad;
    }

    @Override
    public int compareTo(Job other) {
        return Integer.compare(this.arrivalTime, other.arrivalTime);
    }

    @Override
    public String toString() {
        return "Job{" +
                "duration=" + duration +
                ", resourceRequirement=" + resourceRequirement +
                ", elapsedTime=" + elapsedTime +
                ", arrivalTime=" + arrivalTime +
                ", dataLoad=" + dataLoad +
                '}';
    }
}
