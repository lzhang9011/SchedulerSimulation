package org.example;

public class GlobalScreenshot {
    private GlobalScreenshotPart[] parts;


    public GlobalScreenshot(GlobalScreenshotPart[] parts) {
        this.parts = parts;
    }
    public GlobalScreenshotPart[] getParts() {
        return parts;
    }
    public void setParts(GlobalScreenshotPart[] parts) {
        this.parts = parts;
    }



    public void printDetails() {
        System.out.println("Global Screenshot Details:---------------------------------");
        for (int i = 0; i < parts.length; i++) {
            GlobalScreenshotPart part = parts[i];
            System.out.println("Region " + i + ":");
            System.out.println("CPU Status:");
            boolean[] cpuStatus = part.getCpuStatus();
            StringBuilder cpuStatusBuilder = new StringBuilder();
            for (int j = 0; j < cpuStatus.length; j++) {
//                System.out.println("CPU " + j + ": " + cpuStatus[j]);
                cpuStatusBuilder.append("CPU ").append(j).append(": ").append(cpuStatus[j]).append("  ");
            }
            System.out.println(cpuStatusBuilder.toString());
            double pendingPortions = Math.round(part.getPendingPortions() * 100.0) / 100.0;
            double completedPortions = Math.round((part.getTotalTasks() - pendingPortions) * 100.0) / 100.0;
//            double completedPortions = part.getTotalTasks() - pendingPortions;
            System.out.println("Unassigned Tasks: " + part.getUnassignedTasks());
            System.out.println("Assigned Tasks: " + part.getAssignedTasks());
            System.out.println("Pending Portion: " + pendingPortions);
            System.out.println("Current Progress: " + completedPortions + "/" + part.getTotalTasks());

        }
    }
}
