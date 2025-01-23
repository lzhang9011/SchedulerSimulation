package org.example;

public class Job {
    private String jobId;
    private int size;
    private int estimatedCompletionTime;
    private int waitingTime;

    public Job(String jobId, int size, int estimatedCompletionTime) {
        this.jobId = jobId;
        this.size = size;
        this.estimatedCompletionTime = estimatedCompletionTime;
        this.waitingTime = 0;
    }

    // Getters and Setters
    public String getJobId() {
        return jobId;
    }

    public int getSize() {
        return size;
    }

    public int getEstimatedCompletionTime() {
        return estimatedCompletionTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
}
