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
    public void assignInitialTasks(GlobalScreenshot gs, double partitionsPerTimeStep) {
        GlobalScreenshotPart[] parts = gs.getParts();
        // assign initial tasks for every region's GlobalScreenshotPart
        for (int i = 0; i < parts.length; i++) {
            GlobalScreenshotPart gsp = parts[i];
            int numOfWorkers = gsp.getCpuStatus().length;
            int unassignedTasks = gsp.getUnassignedTasks();
            int assignedTasks = gsp.getAssignedTasks();
            int totalTasks = gsp.getTotalTasks();
            double pendingPortions = gsp.getPendingPortions();

            boolean[] newStates = new boolean[numOfWorkers]; // all false(busy)
            int newStatesID = 0;
            double maxCurrentCapacity = partitionsPerTimeStep * numOfWorkers;

            if (pendingPortions <= maxCurrentCapacity) {
                int numOfFreeWorkers = (int)((maxCurrentCapacity - pendingPortions)/partitionsPerTimeStep);
                while (numOfFreeWorkers > 0) {
                    newStates[newStatesID] = true;
                    numOfFreeWorkers--;
                    newStatesID++;
                }
                unassignedTasks = 0;
                assignedTasks = totalTasks;
                pendingPortions = 0.0;
            } else {
                pendingPortions -= maxCurrentCapacity;
                assignedTasks += numOfWorkers;
                unassignedTasks -= numOfWorkers;
            }
            gsp.setUnassignedTasks(unassignedTasks);
            gsp.setAssignedTasks(assignedTasks);
            gsp.setPendingPortions(pendingPortions);
            gsp.setCpuStatus(newStates);
        }
    }
    public void updateGlobalScreenshot (GlobalScreenshot gs, double partitionsPerTimeStep, boolean update) {
        GlobalScreenshotPart[] parts = gs.getParts();

        // update every region's GlobalScreenshotPart.
        for (int i = 0; i < parts.length; i++) {
            GlobalScreenshotPart gsp = parts[i];
            int numOfWorkers = gsp.getCpuStatus().length;
            int unassignedTasks = gsp.getUnassignedTasks();
            int assignedTasks = gsp.getAssignedTasks();
            int totalTasks = gsp.getTotalTasks();
            double pendingPortions = gsp.getPendingPortions();
            boolean[] newStates = new boolean[numOfWorkers]; // all false(busy)
            int newStatesID = 0;
            double maxCurrentCapacity = partitionsPerTimeStep * numOfWorkers;

            if (pendingPortions <= maxCurrentCapacity) {
                int numOfFreeWorkers = (int)((maxCurrentCapacity - pendingPortions)/partitionsPerTimeStep);
                while (numOfFreeWorkers > 0 && newStatesID < numOfWorkers) {
                    newStates[newStatesID] = true;
                    numOfFreeWorkers--;
                    newStatesID++;
                }
                unassignedTasks = 0;
                assignedTasks = totalTasks;
                pendingPortions = 0.0;
            }
            else {
                pendingPortions -= maxCurrentCapacity;
                if (update){
                    if (unassignedTasks < numOfWorkers) {
                        assignedTasks += unassignedTasks;
                        unassignedTasks = 0;

                    } else {
                        assignedTasks += numOfWorkers;
                        unassignedTasks -= numOfWorkers;
                    }
                }

            }
            gsp.setUnassignedTasks(unassignedTasks);
            gsp.setAssignedTasks(assignedTasks);
            gsp.setPendingPortions(pendingPortions);
            gsp.setCpuStatus(newStates);
        }

        // update GlobalScreenshot
        gs.setParts(parts);
    }


    public static void main(String[] args) {
        double partitionsPerTimeStep; // partitions per time step

        // each partition takes n time steps
//        int timeOfCompletion = CompletionTimeGenerator.generateTimeOfCompletion(DISTRIBUTION_CHOICE);
        int timeOfCompletion = 5;
        System.out.println("time of completion: " + timeOfCompletion);
        // suppose timeOfCompletion = 5, it means a partitions takes 5 time steps to finish. so it means partitionsPerTimeStep = 1/5.
        partitionsPerTimeStep = 1.0 / timeOfCompletion;

        String regionsFile = "regions.json";
        Map<Integer, Region> regionMap = readRegions(regionsFile);
        RegionManager rm = new RegionManager(regionMap);

        GlobalScreenshotPart[] parts = new GlobalScreenshotPart[regionMap.size()]; // an empty gsp[]
        int partsID = 0;

        //First, generate empty GlobalScreenshotPart for every region,
        for (Map.Entry<Integer, Region> entry:regionMap.entrySet()){

            int numOfCPUs = entry.getValue().getNumOfCPUs();
            Map<String, Integer> dataSizeMap = entry.getValue().getDataSizeMap();
            boolean[] cpuStatus = new boolean[numOfCPUs];
            for (int i = 0; i < cpuStatus.length; i++) {
                cpuStatus[i] = true;
            }

            int totalTasks = dataSizeMap.values().stream().mapToInt(Integer::intValue).sum();
            int unassignedTasks = dataSizeMap.values().stream().mapToInt(Integer::intValue).sum();
            int assignedTasks = 0;
            double pendingPortions = unassignedTasks;

            GlobalScreenshotPart gsp = new GlobalScreenshotPart(cpuStatus, unassignedTasks, assignedTasks, totalTasks, pendingPortions);
            parts[partsID] = gsp;
            partsID++;
        }

        GlobalScreenshot gs = new GlobalScreenshot(parts);
        gs.printDetails(); // no worker has been assigned any jobs yet


        // Second, assign initial tasks.
        rm.assignInitialTasks(gs, partitionsPerTimeStep);
        gs.printDetails(); // initial jobs has been assigned
//
        // Third, check if any worker in any of the region is still busy. If so, stay in the loop.
        // check if any item in cpuStatus in any region == false, if so, means at least one worker is still busy
        // stay in the while loop.
        int count = timeOfCompletion - 1; // 此时count=4
        while (rm.checkIfAnyBusy(gs)) {
            if (count > 0) {
                rm.updateGlobalScreenshot(gs, partitionsPerTimeStep, false);
                count--;
            } else if (count == 0) {
                rm.updateGlobalScreenshot(gs, partitionsPerTimeStep, true);
                count = timeOfCompletion - 1;
            }

            gs.printDetails();
        }
    }
}
