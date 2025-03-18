package org.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TaskMilestone {
    private static class Milestone {
        int taskId;
        int originalArrivalTime;
        int eventualArrivalTime;
        int cpuRequired;
        boolean transferred;
        int actualCompletionTimeStamp;
        int originalDuration;
        int actualDuration;
        double dataTransferred;
        double dataLoad;
        int transferCompletionTime; // how long it took to transfer
        double cost;

        Milestone(Task task, int eventualArrivalTime, boolean transferred, double dataTransferred, double cost) {
            this.taskId = task.getId();
            this.originalArrivalTime = task.getOriginalArrivalTime();
            this.eventualArrivalTime = eventualArrivalTime;
            this.cpuRequired = task.cpuRequirement;
            this.transferred = transferred;
            this.actualCompletionTimeStamp = task.getCompletionTimeStamp();
            this.originalDuration = task.getDuration();
            this.actualDuration = task.getCompletionTimeStamp() - task.originalArrivalTime;
            this.dataTransferred = dataTransferred;
            this.dataLoad = task.dataLoad;
            this.transferCompletionTime = task.getTransferCompletionTime();
            this.cost = task.getCost();
        }
    }

    private final List<Milestone> milestones = new ArrayList<>();

    public void recordMilestone(Task task, int eventualArrivalTime, boolean transferred, double dataTransferred, double cost) {
        milestones.add(new Milestone(task, eventualArrivalTime, transferred, dataTransferred, cost));
    }

    public void clearMilestones() {
        milestones.clear();
    }

    // returns the total observation period T of this specific interval.
    public int writeToCSVFull(String s, int interval) {
        String filePath = s.replace(".csv", "_" + interval + ".csv");
        int maxCompletionTime = Integer.MIN_VALUE;
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write("task_id,original_arrival_time,eventual_arrival_time,cpu_required,transferred,actual_completion_timestamp,original_duration,actual_duration,data_transferred,data_load,transfer_completion_time, cost\n");
            for (Milestone milestone : milestones) {
                writer.write(milestone.taskId + "," +
                        milestone.originalArrivalTime + "," +
                        milestone.eventualArrivalTime + "," +
                        milestone.cpuRequired + "," +
                        milestone.transferred + "," +
                        milestone.actualCompletionTimeStamp + "," +
                        milestone.originalDuration + "," +
                        milestone.actualDuration + "," +
                        String.format("%.2f", milestone.dataTransferred) + "," +
                        String.format("%.2f", milestone.dataLoad) + "," +
                        milestone.transferCompletionTime + "," +
                        milestone.cost + "\n");

                if (milestone.actualCompletionTimeStamp > maxCompletionTime) {
                    maxCompletionTime = milestone.actualCompletionTimeStamp;
                }
            }
//            System.out.println("✅ Milestone data written to " + filePath);
        } catch (IOException e) {
            System.out.println("❌ Error writing to CSV: " + e.getMessage());
        }
        return maxCompletionTime == Integer.MIN_VALUE ? 0 : maxCompletionTime;
    }

    public void writeToCSVImportant(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("task_id,transferred,original_duration,actual_duration, data_transferred\n");
            for (Milestone milestone : milestones) {
                writer.write(milestone.taskId + "," +
                        milestone.transferred + "," +
                        milestone.originalDuration + "," +
                        milestone.actualDuration + "," +
                        milestone.dataTransferred + "\n");
            }
            System.out.println("✅ Milestone data written to " + filePath);
        } catch (IOException e) {
            System.out.println("❌ Error writing to CSV: " + e.getMessage());
        }
    }
}

