package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClusterGenerator {
    private static final String FILE_NAME = "clusters.json";

    public static List<Cluster> generateClusters() {
        List<Cluster> clusters = new ArrayList<>();

        clusters.add(new Cluster(16, 1, 10000000, 0, 0.0, 10, 0.0));
        clusters.add(new Cluster(64, 2, 10000000, 0, 0.0, 10, 0.0));

        return clusters;
    }

    public static void writeClustersToJson(List<Cluster> clusters) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(clusters, writer);
            System.out.println("Clusters saved to " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Cluster> readClustersFromJson() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Type clusterListType = new TypeToken<List<Cluster>>() {}.getType();
            return gson.fromJson(reader, clusterListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
