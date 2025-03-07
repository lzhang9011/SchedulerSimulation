package org.demo;

import java.util.Scanner;

public class JobSchedulerDemo {
    public static void main(String[] args) {
        String datacenterFile = "clusters.json";
        Scheduler scheduler = new Scheduler(datacenterFile);
        int sampleSize = getSampleSizeFromUser();

        // once the scheduler runs the simulation, the following will happen:
        /* 1. datacenters are generated and loaded to my simulation
        * 2. sample dataset is loaded from the job trace csv file
        * 3. running the discrete time stepped simulation
        * */

        // run the simulation with the following intervals: {1, 4, 7}
        for (int i = 1; i < 10; i += 3) {
            scheduler.runSimulation(i, sampleSize);
        }

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
}
