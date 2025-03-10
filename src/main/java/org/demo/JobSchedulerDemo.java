package org.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JobSchedulerDemo {
    public static void main(String[] args) {
        String datacenterFile = "clusters.json";
        Scheduler scheduler = new Scheduler(datacenterFile);
        int sampleSize = getSampleSizeFromUser();
        List<List<Integer>> allResults = new ArrayList<>();

        // run the simulation with the following intervals: {1, 4, 7}
        for (int i = 1; i < 10; i += 3) {
            scheduler.runSimulation(i, sampleSize);
            allResults.add(new ArrayList<>(scheduler.resultList));
        }

        // stats to compute system load:
        writeResultsToCSV(allResults, "results.csv");

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
        return sampleSize;
    }

    private static void writeResultsToCSV(List<List<Integer>> results, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.write("run_id,total_demand,total_resource,observation_period\n");

            // Write data
            int runId = 1;
            for (List<Integer> result : results) {
                writer.write(runId + "," + result.get(0) + "," + result.get(1) + "," + result.get(2) + "\n");
                runId++;
            }

            System.out.println(" Results written to " + filename);
        } catch (IOException e) {
            System.out.println(" Error writing to CSV: " + e.getMessage());
        }
    }
}
