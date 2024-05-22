package org.example;

public class GlobalScreenshotPart {
    private boolean[] cpuStatus;
    private int unassignedTasks;
    private int assignedTasks;
    private int totalTasks;
    private double pendingPortions;

    public GlobalScreenshotPart(boolean[] cpuStatus, int unassignedTasks, int assignedTasks, int totalTasks, double pendingPortions) {
        this.cpuStatus = new boolean[cpuStatus.length];
        for (int i = 0; i < cpuStatus.length; i++) {
            this.cpuStatus[i] = cpuStatus[i];
        }
        this.unassignedTasks = unassignedTasks;
        this.assignedTasks = assignedTasks;
        this.totalTasks = totalTasks;
        this.pendingPortions = pendingPortions;
    }
    public boolean[] getCpuStatus() {
        return cpuStatus;
    }
    public void setCpuStatus(boolean[] cpuStatus) {
        this.cpuStatus = cpuStatus;
    }
    public int getUnassignedTasks() {
        return unassignedTasks;
    }
    public void setUnassignedTasks(int unassignedTasks) {
        this.unassignedTasks = unassignedTasks;
    }
    public int getAssignedTasks() {
        return assignedTasks;
    }
    public void setAssignedTasks(int assignedTasks) {
        this.assignedTasks = assignedTasks;
    }
    public int getTotalTasks() {
        return totalTasks;
    }
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public double getPendingPortions() {
        return pendingPortions;
    }
    public void setPendingPortions(double pendingPortions) {
        this.pendingPortions = pendingPortions;
    }
}
