package org.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JobSchedulerDemo {
    public static void main(String[] args) {

        Scheduler scheduler = new Scheduler();
        Task task1 = new Task(1,1,5,8,2);
        Task task2 = new Task(2,2,2,12,4);
//        Task task3 = new Task(3,3,2,12,1);

        //add to local datacenter's eventQueue.
        scheduler.addTask(task1);
        scheduler.addTask(task2);
//        scheduler.addTask(task3);

        scheduler.runSimulation();
    }
}
