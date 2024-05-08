package com.ruse.world.content.teleporter;

public enum TeleportCategory {
    SKILLING(0),
    BOSSES(1),
    MINIGAMES(2),
    DUNGEONS(3),
    GLOBALS(4),
    RAIDS(5)
    ;

    private final int configFrame;

    TeleportCategory(int configFrame) {
        this.configFrame = configFrame;
    }

    public int getConfigFrame() {
        return configFrame;
    }
}
