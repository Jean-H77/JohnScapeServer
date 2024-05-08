package com.ruse.world.content.strangertasks;

public class StrangerTask {

    public enum Difficulty { EASY, MEDIUM, HARD, ELITE}

    private final int itemId;

    private int amount;
    private boolean isCompleted;
    private boolean inProgress = true;

    public StrangerTask(int amount, int itemId) {
        this.amount = amount;
        this.itemId = itemId;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
        if(completed) {
            inProgress = false;
        }
    }

    public boolean inProgress() {
        return inProgress;
    }

    public int getItemId() {
        return itemId;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public void decrementAmount(int amount) {
        this.amount -= amount;
    }
}
