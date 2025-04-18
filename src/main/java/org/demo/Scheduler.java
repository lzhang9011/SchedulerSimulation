package org.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


class Scheduler {
    String datacenterFile = "";

    public static List<Datacenter> datacenters = new ArrayList<>();
    private List<Task> tasksEverExisted = new ArrayList<>();

    private final TaskMilestone milestoneTracker = new TaskMilestone();
    private int currentTime = 0;
    private static final double MBperTick = 0.005;

    private final String baseTime = "2020-03-18 04:01:39"; // used to compute job's arrival time relative to the baseTime
    private final long baseEpochSeconds;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Long> resultList = new ArrayList<>();

    public Scheduler(String datacenterFile) {
        this.datacenterFile = datacenterFile;
        LocalDateTime dateTime = LocalDateTime.parse(baseTime, formatter);
        this.baseEpochSeconds = dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public long getBaseEpochSeconds() { return baseEpochSeconds; }
    public void addTask(Task task) {
        // scheduler's addTask is to localDatacenter's eventQueue. This is the very INITIAL add task.
        datacenters.get(0).addTask(task);
    }


    public static List<Datacenter> loadOrGenerateDatacenters(String datacenterFile) {
        File file = new File(datacenterFile);

        if (file.exists()) {
//            System.out.println("Loading datacenters from JSON...");
            datacenters = DatacenterGenerator.readDatacentersFromJson();
        } else {
//            System.out.println("Generating new clusters...");
            datacenters = DatacenterGenerator.generateDatacenters();
            DatacenterGenerator.writeDatacentersToJson(datacenters);
        }


        return datacenters;
    }

    public void load10TasksFromCSV(String inputFile, double factor) {
        tasksEverExisted.clear();
        File sourceFile = new File(inputFile);

        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
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
            int lastArrival = 0;

            int skipped = 0;

            while ((dataLine = reader.readLine()) != null) {
                if (skipped < 1) {
                    skipped++;
                    continue;
                }

                if (entryCount >= 40000) {
                    break;
                }

                String[] values = dataLine.split(",");
                if (values.length <= Math.max(durationIdx, Math.max(gpuIdx, Math.max(cpuIdx, submitTimeIdx)))) {
                    System.out.println("Skipping malformed row: " + dataLine);
                    continue;
                }

                int duration = Integer.parseInt(values[durationIdx].trim());
                int resourceRequirement = Integer.parseInt(values[gpuIdx].trim()) + Integer.parseInt(values[cpuIdx].trim());

                LocalDateTime submitDateTime = LocalDateTime.parse(values[submitTimeIdx].trim(), formatter);
                long submitEpoch = submitDateTime.toEpochSecond(ZoneOffset.UTC);
                int rawArrival = (int)(submitEpoch - baseEpochSeconds);
                int scaledArrival = (int)Math.ceil(rawArrival / factor);

                double dataLoad = dataLoadCompute(duration, resourceRequirement);
                int maxWaitTime = maxWaitTimeCompute(duration, resourceRequirement);

                Task task = new Task(entryCount++, scaledArrival, duration, resourceRequirement, dataLoad, maxWaitTime);
                tasksEverExisted.add(task);
            }

            if (entryCount == 0) {
                System.out.println("No valid data entries found in " + inputFile);
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

//    public void tasksGenerate(double factor) {
//        tasksEverExisted.clear();
//
//        int[] originalArrivals = {
//                1, 2, 4, 10, 11, 12, 17, 30, 38, 47,
//                55, 63, 70, 80, 90, 93, 97, 101, 106, 115
//        };
//
//        // Durations and CPU requirements
//        int[] durations = {
//                8, 5, 6, 5, 8, 12, 4, 10, 14, 7,
//                9, 6, 5, 11, 13, 8, 10, 7, 9, 6
//        };
//
//        int[] cpus = {
//                5, 3, 9, 10, 7, 4, 5, 6, 9, 11,
//                6, 5, 8, 7, 10, 4, 8, 6, 7, 5
//        };
//
//        // Slightly increased dataLoads, still loosely tied to duration * cpu
//        double[] dataLoads = {
//                3.0, 1.2, 6.0, 13.0, 4.2, 3.5, 1.0, 5.0, 9.0, 5.5,
//                6.0, 3.2, 3.8, 7.0, 8.5, 4.5, 6.8, 4.2, 6.0, 3.3
//        };
//
//        int lastArrival = 0;
//
//        for (int i = 0; i < originalArrivals.length; i++) {
//            int arrival = (int)Math.ceil(originalArrivals[i] / factor);
//            arrival = Math.max(arrival, lastArrival);
//            lastArrival = arrival;
//
//            int duration = durations[i];
//            int cpu = cpus[i];
//            double data = dataLoads[i];
//            int maxWaitTime = maxWaitTimeCompute(duration, cpu);
//
//            tasksEverExisted.add(new Task(i, arrival, duration, cpu, data, maxWaitTime));
//        }
//    }





    public int maxWaitTimeCompute(int duration, int cpuRequirement) {
//        return 0;
        return (int)Math.round(0.125 * duration * cpuRequirement);
//        return (int)(0.5 * duration * cpuRequirement);
    }

    public double dataLoadCompute(int duration, int cpuRequirement) {
//        return 0.0;
        return duration * cpuRequirement * MBperTick;
    }




    public void runSimulation(double factor, double bandwidth) {
        // 0. clear previous simulation trace
        tasksEverExisted.clear();
        datacenters.clear();
        milestoneTracker.clearMilestones();
        currentTime = 0;
        resultList.clear();

        // ✅1. load Or Generate datacenters.
        List<Datacenter> datacenters = loadOrGenerateDatacenters(datacenterFile);
        Datacenter localDatacenter = datacenters.get(0);
        Datacenter remoteDatacenter = datacenters.get(1);

//        //DEBUG
//        localDatacenter.setTotalCPUs(10);
//        FieldSetter.setField(localDatacenter, "availableCPUs", 10);

        // ✅2. load csv file to generate sample dataset.
//        loadTasksFromCSV("cluster_log.csv", interval);
        load10TasksFromCSV("cluster_log.csv", factor);

        //DEBUG
//        tasksGenerate(factor);
//
//        for (Task task : tasksEverExisted) {
//            System.out.println("Task " + task.getId() + " created with arrivalTime: " + task.getArrivalTime() +
//                    ", duration: " + task.getDuration() +
//                    ", cpuRequired: " + task.getCpuRequirement() +
//                    ", dataLoad: " + task.getDataLoad());
////            localDatacenter.addTask(task);
//        }



        // WILL TURN ON AFTER FINISH DEBUG MODE
//        int numOftransferTasks = localDatacenter.numOftransferTasks();
//        double currentBandwidthRate = (numOftransferTasks == 0) ? bandwidth : bandwidth / numOftransferTasks;
//        for (Task task : tasksEverExisted) {
//            double transferTime = task.getDataLoad() / currentBandwidthRate;
//            task.setMaxWaitTime((int)transferTime);
//        }

        // ✅3. add to local's eventQueue
        for (Task task : tasksEverExisted) {
            localDatacenter.addTask(task);
        }
//        System.out.println("Simulation started with interval " + interval);
//        System.out.println("tasksEverExisted: " + tasksEverExisted.toString());


        while (localDatacenter.hasPendingTasks() || localDatacenter.hasUnfinishedTransfers() || remoteDatacenter.hasPendingTasks() || remoteDatacenter.hasUnfinishedTransfers()) {


//            System.out.println("Current Time/Tick is: " + currentTime);

            List<Task> completedTransfers = localDatacenter.updateTransferProgress(currentTime, bandwidth);
            for (Task task : completedTransfers) {
                task.setArrivalTime(currentTime); // update transferred task's arrival time
                remoteDatacenter.addTask(task);
//                System.out.println("Task " + task.id + " has arrived at remoteDatacenter's eventQueue.");
            }
            localDatacenter.moveTaskToTracker(currentTime);//increment waitTime for waiting Tasks.
            /*
             * add milestoneTracker instance
             * because both methods --handleTaskArrival() and handleTaskCompletion()
             * will call startTask(), which will check if the task is transferred or not
             * */
            localDatacenter.handleTaskArrival(currentTime, milestoneTracker);
            localDatacenter.handleTaskCompletion(currentTime, milestoneTracker);
//            localDatacenter.printSystemStatus(currentTime, bandwidth);

            remoteDatacenter.handleTaskArrival(currentTime, milestoneTracker);
            remoteDatacenter.handleTaskCompletion(currentTime, milestoneTracker);
//            remoteDatacenter.printSystemStatus(currentTime, bandwidth);

            currentTime++;
        }

        // recording milestones of tasks
        for (Task task : tasksEverExisted) {
            if (task.getOriginalArrivalTime() == task.getArrivalTime()) {
                milestoneTracker.recordMilestone(task, task.getArrivalTime(), false, 0);
            } else {
                // remote task milestone tracking:
                milestoneTracker.recordMilestone(task, task.getArrivalTime(), true, task.getDataLoad());
            }
        }

        milestoneTracker.writeToCSVFull("task_milestones.csv", factor, bandwidth);


        System.out.println("\nSimulation complete.");

        // compute and print system load:
        long totalDemand = 0;
        long totalPeriod = currentTime - 1;
        int totalCPUs = localDatacenter.getTotalCPUs();

        for (Task task : tasksEverExisted) {
            totalDemand += task.getCpuRequirement() * task.getDuration();
        }


        System.out.println("total demand: " + totalDemand);
        System.out.println("total period: " + totalPeriod);
        System.out.println("total CPUs: " + totalCPUs);
        System.out.println("running under bandwidth = " + bandwidth);

        double systemLoad = (double) totalDemand / (totalPeriod * totalCPUs);
        System.out.printf("Current system load is: %.2f%n", systemLoad);



    }

}
