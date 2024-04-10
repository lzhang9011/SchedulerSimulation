package org.example;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegionManager {
    private Map<Integer, Region> regionMap;
    private RegionsInfo regionsInfo;
    private ExecutorService executor;

    public RegionManager(Map<Integer, Region> regionMap, RegionsInfo regionsInfo) {
        this.regionMap = regionMap;
        this.regionsInfo = regionsInfo;
        executor = Executors.newFixedThreadPool(regionMap.size());
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
    public static RegionsInfo readRegionsInfo(String file) {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, RegionsInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void start() {

        for (Region region : regionMap.values()) {
            executor.execute(new RegionThread(region));
        }

    }
    private void shutdown() {
        // Shutdown the executor after all tasks are completed
        executor.shutdown();
    }

    public int getTotalDataSize() {
        return regionsInfo.getTotalDataSize();
    }
    public int getNumOfRegions() {
        return regionsInfo.getNumOfRegions();
    }
    public Region getRegion(int regionID) {
        return regionMap.get(regionID);
    }
    // TODO: methods: get region status methods: query capacity, query esitimation time , etc

    public static void main(String[] args) {
        // read regions' config file and run regions multi-threaded.

        String regionsFile = "regions.json";
        String regionsInfoFile = "regionsInfo.json";
        Map<Integer, Region> regionMap = readRegions(regionsFile);
        RegionsInfo regionsInfo = readRegionsInfo(regionsInfoFile);

        RegionManager rm = new RegionManager(regionMap, regionsInfo);
        System.out.println("Total Data Size: " + rm.getTotalDataSize());
        System.out.println("Number of Regions: " + rm.getNumOfRegions());
        System.out.println("Region 0 Info: " + rm.getRegion(0));

        rm.start();
        rm.shutdown();

    }


}
