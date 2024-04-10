package org.example;

public class RegionsInfo {
    private int totalDataSize;
    private int numOfRegions;

    public RegionsInfo(int totalDataSize, int numOfRegions) {
        this.totalDataSize = totalDataSize;
        this.numOfRegions = numOfRegions;
    }
    public int getTotalDataSize() {
        return totalDataSize;
    }
    public int getNumOfRegions() {
        return numOfRegions;
    }
}

