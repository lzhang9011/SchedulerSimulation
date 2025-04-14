package org.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TaskMilestone {
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
