package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Loader_CSV {

    public static void processCSV(String inputFile, String outputFile, int sampleSize) {
        List<String> completedEntries = new ArrayList<>();
        String header = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            header = reader.readLine();
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

        Collections.shuffle(completedEntries);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }

            int limit = Math.min(sampleSize, completedEntries.size());
            for (int i = 0; i < limit; i++) {
                writer.write(completedEntries.get(i));
                writer.newLine();
            }

            System.out.println("Successfully created " + outputFile + " with " + limit + " randomly selected entries.");
        } catch (IOException e) {
            System.err.println("Error writing the file: " + e.getMessage());
        }
    }
}
