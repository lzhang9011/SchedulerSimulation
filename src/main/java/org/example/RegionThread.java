package org.example;

import java.util.Map;

public class RegionThread extends Thread {
    private Region region;

    public RegionThread(Region region) {
        this.region = region;
    }

    @Override
    public void run() {
        Map<String, Integer> dataSizeMap = region.getDataSizeMap();

        // Iterate through dataSizeMap
        for (Map.Entry<String, Integer> entry : dataSizeMap.entrySet()) {
            String key = entry.getKey();
            int dataSize = entry.getValue();

            long startTime = System.currentTimeMillis();

            try {
                System.out.println("Processing data for key: " + key);
                Thread.sleep(dataSize * 1000);
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                System.out.println("Data processing for key " + key + " completed in " + elapsedTime + " seconds.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

