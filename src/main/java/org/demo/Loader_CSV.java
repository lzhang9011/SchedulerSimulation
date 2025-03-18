package org.demo;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Loader_CSV {

    public static void processCSV(String inputFile, String outputFile, int sampleSize) {
        List<String> completedEntries = new ArrayList<>();
        String header = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            header = reader.readLine(); // Read header
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 7 && "COMPLETED".equalsIgnoreCase(values[6].trim())) {
                    completedEntries.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return;
        }

        if (completedEntries.isEmpty()) {
            System.out.println("No COMPLETED entries found in the CSV file.");
            return;
        }

        int limit = Math.min(sampleSize, completedEntries.size());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }

            for (int i = 0; i < limit; i++) {
                writer.write(completedEntries.get(i));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing the file: " + e.getMessage());
        }
    }

}
