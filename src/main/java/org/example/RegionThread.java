//package org.example;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//public class RegionThread extends Thread {
//    private Cluster cluster;
//
//    public RegionThread(Cluster cluster) {
//        this.cluster = cluster;
//    }
//
//    @Override
//    public void run() {
//        Map<String, Integer> dataSizeMap = cluster.getDataSizeMap();
//        int numOfCPUs = cluster.getNumOfCPUs();
//
//        // Create a thread pool with a size equal to the number of CPUs
//        ExecutorService executor = Executors.newFixedThreadPool(numOfCPUs);
//
//        // Iterate through dataSizeMap
//        for (Map.Entry<String, Integer> entry : dataSizeMap.entrySet()) {
//            String key = entry.getKey();
//            int dataSize = entry.getValue();
//
//            // Record the start time
//            long startTime = System.currentTimeMillis();
//
//            // Submit task to executor
//            executor.submit(() -> {
//                // Adjust sleeping time based on dataSize
//                try {
//                    System.out.println("Processing data for key: " + key);
//                    // Sleep for dataSize seconds
//                    Thread.sleep(dataSize * 1000);
//                    // Calculate elapsed time
//                    long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
//                    System.out.println("Data processing for key " + key + " completed in " + elapsedTime + " seconds.");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//
//        // Shutdown executor after all tasks are submitted
//        executor.shutdown();
//
//        try {
//            // Wait for all tasks to complete or timeout after 1 hour
//            executor.awaitTermination(1, TimeUnit.HOURS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//
