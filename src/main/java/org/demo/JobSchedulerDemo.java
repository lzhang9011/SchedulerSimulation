package org.demo;

public class JobSchedulerDemo {
    public static void main(String[] args) {
        String datacenterFile = "clusters.json";
        Scheduler scheduler = new Scheduler(datacenterFile);

        int i = 1;
        int[] steps = {60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};

        scheduler.runSimulation(100);
//        for (int loopCount = 0; loopCount < 20; loopCount++) {
//            scheduler.runSimulation(i);
//            i += steps[loopCount];
//        }
    }

}
