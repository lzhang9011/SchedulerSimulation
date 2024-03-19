package org.example;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegionManager {
    private Region[] regions;
    // TODO: change to map. key region id, value region


    private ExecutorService executor;

    public RegionManager(Region[] regions) {
        this.regions = regions;
        executor = Executors.newFixedThreadPool(regions.length);
    }

    public static Region[] readRegions(String file) {
        Gson gson = new Gson();
        Region[] regions = null;

        try (FileReader reader = new FileReader(file)) {
            regions = gson.fromJson(reader, Region[].class); // read from JSON file

        } catch (IOException e) {
            e.printStackTrace();
        }
        return regions;
    }

    public void start() {

        for (Region region : regions) {
            executor.execute(new RegionThread(region));
        }

    }
    private void shutdown() {
        // Shutdown the executor after all tasks are completed
        executor.shutdown();
    }

    // TODO: methods: get region status methods: query capacity, query esitimation time , etc






    public static void main(String[] args) {

        String file = "regions.json";
        Region[] regions = RegionManager.readRegions(file);

        RegionManager rm = new RegionManager(regions);

        rm.start();

        rm.shutdown();

    }


}
