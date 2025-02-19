package org.example;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ClusterManager {
    public static int tickDurationSeconds = 1; // start with 1:1 resolution
    private int x;
    private static final double GBperTick = 0.05;

    private final String baseTime = "2020-03-18 04:01:39"; // used to compute job's arrival time relative to the baseTime
    private final long baseEpochSeconds;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<Cluster> clusters;
    public List<Job> jobs;
    private int currentTime = 0;

    public ClusterManager(List<Cluster> clusters) {
        this.clusters = clusters;
        this.jobs = new ArrayList<>();
        this.x = new Random().nextInt(10) + 1; // random number [1,10]
        LocalDateTime dateTime = LocalDateTime.parse(baseTime, formatter);
        this.baseEpochSeconds = dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public int getX() { return x; }

    public long getBaseEpochSeconds() { return baseEpochSeconds; }

    public void runSimulation() {
        System.out.println("Simulation started.\n---------------------------------------------------------");
        Cluster local = clusters.get(0);
        Cluster remote = clusters.get(1);

        while (local.hasPendingJobs() || remote.hasPendingJobs()) {
            local.handleJobArrival(currentTime);
            local.handleJobCompletion(currentTime);
            currentTime++;
        }

        System.out.println("\nSimulation complete.");
    }

    public static List<Cluster> loadOrGenerateClusters(String clusterFile) {
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
        return clusters;
    }

    public void loadJobsFromCSV(String inputFile, int sampleSize) {
        File sampleFile = new File("sample" + sampleSize + ".csv");
        Loader_CSV.processCSV(inputFile, sampleFile.getName(), sampleSize);

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
                long submitEpochSeconds = LocalDateTime.parse(values[submitTimeIdx].trim(), formatter)
                        .toEpochSecond(ZoneOffset.UTC);
                int arrivalTime = (int) (submitEpochSeconds - getBaseEpochSeconds());
                double dataLoad = durationInTicks * resourceRequirement * GBperTick + getX();

                Job job = new Job(entryCount++, durationInTicks, resourceRequirement, arrivalTime, dataLoad);
                jobs.add(job);
            }

            if (entryCount == 0) {
                System.out.println("No valid data entries found in " + sampleFile.getName());
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    public static int getSampleSizeFromUser() {
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
        return sampleSize;
    }

    public static void main(String[] args) {
        String clusterFile = "clusters.json";
        List<Cluster> clusters = loadOrGenerateClusters(clusterFile);

        int sampleSize = getSampleSizeFromUser();
        ClusterManager manager = new ClusterManager(clusters);
        manager.loadJobsFromCSV("cluster_log.csv", sampleSize);

        Cluster local = clusters.get(0);
        for (Job job : manager.jobs) {
            local.addJob(job);
        }

        manager.runSimulation();
    }
}
