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
    private ExecutorService executor;

    public RegionManager(Map<Integer, Region> regionMap) {
        this.regionMap = regionMap;
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


    public void start() {

        for (Region region : regionMap.values()) {
            executor.execute(new RegionThread(region));
        }

    }
    private void shutdown() {
        // Shutdown the executor after all tasks are completed
        executor.shutdown();
    }


    public Region getRegion(int regionID) {
        return regionMap.get(regionID);
    }
    // TODO: methods: get region status methods: query capacity, query esitimation time , etc

    public static void main(String[] args) {
        // read regions' config file and run regions multi-threaded.

        String regionsFile = "regions.json";
        Map<Integer, Region> regionMap = readRegions(regionsFile);

        RegionManager rm = new RegionManager(regionMap);

        rm.start();
        rm.shutdown();

    }


}
