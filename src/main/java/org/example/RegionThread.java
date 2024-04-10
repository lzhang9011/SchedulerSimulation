package org.example;

public class RegionThread extends Thread {
    private Region region;

    public RegionThread(Region region) {
        this.region = region;
    }

    @Override
    public void run() {
        System.out.println("Region " + region.getRegionID() + " started with " + region.getNumOfCPUs() + " CPUs.");
        for (int i = 1; i <= 2; i++) {
            System.out.println("Region " + region.getRegionID() + " - Number: " + i);
            try {
                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

