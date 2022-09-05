package com.ruse.world.content.Quest;

import lombok.Data;

@Data
public class QuestStep {
    private final String description;
    private final int stepNumber;
    private boolean isCompleted;
}
