package org.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DatacenterGenerator {
    private static final String FILE_NAME = "clusters.json";
    private static final String CSV_FILE = "cluster_log.csv";

    public static List<Datacenter> generateDatacenters() {
        List<Datacenter> datacenters = new ArrayList<>();
        int maxCpuGpuSum = getMaxCpuGpuSumFromCSV();

        datacenters.add(new Datacenter(0, maxCpuGpuSum));
        datacenters.add(new Datacenter(1, maxCpuGpuSum * 100));

        return datacenters;
    }



    public static void writeDatacentersToJson(List<Datacenter> datacenters) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(datacenters, writer);
            System.out.println("Clusters saved to " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Datacenter> readDatacentersFromJson() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Type datacenterListType = new TypeToken<List<Datacenter>>() {}.getType();
            return gson.fromJson(reader, datacenterListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static int getMaxCpuGpuSumFromCSV() {
        int maxSum = Integer.MIN_VALUE;
        String delimiter = ",";
        int gpuIndex = -1, cpuIndex = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            boolean isHeader = true;
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);

                if (isHeader) {
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].trim().equalsIgnoreCase("gpu_num")) {
                            gpuIndex = i;
                        } else if (values[i].trim().equalsIgnoreCase("cpu_num")) {
                            cpuIndex = i;
                        }
                    }
                    if (gpuIndex == -1 || cpuIndex == -1) {
                        System.out.println("Error: Columns gpu_num or cpu_num not found.");
                        return 16; // Default fallback
                    }
                    isHeader = false;
                    continue;
                }

                try {
                    int gpuNum = Integer.parseInt(values[gpuIndex].trim());
                    int cpuNum = Integer.parseInt(values[cpuIndex].trim());
                    int sum = gpuNum + cpuNum;
                    maxSum = Math.max(maxSum, sum);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Skipping invalid row: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return 16; // Default fallback
        }
        return maxSum == Integer.MIN_VALUE ? 16 : maxSum; // Default to 16 if no valid data found
    }
}
