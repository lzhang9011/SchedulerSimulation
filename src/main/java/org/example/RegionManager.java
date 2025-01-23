package org.example;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;



// RegionManager can assign jobs across regions, then produce screenshots that track progress of each region.
public class RegionManager {
    // 1 is normal distribution, will return an int of range [1-10].
    // 2 is discrete uniform distribution, will return an int of range [1-10].
    // 3 is poisson distribution, will return an int of range [1-10]

    private static final int DISTRIBUTION_CHOICE = 1; // Change this value to switch between distributions

    private Map<Integer, Region> regionMap;

    public RegionManager(Map<Integer, Region> regionMap) {
        this.regionMap = regionMap;
    }
    public Region getRegion(int regionID) {
        return regionMap.get(regionID);
    }
    public GlobalScreenshotPart[] initializeGlobalScreenshotParts() {
        GlobalScreenshotPart[] parts = new GlobalScreenshotPart[regionMap.size()];
        int partsID = 0;

        // Iterate through each region and initialize the corresponding GlobalScreenshotPart
        for (Map.Entry<Integer, Region> entry : regionMap.entrySet()) {
            Region region = entry.getValue();
            int numOfCPUs = region.getNumOfCPUs();
            Map<String, Integer> dataSizeMap = region.getDataSizeMap();

            // true == free, false == busy
            boolean[] cpuStatus = new boolean[numOfCPUs];
            Arrays.fill(cpuStatus, true);

            int totalTasks = dataSizeMap.values().stream().mapToInt(Integer::intValue).sum();
            int unassignedTasks = totalTasks;
            int assignedTasks = 0;
            double pendingPortions = totalTasks;

            // Create and initialize the GlobalScreenshotPart for this region
            GlobalScreenshotPart gsp = new GlobalScreenshotPart(cpuStatus, unassignedTasks, assignedTasks, totalTasks, pendingPortions);
            parts[partsID] = gsp; // Add to the parts array
            partsID++;
        }

        return parts;
    }
    public static Map<Integer, Region> readRegions(String file) {
        Gson gson = new Gson();
        Map<Integer, Region> regionMap = new HashMap<>();

        try (FileReader reader = new FileReader(file)) {
            Region[] regions = gson.fromJson(reader, Region[].class);
            if (regions != null) {
                for (Region region : regions) {
                    regionMap.put(region.getRegionID(), region);
                }
            } else {
                System.err.println("Failed to parse regions from JSON file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return regionMap;
    }


    public boolean checkIfAnyBusy(GlobalScreenshot gs) {
//        System.out.println("checking");
        GlobalScreenshotPart[] parts = gs.getParts();
        for (int i = 0; i < parts.length; i++) {
            GlobalScreenshotPart gsp = parts[i];
            for (boolean item:gsp.getCpuStatus()) {
                if (item == false) {
//                    System.out.println("someone is busy");
                    return true;
                }
            }
        }
//        System.out.println("no one is busy");
        return false;
    }
    public void updateGlobalScreenshotWithBudget(GlobalScreenshot gs, double alpha, int maxWaitTime) {
        GlobalScreenshotPart[] parts = gs.getParts();
        for (GlobalScreenshotPart part : parts) {
            setDynamicWaitingTime(part, alpha);

            Queue<Job> jobQueue = part.getJobQueue();
            for (Job job : jobQueue) {
                if (job.getWaitingTime() > maxWaitTime) {
                    System.out.println("Job " + job.getJobId() + " sent to the cloud.");
                    jobQueue.poll();
                }
            }
        }
    }


    public void setDynamicWaitingTime(GlobalScreenshotPart part, double alpha) {
        Queue<Job> jobQueue = part.getJobQueue();
        for (Job job : jobQueue) {
            int dynamicWait = (int) (alpha * job.getSize() * job.getEstimatedCompletionTime());
            job.setWaitingTime(dynamicWait);
        }
    }

    public void executeJobsOutOfOrder(GlobalScreenshotPart part, double partitionsPerTimeStep) {
        Queue<Job> jobQueue = part.getJobQueue();
        boolean[] cpuStatus = part.getCpuStatus();
        int availableCPUs = 0;
        for (boolean cpu : cpuStatus) {
            if (cpu) {
                availableCPUs++;
            }
        }

        while (!jobQueue.isEmpty()) {
            Job job = jobQueue.peek();
            if (job.getSize() <= availableCPUs) {
                availableCPUs -= job.getSize();
                jobQueue.poll();
                System.out.println("Executing job " + job.getJobId());
                try {
                    Thread.sleep(job.getEstimatedCompletionTime() * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Completed job " + job.getJobId());
            } else {
                break; // No jobs can fit
            }
        }
    }



    public static void main(String[] args) {
        double partitionsPerTimeStep = 0.2; // in each time step, 0.2 job should be completed.
        int maxWaitTime = 5; // in secs
        double alpha = 0.5; // the smaller alpha is, the smaller the waiting time, the more likely the job is sent to cloud

        String regionsFile = "regions.json";
        Map<Integer, Region> regionMap = readRegions(regionsFile);
        RegionManager rm = new RegionManager(regionMap);

        GlobalScreenshotPart[] parts = rm.initializeGlobalScreenshotParts();

        GlobalScreenshot gs = new GlobalScreenshot(parts);

        gs.printDetails();

        // scheduler
        while (rm.checkIfAnyBusy(gs)) {
            rm.updateGlobalScreenshotWithBudget(gs, alpha, maxWaitTime);
            for (GlobalScreenshotPart part : gs.getParts()) {
                rm.executeJobsOutOfOrder(part, partitionsPerTimeStep);
            }
        }
    }


}
