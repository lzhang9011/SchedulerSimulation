package org.example;

import java.util.PriorityQueue;

public class Cluster {
    private int numOfCPUs;
    private int cpuAvailable;
    private int regionID;
    private int storage;
    private int currentTotalData;
    private double executionCostPerMin;
    private int bandwidth;
    private double processCapability; //GB per tick, per CPU.
    private PriorityQueue<Job> jobQueue;

    public Cluster(int numOfCPUs, int regionID, int storage, int currentTotalData,
                   double executionCostPerMin, int bandwidth, double processCapability) {
        this.numOfCPUs = numOfCPUs;
        this.cpuAvailable = numOfCPUs;
        this.regionID = regionID;
        this.storage = storage;
        this.currentTotalData = currentTotalData;
        this.executionCostPerMin = executionCostPerMin;
        this.bandwidth = bandwidth;
        this.processCapability = processCapability;
        this.jobQueue = new PriorityQueue<>();
    }

    public void addJob(Job job) {
        jobQueue.offer(job);
        System.out.println("Added: " + job);
    }

    public Job getNextJob() {
        return jobQueue.poll();
    }

    public boolean hasPendingJobs() {
        return !jobQueue.isEmpty();
    }

    public boolean allocateCPU(int requiredCPUs) {
        if (requiredCPUs <= cpuAvailable) {
            cpuAvailable -= requiredCPUs;
            return true;
        }
        return false;
    }

    public void releaseCPU(int releasedCPUs) {
        cpuAvailable = Math.min(cpuAvailable + releasedCPUs, numOfCPUs);
    }

    public int getNumOfCPUs() { return numOfCPUs; }
    public int getCpuAvailable() { return cpuAvailable; }
    public int getRegionID() { return regionID; }
    public int getStorage() { return storage; }
    public int getCurrentTotalData() { return currentTotalData; }
    public double getExecutionCostPerMin() { return executionCostPerMin; }
    public int getBandwidth() { return bandwidth; }
    public double getProcessCapability() {return processCapability; }

    @Override
    public String toString() {
        return "Cluster{" +
                "numOfCPUs=" + numOfCPUs +
                ", cpuAvailable=" + cpuAvailable +
                ", regionID=" + regionID +
                ", storage=" + storage +
                ", currentTotalData=" + currentTotalData +
                ", executionCostPerMin=" + executionCostPerMin +
                ", bandwidth=" + bandwidth +
                ", processCapability=" + processCapability +
                ", jobQueueSize=" + jobQueue.size() +
                '}';
    }
}
