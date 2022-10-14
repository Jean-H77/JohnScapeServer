package com.ruse.world.content.achievementsystem;

public class PlayerAchievement {
    private int progress;
    private boolean completed;

    public int getProgress() {
        return this.progress;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
