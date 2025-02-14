package org.example;

import java.util.*;

public class Cluster {
    private int numOfCPUs;
    private int cpuAvailable;
    private int regionID;
    private int storage;
    private int currentTotalData;
    private double executionCostPerMin;
    private int bandwidth;
    private double processCapability; //GB per tick, per CPU.
    private final PriorityQueue<Job> eventQueue = new PriorityQueue<>(Comparator.comparingInt(j -> j.getArrivalTime()));
    private final Queue<Job> waitingQueue = new LinkedList<>();
    private final Map<Integer, Job> runningJobs = new HashMap<>();

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
    }

    public void addJob(Job job) {
        eventQueue.offer(job);
    }

    public void addToWaitingQueue(Job job) {
        waitingQueue.add(job);
    }
    public void addToRunningQueue(Job job, int currentTime) {
        this.runningJobs.put(currentTime + job.getDuration(), job);
    }

    public Map<Integer, Job> getRunningJobs() {
        return runningJobs;
    }

    public Queue<Job> getWaitingQueue() {
        return waitingQueue;
    }

    public PriorityQueue<Job> getEventQueue() {
        return eventQueue;
    }

    public void releaseCPU(int num) {
        this.cpuAvailable += num;
    }
    public void allocateCPU(int num) {
        this.cpuAvailable -= num;
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
                '}';
    }
}
