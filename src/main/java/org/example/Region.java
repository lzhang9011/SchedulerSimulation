package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Region {
    private int numOfCPUs;
    private int regionID;
    private Map<String, Double> dataDistributionMap;
    private Map<Integer, Integer> connectivityMap;

    public Region(int numOfCPUs, int regionID, Map<String, Double> dataDistributionMap, Map<Integer, Integer> connectivityMap) {
        this.numOfCPUs = numOfCPUs;
        this.regionID = regionID;
        this.dataDistributionMap = dataDistributionMap;
        this.connectivityMap = connectivityMap;

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

    public Map<String, Double> getDataDistributionMap() {
        return dataDistributionMap;
    }

    public void setDataDistributionMap(Map<String, Double> dataDistributionMap) {
        this.dataDistributionMap = dataDistributionMap;
    }

    public Map<Integer, Integer> getConnectivityMap() {
        return connectivityMap;
    }
    public void setConnectivityMap(Map<Integer, Integer> connectivityMap) {
        this.connectivityMap = connectivityMap;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RegionID: ").append(regionID).append(", ");
        sb.append("NumOfCPUs: ").append(numOfCPUs).append(", ");
        sb.append("DataDistributionMap: {");
        for (Map.Entry<String, Double> entry : dataDistributionMap.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue();
            String formattedValue = String.format("%.2f", value); // Format value to have two digits after the decimal point
            sb.append(key).append("=").append(formattedValue).append(", ");
        }
        sb.append("}");
        return sb.toString();
    }
}
