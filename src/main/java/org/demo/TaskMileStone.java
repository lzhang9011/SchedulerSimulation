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
        boolean transferred;
        int actualCompletionTimeStamp;
        int originalDuration;
        int actualDuration;
        int dataTransferred;
        int transferCompletionTime; // how long it took to transfer

        Milestone(Task task, int eventualArrivalTime, boolean transferred, int dataTransferred) {
            this.taskId = task.getId();
            this.originalArrivalTime = task.getOriginalArrivalTime();
            this.eventualArrivalTime = eventualArrivalTime;
            this.transferred = transferred;
            this.actualCompletionTimeStamp = task.getCompletionTimeStamp();
            this.originalDuration = task.getDuration();
            this.actualDuration = task.getCompletionTimeStamp() - task.originalArrivalTime;
            this.dataTransferred = dataTransferred;
            this.transferCompletionTime = task.getTransferCompletionTime();
        }
    }

    private final List<Milestone> milestones = new ArrayList<>();

    public void recordMilestone(Task task, int eventualArrivalTime, boolean transferred, int dataTransferred) {
        milestones.add(new Milestone(task, eventualArrivalTime, transferred, dataTransferred));
    }

    public void writeToCSVFull(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("task_id,original_arrival_time,eventual_arrival_time,transferred,actual_completion_time,original_duration,actual_duration, data_transferred, transfer_completion_time\n");
            for (Milestone milestone : milestones) {
                writer.write(milestone.taskId + "," +
                        milestone.originalArrivalTime + "," +
                        milestone.eventualArrivalTime + "," +
                        milestone.transferred + "," +
                        milestone.actualCompletionTimeStamp + "," +
                        milestone.originalDuration + "," +
                        milestone.actualDuration + "," +
                        milestone.dataTransferred + "," +
                        milestone.transferCompletionTime + "\n");
            }
            System.out.println("✅ Milestone data written to " + filePath);
        } catch (IOException e) {
            System.out.println("❌ Error writing to CSV: " + e.getMessage());
        }
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

