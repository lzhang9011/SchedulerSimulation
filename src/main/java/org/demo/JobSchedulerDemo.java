package org.demo;

public class JobSchedulerDemo {
    public static void main(String[] args) {
        String datacenterFile = "clusters.json";
        Scheduler scheduler = new Scheduler(datacenterFile);

        double[] tmpFactors = {0.1, 0.3, 0.5, 0.7, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 6.0};
//        double[] bandwidths = {3.0, 125.0};
//        double[] bandwidths = {3.0};
//        double[] bandwidths = {6.0};
//        double[] bandwidths = {10.0};

//        double[] bandwidths = {30.0};
//        double[] bandwidths = {125.0};
        double[] bandwidths = {3.0, 6.0, 10.0};


        for (double bandwidth : bandwidths) {
            for (double tmpFactor : tmpFactors) {
                scheduler.runSimulation(tmpFactor, bandwidth);
            }
        }

    }

}
