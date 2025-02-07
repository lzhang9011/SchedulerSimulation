package org.example;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ElapsedSecondsCalculator {
    private static final String BASE_TIME_STRING = "2020-03-18 04:01:39";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long BASE_EPOCH_SECONDS = LocalDateTime.parse(BASE_TIME_STRING, FORMATTER)
            .toEpochSecond(ZoneOffset.UTC);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a timestamp (yyyy-MM-dd HH:mm:ss): ");
        String inputTimestamp = scanner.nextLine();
        scanner.close();

        try {
            long elapsedSeconds = computeElapsedSeconds(inputTimestamp);
            System.out.println("Elapsed seconds since " + BASE_TIME_STRING + ": " + elapsedSeconds);
        } catch (Exception e) {
            System.out.println("Invalid timestamp format. Please enter in 'yyyy-MM-dd HH:mm:ss' format.");
        }
    }

    public static long computeElapsedSeconds(String timestamp) {
        LocalDateTime inputTime = LocalDateTime.parse(timestamp, FORMATTER);
        long inputEpochSeconds = inputTime.toEpochSecond(ZoneOffset.UTC);
        return inputEpochSeconds - BASE_EPOCH_SECONDS;
    }
}

