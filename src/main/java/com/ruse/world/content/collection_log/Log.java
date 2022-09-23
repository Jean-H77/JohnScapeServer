package com.ruse.world.content.collection_log;

import com.ruse.model.Item;

public class Log {

    private final String name;
    private final LogType logType;
    private final int[] requiredItemIds;
    private final Item[] rewards;

    public Log(String name, LogType logType, int[] requiredItemIds, Item[] rewards) {
        this.name = name;
        this.logType = logType;
        this.requiredItemIds = requiredItemIds;
        this.rewards = rewards;
    }

    public String getName() {
        return name;
    }

    public Item[] getRewards() {
        return rewards;
    }

    public int[] getRequiredItemIds() {
        return requiredItemIds;
    }

    public LogType getLogType() {
        return logType;
    }
}
