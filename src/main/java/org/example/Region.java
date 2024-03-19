package org.example;
import java.util.HashMap;
import java.util.Map;

public class Region {
    private int numOfCPUs;
    private int regionID;
    private Map<Integer, Integer> connectivityMap;
    private double dataDistribution; // Represented in percentage (0% - 100%)
    // TODO: change to list/map of partiions



    // TODO: queue of parition / task

    // TODO: methods like query data distribution


    public Region(int numOfCPUs, int regionID, double dataDistribution, Map<Integer, Integer> connectivityMap) {
        this.numOfCPUs = numOfCPUs;
        this.regionID = regionID;
        this.dataDistribution = dataDistribution;
        this.connectivityMap = connectivityMap;
    }

    public void addConnection(int connectedRegionID, int bandwidth) {
        connectivityMap.put(connectedRegionID, bandwidth);
    }

    public int getNumOfCPUs() {
        return numOfCPUs;
    }

    public void setNumOfCPUs(int numOfCPUs) {
        this.numOfCPUs = numOfCPUs;
    }

    public int getRegionID() {
        return regionID;
    }

    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }

    public double getDataDistribution() {
        return dataDistribution;
    }

    public void setDataDistribution(double dataDistribution) {
        this.dataDistribution = dataDistribution;
    }

    public Map<Integer, Integer> getConnectivityMap() {
        return connectivityMap;
    }

    @Override
    public String toString() {
        return "Region{" +
                "numOfCPUs=" + numOfCPUs +
                ", regionID=" + regionID +
                ", dataDistribution=" + dataDistribution +
                '}';
    }
}

