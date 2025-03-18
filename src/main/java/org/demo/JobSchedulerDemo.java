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
        List<List<Long>> allResults = new ArrayList<>();

        // run the simulation with the following intervals: {1, 101, 201, 301, 401, 501, 601, 701, 801, 901, 1001, 1101, 1201, 1301, 1401, 1501, 1601, 1701, 1801, 1901}
        for (int i = 1; i <= 2000; i += 100) {
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
            System.out.print("Enter a sample size (1-170320): ");
            if (scanner.hasNextInt()) {
                sampleSize = scanner.nextInt();
                if (sampleSize >= 1 && sampleSize <= 170320) {
                    break;
                }
            } else {
                scanner.next();
            }
            System.out.println("Invalid input. Please enter a number between 1 and 170320.");
        }
        return sampleSize;
    }

    private static void writeResultsToCSV(List<List<Long>> results, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.write("run_id,total_demand,total_resource,observation_period\n");

            // Write data
            int runId = 1;
            for (List<Long> result : results) {
                writer.write(runId + "," + result.get(0) + "," + result.get(1) + "," + result.get(2) + "\n");
                runId++;
            }

            System.out.println(" Results written to " + filename);
        } catch (IOException e) {
            System.out.println(" Error writing to CSV: " + e.getMessage());
        }
    }
}
