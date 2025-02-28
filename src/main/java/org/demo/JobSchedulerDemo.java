package org.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JobSchedulerDemo {
    public static void main(String[] args) {

        Scheduler scheduler = new Scheduler();
        Task task1 = new Task(1,1,5,8,200);
        Task task2 = new Task(2,2,1,1,100);
        Task task3 = new Task(3,3,2,12,1000); // transfer
        Task task4 = new Task(4,4,2,12,100);
        Task task5 = new Task(5,5,2,32,500); // transfer


        //add to local datacenter's eventQueue.
        scheduler.addTask(task1);
        scheduler.addTask(task2);
        scheduler.addTask(task3);
        scheduler.addTask(task4);
        scheduler.addTask(task5);

        scheduler.runSimulation();
    }
}
