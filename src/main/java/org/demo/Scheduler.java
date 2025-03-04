package org.demo;

import org.example.Loader_CSV;

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
import java.util.Scanner;


class Scheduler {
    String datacenterFile = "";

    public static List<Datacenter> datacenters = new ArrayList<>();
    private List<Task> tasksEverExisted = new ArrayList<>();

    private final TaskMilestone milestoneTracker = new TaskMilestone();
    private int currentTime = 0;
    private final double bandwidth = 100;


    private static final double GBperTick = 0.05;

    private final String baseTime = "2020-03-18 04:01:39"; // used to compute job's arrival time relative to the baseTime
    private final long baseEpochSeconds;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
            System.out.println("Loading datacenters from JSON...");
            datacenters = DatacenterGenerator.readDatacentersFromJson();
        } else {
            System.out.println("Generating new clusters...");
            datacenters = DatacenterGenerator.generateDatacenters();
            DatacenterGenerator.writeDatacentersToJson(datacenters);
        }


        return datacenters;
    }

    public static int getSampleSizeFromUser() {
        Scanner scanner = new Scanner(System.in);
        int sampleSize;
        while (true) {
            System.out.print("Enter a sample size (1-100): ");
            if (scanner.hasNextInt()) {
                sampleSize = scanner.nextInt();
                if (sampleSize >= 1 && sampleSize <= 100) {
                    break;
                }
            } else {
                scanner.next();
            }
            System.out.println("Invalid input. Please enter a number between 1 and 100.");
        }
        scanner.close();
        return sampleSize;
    }

    public void loadTasksFromCSV(String inputFile, int sampleSize) {
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

            while ((dataLine = reader.readLine()) != null) {
                String[] values = dataLine.split(",");
                if (values.length <= Math.max(durationIdx, Math.max(gpuIdx, Math.max(cpuIdx, submitTimeIdx)))) {
                    System.out.println("Skipping malformed row: " + dataLine);
                    continue;
                }

                int duration = Integer.parseInt(values[durationIdx].trim());
                int resourceRequirement = Integer.parseInt(values[gpuIdx].trim()) + Integer.parseInt(values[cpuIdx].trim());
                long submitEpochSeconds = LocalDateTime.parse(values[submitTimeIdx].trim(), formatter)
                        .toEpochSecond(ZoneOffset.UTC);
                int arrivalTime = (int) (submitEpochSeconds - getBaseEpochSeconds());

//                if (!tasksEverExisted.isEmpty()) {
//                    Task lastTask = tasksEverExisted.get(tasksEverExisted.size() - 1);
//                    if (lastTask.getArrivalTime() == arrivalTime) {
//                        arrivalTime++;
//                    }
//                }

                double dataLoad = 1024 * duration * resourceRequirement * GBperTick - (new Random().nextInt(10) + 1);

                Task task = new Task(entryCount++, arrivalTime, duration, resourceRequirement, dataLoad);
                tasksEverExisted.add(task);
            }

            if (entryCount == 0) {
                System.out.println("No valid data entries found in " + sampleFile.getName());
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }


    public void runSimulation() {
        // ✅1. load Or Generate datacenters.
        List<Datacenter> datacenters = loadOrGenerateDatacenters(datacenterFile);
//        for (Datacenter datacenter : datacenters) {
//            System.out.println(datacenter);
//        }
        Datacenter localDatacenter = datacenters.get(0);
        Datacenter remoteDatacenter = datacenters.get(1);

        // ✅2. load csv file to generate sample dataset.
        int sampleSize = getSampleSizeFromUser();
        loadTasksFromCSV("cluster_log.csv", sampleSize);

        // ✅3. add to local's eventQueue
        for (Task task : tasksEverExisted) {
            localDatacenter.addTask(task);
        }
//        System.out.println(localDatacenter.getEventQueue());

        System.out.println("Simulation started.\n---------------------------------------------------------");


        while (localDatacenter.hasPendingTasks() || localDatacenter.hasUnfinishedTransfers() || remoteDatacenter.hasPendingTasks() || remoteDatacenter.hasUnfinishedTransfers()) {
//            System.out.println("Current Time is: " + currentTime);

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
//            localDatacenter.printSystemStatus(currentTime);

            remoteDatacenter.handleTaskArrival(currentTime, milestoneTracker);
            remoteDatacenter.handleTaskCompletion(currentTime, milestoneTracker);
//            remoteDatacenter.printSystemStatus(currentTime);

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

        // stats analysis


//        // Write full milestones to CSV
        milestoneTracker.writeToCSVFull("task_milestonesFull.csv");

        // Write Data Transferred & Total Completion Time (considering transfer time cost) ONLY to CSV
//        milestoneTracker.writeToCSVImportant("task_milestonesImportant.csv");

//        System.out.println(remoteDatacenter.getEventQueue());
        System.out.println("\nSimulation complete.");
    }

}
