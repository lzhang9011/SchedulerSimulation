package org.example;

import java.io.*;
import java.util.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ClusterManager {
//    public static final double jobClassificationThreshold = 0.6; // if 60% jobs are short jobs, or if 60% jobs are long jobs.
    public static int tickDurationSeconds = 1; // start with 1:1 resolution
    private int x;
    private static final double GBperTick = 0.05;

    private final String baseTime = "2020-03-18 04:01:39"; //used to compute job's arrival time relative to the baseTime
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

    public int getX() {
        return x;
    }
    public long getBaseEpochSeconds() {
        return baseEpochSeconds;
    }

    private void handleJobArrival(Job job) {
        System.out.println("Job " + job.getJobID() + " has arrived at tick " + currentTime + ", requiring " + job.getResourceRequirement() + " CPUs for " + job.getDuration() + " ticks.");
        if (job.getResourceRequirement() <= clusters.get(0).getCpuAvailable()) {
            System.out.println("Decision: Job " + job.getJobID() + " can run immediately.");
            startJob(job);
        } else {
            System.out.println("Decision: Job " + job.getJobID() + " has to wait in the queue.");
            clusters.get(0).addToWaitingQueue(job);
        }
        printSystemStatus();
    }

    private void handleJobCompletion() {
        Iterator<Map.Entry<Integer, Job>> iterator = clusters.get(0).getRunningJobs().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Job> entry = iterator.next();
            int endTime = entry.getKey();
            Job job = entry.getValue();

            if (currentTime == endTime) {
                System.out.println("Job " + job.getJobID() + " has completed at tick " + currentTime + ", releasing " + job.getResourceRequirement() + " CPUs.");
                clusters.get(0).releaseCPU(job.getResourceRequirement());
                iterator.remove();
                checkWaitingQueue();
                printSystemStatus();
            }
        }
    }
    private void checkWaitingQueue() {
        Iterator<Job> iterator = clusters.get(0).getWaitingQueue().iterator();
        while (iterator.hasNext()) {
            Job job = iterator.next();
            if (job.getResourceRequirement() <= clusters.get(0).getCpuAvailable()) {
                System.out.println("Job " + job.getJobID() + " from waiting queue is now able to run.");
                startJob(job);
                iterator.remove();
            } else {
                break; // If the first waiting job can't be scheduled, others won't be either
            }
        }
    }

    private void startJob(Job job) {
        clusters.get(0).getRunningJobs().put(currentTime + job.getDuration(), job);
        clusters.get(0).allocateCPU(job.getResourceRequirement());
    }

    private void printSystemStatus() {
        System.out.println("Tick " + currentTime);
        System.out.println("Free CPUs: " + clusters.get(0).getCpuAvailable() + ", Busy CPUs: " + (clusters.get(0).getNumOfCPUs() - clusters.get(0).getCpuAvailable()));
        System.out.println("Running Jobs: " + clusters.get(0).getRunningJobs().values());
        System.out.println("Waiting Queue: " + clusters.get(0).getWaitingQueue());
        System.out.println("---------------------------------------------------------");
    }

    public void runSimulation() {
        System.out.println("Simulation started.\n---------------------------------------------------------");
        printSystemStatus();

        while (!clusters.get(0).getEventQueue().isEmpty() || !clusters.get(0).getRunningJobs().isEmpty()) {
            if (!clusters.get(0).getEventQueue().isEmpty()) {
                Job job = clusters.get(0).getEventQueue().peek();
                if (job.getArrivalTime() == currentTime) {
                    clusters.get(0).getEventQueue().poll();
                    handleJobArrival(job);
                }
            }

            handleJobCompletion();

            currentTime++;
        }

        System.out.println("\nSimulation complete.");
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

        // 3. fill up the job list from the sampled dataset
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
                int arrivalTime = (int) (submitEpochSeconds - manager.getBaseEpochSeconds());

                int rand = manager.getX();
                double dataLoad = durationInTicks * resourceRequirement * GBperTick + rand;
                Job job = new Job(entryCount, durationInTicks, resourceRequirement, arrivalTime, dataLoad);
                manager.jobs.add(job);
//
//                System.out.println("Entry " + (++entryCount) + ":");
//                System.out.println("Duration: " + values[durationIdx].trim());
//                System.out.println("GPU Num: " + values[gpuIdx].trim());
//                System.out.println("CPU Num: " + values[cpuIdx].trim());
//                System.out.println("Submit Time: " + values[submitTimeIdx].trim());
//                System.out.println("---------------------------");
                entryCount ++;
            }

            if (entryCount == 0) {
                System.out.println("No valid data entries found in " + sampleFile.getName());
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

        // at this point, my Jobs have un-translated durations in terms of simulation tick.
        System.out.println(manager.jobs);

        // TODO: turn off scale down job's duration if long jobs is the majority
//        // 3.1 examining resolution. (1:1 or 1:60 in terms of time translation)
//        int longJobCount = 0;
//        for (Job job : manager.jobs) {
//            if (job.getDuration() > 60) {
//                longJobCount++;
//            }
//        }
//
//        double longJobRadio = (double) longJobCount / manager.jobs.size();
//
//        if (longJobRadio > ClusterManager.jobClassificationThreshold) {
//            System.out.println("long jobs wins!");
//            tickDurationSeconds = 60; //long jobs are majority
//            // update job class's duration and arrivalTime with the correct resolution
//            for (Job job : manager.jobs) {
//                job.setDuration((int) Math.ceil(job.getDuration() / (double)tickDurationSeconds));
//                job.setArrivalTime((int) Math.ceil(job.getArrivalTime() / (double)tickDurationSeconds));
//            }
//
//        } else {
//            tickDurationSeconds = 1; // short jobs are majority, no need to update jobs
//
//        }
        Cluster cluster = clusters.get(0);
        for (Job job : manager.jobs) {
            cluster.addJob(job);
        }
//        System.out.println("Event Queue initialized with jobs: " + cluster.getEventQueue());

        // 5. run the simulation
        manager.runSimulation();


    }
}

