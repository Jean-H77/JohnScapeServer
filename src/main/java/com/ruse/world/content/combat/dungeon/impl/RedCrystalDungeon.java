package com.ruse.world.content.combat.dungeon.impl;

import com.ruse.model.Position;
import com.ruse.world.content.combat.dungeon.Dungeon;

public class RedCrystalDungeon extends Dungeon {

    public static final int ENTER_KEY_ID = 53776;

    public static final int LENGTH_IN_MINUTES = 45;

    public static final String DUNGEON_NAME = "Red Crystal Dungeon";

    public static final Position START_POSITION = new Position(2982, 2762);
    public static final Position EXIT_POSITION = new Position(2982, 2762);

    public RedCrystalDungeon() {
        super(START_POSITION, EXIT_POSITION, LENGTH_IN_MINUTES, DUNGEON_NAME);
    }

}
