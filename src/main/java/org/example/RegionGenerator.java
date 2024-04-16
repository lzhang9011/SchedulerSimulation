package org.example;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class RegionGenerator {

    public static List<Region> generateRegions() {
        List<Region> regions = new ArrayList<>();
        Random random = new Random();

        // Generate regions
        for (int i = 0; i < 10; i++) {
            int numOfCPUs = getRandomCPUs();
            //generate an empty dataDistributionMap for each region, for now
            Map<String, Double> dataDistributionMap = new HashMap<>();
            Map<Integer, Integer> connectivityMap = new HashMap<>();
            Region region = new Region(numOfCPUs, i, dataDistributionMap, connectivityMap);
            regions.add(region);
        }
        // -------------------------------------------------------------------------------------
        // Generate tmp map for each key (X: R0->P3, R7->P2, P8->P1; Y: R5->P2, R9->P2, P1->P1;)
        String[] keys = {"X", "Y", "Z"};
        List<Map<Integer, Double>> tmpMapList = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            int k = random.nextInt(10) + 1;
            Map<Integer, Double> tmpMap = generateTmpMap(regions, k);
            tmpMapList.add(tmpMap);
        }
        // generate map that can fit into (update) region class's member dataDistributionMap.
        for (Region region : regions) {
            Map<String, Double> dataDistributionMap = new HashMap<>();

            for (int i = 0; i < tmpMapList.size(); i++) {
                Map<Integer, Double> tmpMap = tmpMapList.get(i);
                String key = keys[i];

                // Check if the tmpMap contains a value for the current region
                if (tmpMap.containsKey(region.getRegionID())) {
                    // Retrieve the value associated with the region ID
                    Double value = tmpMap.get(region.getRegionID());
                    // Add the entry to the dataDistributionMap
                    dataDistributionMap.put(key, value);
                }
            }

            // Update the region's dataDistributionMap
            region.setDataDistributionMap(dataDistributionMap);
        }
        // ----------------------------------------------
        // generate connectivityMap for each region
        for (Region region : regions) {
            Map<Integer, Integer> connectivityMap = generateConnectivityMap(region.getRegionID());
            region.setConnectivityMap(connectivityMap);
        }

        return regions;
    }
    private static Map<Integer, Double> generateTmpMap(List<Region> regions, int k){
        //First, generate normally distributed double array
        double[] result = generateNormalDistributionArray(k);

        //second, shuffle regions.
        List<Region> tmpRegions = new ArrayList<>(regions);
        Collections.shuffle(tmpRegions);

        //third, build tmp map.
        Map<Integer, Double> tmpMap = new HashMap<>();
        for (int i = 0; i < Math.min(k, tmpRegions.size()); i++) {
            Region region = tmpRegions.get(i);
            int regionID = region.getRegionID();
            double value = result[i];
            tmpMap.put(regionID, value);
        }

        // just checking...
//        for (Map.Entry<Integer, Double> entry : tmpMap.entrySet()) {
//            int regionID = entry.getKey();
//            double value = entry.getValue();
//            String formattedValue = String.format("%.1f", value); // Format value to have one digit after the decimal point
//            System.out.println("RegionID: " + regionID + ", Value: " + formattedValue);
//        }
        return tmpMap;
    }
    //all good
    private static double[] generateNormalDistributionArray(int k) {
        double[] array = new double[k];
        Random random = new Random();
        double sum = 0;

        // Generate random values following a normal distribution
        for (int i = 0; i < k; i++) {
            array[i] = random.nextGaussian();
            sum += array[i];
        }

        // Normalize values to ensure they are within the range (0, 100)
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (double value : array) {
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
        }
        double range = max - min;
        for (int i = 0; i < k; i++) {
            array[i] = (array[i] - min) / range * 99.0 + 0.5;
        }

        // Adjust values to ensure the sum is exactly 100
        double currentSum = Arrays.stream(array).sum();
        double scaleFactor = 100 / currentSum;
        for (int i = 0; i < k; i++) {
            array[i] *= scaleFactor;
        }

        return array;
    }
    private static Map<Integer, Integer> generateConnectivityMap(int selfRegionID) {
        Random random = new Random();
        Map<Integer, Integer> connectivityMap = new HashMap<>();
        for (int i = 0; i < 10; i++) { // Assuming 10 regions
            if (i != selfRegionID) { // Exclude self region
                connectivityMap.put(i, random.nextInt(401) + 100); // Random integer between 100 and 500
            }
        }
        return connectivityMap;
    }


    private static int getRandomCPUs() {
        Random random = new Random();
        int[] cpus = {1, 2, 4, 8};
        return cpus[random.nextInt(cpus.length)];
    }

    private static Map<String, Double> getRandomDataDistribution() {
        Map<String, Double> dataDistribution = new HashMap<>();
        return dataDistribution;
    }

    private static double getRandomValue(Random random) {
        double[] values = {0.14, 15.27, 68.26};
        return values[random.nextInt(values.length)];
    }

    public static void main(String[] args) {
        List<Region> regions = RegionGenerator.generateRegions();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(regions);

        // Write JSON to file
        try (FileWriter writer = new FileWriter("regions.json")) {
            writer.write(json);
            System.out.println("Regions have been written to regions.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
