package org.example;

import java.util.*;

public class Cluster {
    private int numOfCPUs;
    private int cpuAvailable;
    private int regionID;
    private int storage;
    private int currentTotalData;
    private double executionCostPerMin;
    private int bandwidth = 1;
    private double processCapability; //GB per tick, per CPU.
    private final PriorityQueue<Job> eventQueue = new PriorityQueue<>(Comparator.comparingInt(j -> j.getArrivalTime()));
    private final Queue<Job> waitingQueue = new LinkedList<>();
    private final Map<Integer, Job> runningJobs = new HashMap<>();
    private final Queue<Job> outgoingQueue = new LinkedList<>();

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

    public boolean hasPendingJobs() {
        return !eventQueue.isEmpty() || !runningJobs.isEmpty();
    }


    private void handleJobMovingToOutgoingQueue(int currentTime) {
//        if (waitingQueue == null || outgoingQueue == null) {
//            System.err.println("Error: waitingQueue or outgoingQueue is not initialized.");
//            return;
//        }
        Iterator<Job> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Job waitingJob = iterator.next();
            waitingJob.incrementWaitTime();

            if (waitingJob.getCurrentWaitTime() > waitingJob.getMaxWaitTime()) {
                System.out.println("Job " + waitingJob.getJobID() + " has exceeded max wait time and is moved to outgoing queue.");
                outgoingQueue.add(waitingJob);
                iterator.remove();
                printSystemStatus(currentTime);
            }
        }
    }

    public void handleJobArrival(int currentTime) {
        handleJobMovingToOutgoingQueue(currentTime);

        if (!eventQueue.isEmpty() && eventQueue.peek().getArrivalTime() == currentTime) {
            Job job = eventQueue.poll();
            System.out.println("Job " + job.getJobID() + " has arrived at tick " + currentTime + ", requiring " + job.getResourceRequirement() + " CPUs for " + job.getDuration() + " ticks.");
            if (job.getResourceRequirement() <= cpuAvailable) {
                System.out.println("Decision: Job " + job.getJobID() + " can run immediately.");
                startJob(job, currentTime);
            } else {
                System.out.println("Decision: Job " + job.getJobID() + " has to wait in the queue.");
                job.setCurrentWaitTime(0);
                waitingQueue.offer(job);
            }
            printSystemStatus(currentTime);
        }
    }

    public void handleJobCompletion(int currentTime) {
        Iterator<Map.Entry<Integer, Job>> iterator = runningJobs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Job> entry = iterator.next();
            int endTime = entry.getKey();
            Job job = entry.getValue();

            if (currentTime == endTime) {
                System.out.println("Job " + job.getJobID() + " has completed at tick " + currentTime + ", releasing " + job.getResourceRequirement() + " CPUs.");
                cpuAvailable += job.getResourceRequirement();
                iterator.remove();
                checkWaitingQueue(currentTime);
                printSystemStatus(currentTime);
            }
        }
    }

    private void checkWaitingQueue(int currentTime) {
        handleJobMovingToOutgoingQueue(currentTime);
        Iterator<Job> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Job job = iterator.next();

            if (job.getResourceRequirement() <= cpuAvailable) {
                System.out.println("Job " + job.getJobID() + " from waiting queue is now able to run.");
                startJob(job, currentTime);
                iterator.remove();
            }
        }
    }

    private void startJob(Job job, int currentTime) {
        System.out.println("Job " + job.getJobID() + " is starting execution after waiting " + job.getCurrentWaitTime() + " ticks.");

        runningJobs.put(currentTime + job.getDuration(), job);
        cpuAvailable -= job.getResourceRequirement();
    }

    private void printSystemStatus(int currentTime) {
        System.out.println("Tick " + currentTime);
        System.out.println("Free CPUs: " + cpuAvailable + ", Busy CPUs: " + (numOfCPUs - cpuAvailable));
        System.out.println("Running Tasks: " + runningJobs.values());
        System.out.println("Waiting Queue: " + waitingQueue);
        System.out.println("Outgoing Queue: " + outgoingQueue);
        System.out.println("---------------------------------------------------------");
    }

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
