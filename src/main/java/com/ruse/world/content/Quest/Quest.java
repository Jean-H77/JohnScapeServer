package com.ruse.world.content.Quest;

import lombok.Data;


@Data
public abstract class Quest {
    protected final String questTitle;
    protected QuestStep currentStep;
    protected final QuestStep[] steps;
    protected boolean isCompleted;

    public QuestStep getStep(int stepNumber) {
        return steps[stepNumber];
    }
}
