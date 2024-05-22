package org.example;

import java.util.Random;

public class CompletionTimeGenerator {
    public static int generateTimeOfCompletion(int distributionChoice) {

        if (distributionChoice == 1) {
            double mean = 5.0;
            double stdDev = 2.0;
            int low = 1;
            int high = 10;
            NormalDistribution distribution = new NormalDistribution(mean, stdDev, low, high);
            return distribution.generateRandomNumber();
        }
        else if (distributionChoice == 2) {
            int lower = 1;
            int upper = 10;
            DiscreteUniformDistribution distribution = new DiscreteUniformDistribution(lower, upper);
            return distribution.generateRandomNumber();
        }
        else if (distributionChoice == 3) {
            double lambda = 2.5;
            PoissonDistribution distribution = new PoissonDistribution(lambda);
            return distribution.generateRandomNumber();
        }
        else {
            System.out.println("Invalid choice.");
            return 0;
        }
    }

    static class NormalDistribution {
        private double mean;
        private double stdDev;
        private int low;
        private int high;
        private Random random;

        public NormalDistribution(double mean, double stdDev, int low, int high) {
            this.mean = mean;
            this.stdDev = stdDev;
            this.low = low;
            this.high = high;
            this.random = new Random();
        }

        public int generateRandomNumber() {
            double normalValue;
            do {
                // Generate random numbers following a normal distribution
                double u1 = random.nextDouble(); // Uniform(0,1) random doubles
                double u2 = random.nextDouble();
                double z0 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2); // Box-Muller transform

                // Scale and shift the numbers to match the desired mean and standard deviation
                normalValue = mean + stdDev * z0;
            } while (normalValue < low || normalValue > high);

            return (int) Math.round(normalValue);
        }
    }

    static class DiscreteUniformDistribution {
        private int lower;
        private int upper;
        private Random random;

        public DiscreteUniformDistribution(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
            this.random = new Random();
        }
        public int generateRandomNumber() {
            return random.nextInt(upper - lower + 1) + lower;
        }
    }

    static class PoissonDistribution {
        private double lambda;
        private Random random;

        public PoissonDistribution(double lambda) {
            this.lambda = lambda;
            this.random = new Random();
        }

        public int generateRandomNumber() {
            double L = Math.exp(-lambda);
            double p = 1.0;
            int k = 0;

            do {
                k++;
                p *= random.nextDouble();
            } while (p > L);

            return Math.min(k - 1, 10);
        }
    }
}


