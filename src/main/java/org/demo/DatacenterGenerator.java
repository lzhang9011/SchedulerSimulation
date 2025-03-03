package org.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.Cluster;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DatacenterGenerator {
    private static final String FILE_NAME = "clusters.json";

    public static List<Datacenter> generateDatacenters() {
        List<Datacenter> datacenters = new ArrayList<>();

        datacenters.add(new Datacenter(0,0));
        datacenters.add(new Datacenter(1,64));

        return datacenters;
    }

    public static void writeDatacentersToJson(List<Datacenter> datacenters) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(datacenters, writer);
            System.out.println("Clusters saved to " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Datacenter> readDatacentersFromJson() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Type datacenterListType = new TypeToken<List<Datacenter>>() {}.getType();
            return gson.fromJson(reader, datacenterListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
