package com.ruse.world.content.skill.slayer;

import com.ruse.model.Position;

public enum SlayerDungeon {
    ETHEREAL_DUNGEON("Ethereal Dungeon",
            new SlayerTaskDetails(5, new Position(3333,3333)),
            new SlayerTaskDetails(6, new Position(3333,3333)),
            new SlayerTaskDetails(7, new Position(3333,3333)),
            new SlayerTaskDetails(8, new Position(3333,3333)),
            new SlayerTaskDetails(9, new Position(3333,3333))),

    DRAGONIC_DUNGEON("Dragonic Dungeon",
            new SlayerTaskDetails(5, new Position(3333,3333)),
            new SlayerTaskDetails(6, new Position(3333,3333)),
            new SlayerTaskDetails(7, new Position(3333,3333)),
            new SlayerTaskDetails(8, new Position(3333,3333)),
            new SlayerTaskDetails(9, new Position(3333,3333)));

    private final String name;
    private final SlayerTaskDetails[] slayerTaskDetails;

    SlayerDungeon(String name, SlayerTaskDetails... slayerTaskDetails) {
        this.name = name;
        this.slayerTaskDetails = slayerTaskDetails;
    }

    public SlayerTaskDetails[] getSlayerTaskDetails() {
        return slayerTaskDetails;
    }

    public String getName() {
        return name;
    }
}
