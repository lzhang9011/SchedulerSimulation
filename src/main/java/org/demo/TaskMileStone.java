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
        int actualWaitedTime;

        Milestone(Task task, int eventualArrivalTime, boolean transferred, double dataTransferred) {
            this.taskId = task.getId();
            this.originalArrivalTime = task.getOriginalArrivalTime();
            this.eventualArrivalTime = eventualArrivalTime;
            this.cpuRequired = task.getCpuRequirement();
            this.transferred = transferred;
            this.actualCompletionTimeStamp = task.getCompletionTimeStamp();
            this.originalDuration = task.getDuration();
            this.actualDuration = task.getCompletionTimeStamp() - task.getOriginalArrivalTime();
            this.dataTransferred = dataTransferred;
            this.dataLoad = task.getDataLoad();
            this.transferCompletionTime = task.getTransferCompletionTime();

            this.actualWaitedTime = task.getCurrentWaitTime();
        }
    }

    private final List<Milestone> milestones = new ArrayList<>();

    public void recordMilestone(Task task, int eventualArrivalTime, boolean transferred, double dataTransferred) {
        milestones.add(new Milestone(task, eventualArrivalTime, transferred, dataTransferred));
    }

    public void clearMilestones() {
        milestones.clear();
    }

    public void writeToCSVFull(String s, int factor) {
        String filePath = s.replace(".csv", "_" + factor + ".csv");
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write("task_id,original_arrival_time,eventual_arrival_time,cpu_required,transferred,actual_completion_timestamp,original_duration,actual_duration,data_transferred,data_load,transfer_completion_time, actual_waited_time\n");
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
                        milestone.actualWaitedTime + "\n");
            }
//            System.out.println("✅ Milestone data written to " + filePath);
        } catch (IOException e) {
            System.out.println("❌ Error writing to CSV: " + e.getMessage());
        }
    }

}

