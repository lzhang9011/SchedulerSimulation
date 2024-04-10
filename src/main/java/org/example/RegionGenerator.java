package org.example;import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RegionGenerator {

    public static List<Region> generateRegions() {
        List<Region> regions = new ArrayList<>();
        Random random = new Random();

        // Generate regions
        for (int i = 0; i < 10; i++) {
            int numOfCPUs = getRandomCPUs();
            Map<String, Double> dataDistribution = getRandomDataDistribution();
            Region region = new Region(numOfCPUs, i);
            List<Map<String, Double>> dataDistributionList = new ArrayList<>();
            dataDistributionList.add(dataDistribution);
            region.setDataDistributionList(dataDistributionList);
            regions.add(region);
        }

        // Shuffle the regions for each key to make distribution random
        shuffleRegionsForKeys(regions, "X", random);
        shuffleRegionsForKeys(regions, "Y", random);
        shuffleRegionsForKeys(regions, "Z", random);

        return regions;
    }

    private static void shuffleRegionsForKeys(List<Region> regions, String key, Random random) {
        List<Region> shuffledRegions = new ArrayList<>(regions);
        Collections.shuffle(shuffledRegions, random);
        int keyCount = 0;
        for (Region region : shuffledRegions) {
            if (keyCount < 3) {
                Map<String, Double> dataDistribution = region.getDataDistributionList().get(0);
                if (!dataDistribution.containsKey(key)) {
                    double value = getRandomValue(random);
                    dataDistribution.put(key, value);
                    keyCount++;
                }
            } else {
                break;
            }
        }
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
        List<Region> regions = generateRegions();
        for (Region region : regions) {
            System.out.println(region);
        }
    }
}
