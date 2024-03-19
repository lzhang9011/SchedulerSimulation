package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MultiRegionGenerator {



    public static void generateRegionsInJSON(String filename, int n) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Region[] regions = new Region[n];

        // Generate 9 Regions with even distribution
        double remainingPercentage = 100.0;
        for (int i = 0; i < n - 1; i++) {
            int numOfCPUs = getRandomNumOfCPUs();

            double dataDistribution = getRandomDataDistribution(remainingPercentage);
            remainingPercentage -= dataDistribution;
            Map<Integer, Integer> connectivityMap = generateConnectivityMap(i, n);

            Region region = new Region(numOfCPUs, i, dataDistribution, connectivityMap);
            regions[i] = region;
        }

        // Set the last region's data distribution to the remaining value with two decimal places
        int numOfCPUs = getRandomNumOfCPUs();
        int regionID = n - 1;
        double lastDataDistribution = Math.round(remainingPercentage * 100.0) / 100.0; // Round to two decimal places
        Map<Integer, Integer> connectivityMap = generateConnectivityMap(regionID, n);
        regions[regionID] = new Region(numOfCPUs, regionID, lastDataDistribution, connectivityMap);

        // Write JSON file
        try (FileWriter file = new FileWriter(filename)) {
            gson.toJson(regions, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getRandomNumOfCPUs() {
        int[] cpuOptions = {1, 2, 4, 8};
        Random random = new Random();
        return cpuOptions[random.nextInt(cpuOptions.length)];
    }

    private static double getRandomDataDistribution(double remainingPercentage) {
        Random random = new Random();
        double distribution = random.nextDouble() * remainingPercentage;
        return Math.round(distribution * 100.0) / 100.0; // Round to two decimal places
    }

    private static Map<Integer, Integer> generateConnectivityMap(int regionID, int n) {
        Map<Integer, Integer> connectivityMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            if (i != regionID) {
                connectivityMap.put(i, getRandomBandwidth());
            }
        }
        return connectivityMap;
    }

    private static int getRandomBandwidth() {
        Random random = new Random();
        return random.nextInt(1001); // Random number between 0 to 1000
    }

    public static void main(String[] args) {

        int numRegion = 10;
        generateRegionsInJSON("regions.json", numRegion); // generate JSON file


    }

}

