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
import java.util.Random;


class Scheduler {
    String datacenterFile = "";

    public static List<Datacenter> datacenters = new ArrayList<>();
    private List<Task> tasksEverExisted = new ArrayList<>();

    private final TaskMilestone milestoneTracker = new TaskMilestone();
    private int currentTime = 0;
    private final double bandwidth = 10;
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

//    public void load10TasksFromCSV(String inputFile, int factor) {
//        File sourceFile = new File(inputFile);
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
//            String headerLine = reader.readLine();
//            if (headerLine == null) {
//                System.out.println("CSV file is empty.");
//                return;
//            }
//
//            String[] headers = headerLine.split(",");
//            int durationIdx = -1, gpuIdx = -1, cpuIdx = -1, submitTimeIdx = -1;
//
//            for (int i = 0; i < headers.length; i++) {
//                switch (headers[i].trim()) {
//                    case "duration": durationIdx = i; break;
//                    case "gpu_num": gpuIdx = i; break;
//                    case "cpu_num": cpuIdx = i; break;
//                    case "submit_time": submitTimeIdx = i; break;
//                }
//            }
//
//            if (durationIdx == -1 || gpuIdx == -1 || cpuIdx == -1 || submitTimeIdx == -1) {
//                System.out.println("One or more required columns are missing in the CSV file.");
//                return;
//            }
//
//            String dataLine;
//            int entryCount = 0;
//
//            while ((dataLine = reader.readLine()) != null && entryCount < 10) {
//                String[] values = dataLine.split(",");
//                if (values.length <= Math.max(durationIdx, Math.max(gpuIdx, Math.max(cpuIdx, submitTimeIdx)))) {
//                    System.out.println("Skipping malformed row: " + dataLine);
//                    continue;
//                }
//
//                int duration = Integer.parseInt(values[durationIdx].trim());
//                int resourceRequirement = Integer.parseInt(values[gpuIdx].trim()) + Integer.parseInt(values[cpuIdx].trim());
//                double dataLoad = duration * resourceRequirement * MBperTick + (new Random().nextInt(10) + 1);
//                LocalDateTime submitDateTime = LocalDateTime.parse(values[submitTimeIdx].trim(), formatter);
//                long submitEpoch = submitDateTime.toEpochSecond(ZoneOffset.UTC);
//
//                int arrivalTime = (int)((submitEpoch - baseEpochSeconds) / factor);
//                Task task = new Task(entryCount++, arrivalTime, duration, resourceRequirement, dataLoad);                tasksEverExisted.add(task);
//
//            }
//
//            if (entryCount == 0) {
//                System.out.println("No valid data entries found in " + inputFile);
//            }
//
//        } catch (IOException e) {
//            System.err.println("Error reading the file: " + e.getMessage());
//        }
//    }
//
//    public void loadTasksFromCSV(String inputFile, int interval) {
//        File sourceFile = new File(inputFile);
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
//            String headerLine = reader.readLine();
//            if (headerLine == null) {
//                System.out.println("CSV file is empty.");
//                return;
//            }
//
//            String[] headers = headerLine.split(",");
//            int durationIdx = -1, gpuIdx = -1, cpuIdx = -1, submitTimeIdx = -1;
//
//            for (int i = 0; i < headers.length; i++) {
//                switch (headers[i].trim()) {
//                    case "duration": durationIdx = i; break;
//                    case "gpu_num": gpuIdx = i; break;
//                    case "cpu_num": cpuIdx = i; break;
//                    case "submit_time": submitTimeIdx = i; break;
//                }
//            }
//
//            if (durationIdx == -1 || gpuIdx == -1 || cpuIdx == -1 || submitTimeIdx == -1) {
//                System.out.println("One or more required columns are missing in the CSV file.");
//                return;
//            }
//
//            String dataLine;
//            int entryCount = 0;
//            int nextArrivalTime = 0;
//
//            while ((dataLine = reader.readLine()) != null) {
//                String[] values = dataLine.split(",");
//                if (values.length <= Math.max(durationIdx, Math.max(gpuIdx, Math.max(cpuIdx, submitTimeIdx)))) {
//                    System.out.println("Skipping malformed row: " + dataLine);
//                    continue;
//                }
//
//                int duration = Integer.parseInt(values[durationIdx].trim());
//                int resourceRequirement = Integer.parseInt(values[gpuIdx].trim()) + Integer.parseInt(values[cpuIdx].trim());
//                double dataLoad = duration * resourceRequirement * MBperTick + (new Random().nextInt(10) + 1);
//
//                Task task = new Task(entryCount++, nextArrivalTime, duration, resourceRequirement, dataLoad);
//                tasksEverExisted.add(task);
//
//                nextArrivalTime += interval;
//            }
//
//            if (entryCount == 0) {
//                System.out.println("No valid data entries found in " + inputFile);
//            }
//
//        } catch (IOException e) {
//            System.err.println("Error reading the file: " + e.getMessage());
//        }
//    }

    public void tasksGenerate() {
        tasksEverExisted.clear();

        tasksEverExisted.add(new Task(0, 1, 5, 4, 2.5, maxWaitTimeCompute(5, 4)));
        tasksEverExisted.add(new Task(1, 2, 3, 2, 1.0, maxWaitTimeCompute(3,2)));
        tasksEverExisted.add(new Task(2, 4, 4, 8, 5.0, maxWaitTimeCompute(4, 8)));
    }

    public int maxWaitTimeCompute(int duration, int cpuRequirement) {
//        return duration * cpuRequirement;
        return 0;
    }




    public void runSimulation(int factor) {
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

        //DEBUG
        localDatacenter.setTotalCPUs(10);
        FieldSetter.setField(localDatacenter, "availableCPUs", 10);

        // ✅2. load csv file to generate sample dataset.
//        loadTasksFromCSV("cluster_log.csv", interval);
//        load10TasksFromCSV("cluster_log.csv", factor);

        //DEBUG
        tasksGenerate();

        for (Task task : tasksEverExisted) {
            System.out.println("Task " + task.getId() + " created with arrivalTime: " + task.getArrivalTime() +
                    ", duration: " + task.getDuration() +
                    ", cpuRequired: " + task.getCpuRequirement() +
                    ", dataLoad: " + task.getDataLoad());
//            localDatacenter.addTask(task);
        }



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
            System.out.println("Current Time/Tick is: " + currentTime);

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
            localDatacenter.printSystemStatus(currentTime, bandwidth);

            remoteDatacenter.handleTaskArrival(currentTime, milestoneTracker);
            remoteDatacenter.handleTaskCompletion(currentTime, milestoneTracker);
            remoteDatacenter.printSystemStatus(currentTime, bandwidth);

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

        milestoneTracker.writeToCSVFull("task_milestones.csv", factor);


//        System.out.println("\nSimulation complete.");
    }

}
