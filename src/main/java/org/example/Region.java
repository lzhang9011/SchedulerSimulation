package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Region {
    private int numOfCPUs;
    private int regionID;
    private List<Map<String, Double>> dataDistributionList;

    public Region(int numOfCPUs, int regionID) {
        this.numOfCPUs = numOfCPUs;
        this.regionID = regionID;
        this.dataDistributionList = new ArrayList<>();
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

    public List<Map<String, Double>> getDataDistributionList() {
        return dataDistributionList;
    }

    public void setDataDistributionList(List<Map<String, Double>> dataDistributionList) {
        this.dataDistributionList = dataDistributionList;
    }

    @Override
    public String toString() {
        return "Region{" +
                "numOfCPUs=" + numOfCPUs +
                ", regionID=" + regionID +
                ", dataDistributionList=" + dataDistributionList +
                '}';
    }
}
