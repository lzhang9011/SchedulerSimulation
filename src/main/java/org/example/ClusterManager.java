package org.example;

import java.io.*;
import java.util.*;

public class ClusterManager {
    public static final double jobClassificationThreshold = 0.6; // if 60% jobs are short jobs, or if 60% jobs are long jobs.
    public static int tickDurationSeconds = 1; // start with 1:1 resolution
    private int x;
    private static final double GBperTick = 0.05;
    private List<Cluster> clusters;

    public ClusterManager(List<Cluster> clusters) {
        this.clusters = clusters;
        this.x = new Random().nextInt(10) + 1; // random number [1,10]
    }

    public int getX() {
        return x;
    }

    public void runSimulation(int totalTicks) {
        System.out.println("Simulation starting... Each tick = " + tickDurationSeconds + " seconds.");
//        System.out.println("Initial Random X Value: " + x);
        System.out.println("DataLoad Per Tick: " + GBperTick + " GB\n");

        for (int tick = 1; tick <= totalTicks; tick++) {
            System.out.println("Tick " + tick);
            updateClusterStatus();
        }

        System.out.println("\nSimulation complete.");
    }
    private void updateClusterStatus() {
        for (Cluster cluster : clusters) {
            System.out.println(cluster);
        }
        System.out.println("------------------------------");
    }


    public static void main(String[] args) {

        // 1.Generate and load clusters.
        String clusterFile = "clusters.json";
        String inputFile = "cluster_log.csv";
        List<Cluster> clusters;

        File file = new File(clusterFile);
        if (file.exists()) {
            System.out.println("Loading clusters from JSON...");
            clusters = ClusterGenerator.readClustersFromJson();
        } else {
            System.out.println("Generating new clusters...");
            clusters = ClusterGenerator.generateClusters();
            ClusterGenerator.writeClustersToJson(clusters);
        }

        System.out.println("\nLoaded Clusters:");
        for (Cluster cluster : clusters) {
            System.out.println(cluster);
        }

        ClusterManager manager = new ClusterManager(clusters);

        // 2. data loading and pre-processing with a desired sample dataset size.
        Scanner scanner = new Scanner(System.in);
        int sampleSize;
        while (true) {
            System.out.print("Enter a sample size (1-10): ");
            if (scanner.hasNextInt()) {
                sampleSize = scanner.nextInt();
                if (sampleSize >= 1 && sampleSize <= 10) {
                    break;
                }
            } else {
                scanner.next();
            }
            System.out.println("Invalid input. Please enter a number between 1 and 10.");
        }
        scanner.close();
        File sampleFile = new File("sample" + sampleSize + ".csv");
        System.out.println("Generating new sample dataset...");
        Loader_CSV.processCSV(inputFile, sampleFile.getName(), sampleSize);

        // 3. generate a list of jobs from the sampled dataset
        List<Job> jobs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(sampleFile))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.out.println("CSV file is empty.");
                return;
            }

            String[] headers = headerLine.split(",");
            int durationIdx = -1, gpuIdx = -1, cpuIdx = -1, submitTimeIdx = -1;

            for (int i = 0; i < headers.length; i++) {
                switch (headers[i].trim()) {
                    case "duration": durationIdx = i; break;
                    case "gpu_num": gpuIdx = i; break;
                    case "cpu_num": cpuIdx = i; break;
                    case "submit_time": submitTimeIdx = i; break;
                }
            }

            if (durationIdx == -1 || gpuIdx == -1 || cpuIdx == -1 || submitTimeIdx == -1) {
                System.out.println("One or more required columns are missing in the CSV file.");
                return;
            }

            String dataLine;
            int entryCount = 0;
            System.out.println("\nJob Details from CSV:");
            System.out.println("---------------------------");

            while ((dataLine = reader.readLine()) != null) {
                String[] values = dataLine.split(",");
                if (values.length <= Math.max(durationIdx, Math.max(gpuIdx, Math.max(cpuIdx, submitTimeIdx)))) {
                    System.out.println("Skipping malformed row: " + dataLine);
                    continue;
                }

                int duration = Integer.parseInt(values[durationIdx].trim());
                int durationInTicks = duration / tickDurationSeconds;
                int resourceRequirement = Integer.parseInt(values[gpuIdx].trim()) + Integer.parseInt(values[cpuIdx].trim());
                int arrivalTime = 0;
                int rand = manager.getX();
                System.out.println("random variable: " + rand);
                double dataLoad = durationInTicks * resourceRequirement * GBperTick + rand;
                Job job = new Job(durationInTicks, resourceRequirement, arrivalTime, dataLoad);
                jobs.add(job);

                System.out.println("Entry " + (++entryCount) + ":");
                System.out.println("Duration: " + values[durationIdx].trim());
                System.out.println("GPU Num: " + values[gpuIdx].trim());
                System.out.println("CPU Num: " + values[cpuIdx].trim());
                System.out.println("Submit Time: " + values[submitTimeIdx].trim());
                System.out.println("---------------------------");
            }

            if (entryCount == 0) {
                System.out.println("No valid data entries found in " + sampleFile.getName());
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

        // at this point, my Jobs have un-translated durations in terms of simulation tick.

        // 3.1 examining resolution. (1:1 or 1:60 in terms of time translation)
        int longJobCount = 0;
        for (Job job : jobs) {
            if (job.getDuration() > 60) {
                longJobCount++;
            }
        }

        double longJobRadio = (double) longJobCount / jobs.size();

        if (longJobRadio > ClusterManager.jobClassificationThreshold) {
            System.out.println("long jobs wins!");
            tickDurationSeconds = 60; //long jobs are majority
            // update job class's duration with the correct resolution
            for (Job job : jobs) {
                job.setDuration((int) Math.ceil(job.getDuration() / (double)tickDurationSeconds));
            }

        } else {
            tickDurationSeconds = 1; // short jobs are majority, no need to update jobs

        }



        //4. print job list
        System.out.println(jobs);


        // 5. run the simulation
        manager.runSimulation(2);

    }






}

