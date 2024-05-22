package org.example;

import java.util.Map;

public class Region {
    private int numOfCPUs;
    private int regionID;
    private int diskSize;
    private Map<String, Double> dataDistributionMap;
    private Map<String, Integer> dataSizeMap;
    private Map<Integer, Integer> connectivityMap;



    public Region(int numOfCPUs, int regionID, int diskSize, Map<String, Double> dataDistributionMap, Map<String, Integer> dataSizeMap, Map<Integer, Integer> connectivityMap) {
        this.numOfCPUs = numOfCPUs;
        this.regionID = regionID;
        this.diskSize = diskSize;
        this.dataDistributionMap = dataDistributionMap;
        this.dataSizeMap = dataSizeMap;
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

    public Map<String, Integer> getDataSizeMap() {
        return dataSizeMap;
    }

    public void setDataSizeMap(Map<String, Integer> dataSizeMap) {
        this.dataSizeMap = dataSizeMap;
    }

    public Map<Integer, Integer> getConnectivityMap() {
        return connectivityMap;
    }
    public void setConnectivityMap(Map<Integer, Integer> connectivityMap) {
        this.connectivityMap = connectivityMap;
    }

    public int getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Region{");
        sb.append("numOfCPUs=").append(numOfCPUs).append(", ");
        sb.append("diskSize=").append(diskSize).append(", ");
        sb.append("regionID=").append(regionID).append(", ");
        sb.append("dataDistributionMap={");
        for (Map.Entry<String, Double> entry : dataDistributionMap.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue();
            String formattedValue = String.format("%.2f", value); // Format value to have two digits after the decimal point
            sb.append(key).append("=").append(formattedValue).append(", ");
        }
        sb.append("}, ");
        sb.append("dataSizeMap={");
        for (Map.Entry<String, Integer> entry : dataSizeMap.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            sb.append(key).append("=").append(value).append(", ");
        }
        sb.append("}, ");
        sb.append("connectivityMap={");
        for (Map.Entry<Integer, Integer> entry : connectivityMap.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            sb.append(key).append("=").append(value).append(", ");
        }
        sb.append("}");
        sb.append("}");
        return sb.toString();
    }


}
