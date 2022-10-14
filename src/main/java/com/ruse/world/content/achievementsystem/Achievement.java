package com.ruse.world.content.achievementsystem;

public class Achievement {
    private final String description;
    private final int points;
    private final int completionAmount;

    public Achievement(String description, int points, int completionAmount) {
        this.description = description;
        this.points = points;
        this.completionAmount = completionAmount;
    }

    public String getDescription() {
        return this.description;
    }

    public int getPoints() {
        return this.points;
    }

    public int getCompletionAmount() {
        return this.completionAmount;
    }
}
