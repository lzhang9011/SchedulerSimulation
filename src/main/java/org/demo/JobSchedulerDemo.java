package org.demo;

public class JobSchedulerDemo {
    public static void main(String[] args) {
        String datacenterFile = "clusters.json";
        Scheduler scheduler = new Scheduler(datacenterFile);

        // once the scheduler runs the simulation, the following will happen:
        /* 1. datacenters are generated and loaded to my simulation
        * 2. sample dataset is loaded from the job trace csv file
        * 3. running the discrete time stepped simulation
        * */
        scheduler.runSimulation();
    }
}
