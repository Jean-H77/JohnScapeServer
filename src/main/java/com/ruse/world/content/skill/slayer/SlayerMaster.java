package com.ruse.world.content.skill.slayer;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SlayerMaster {
    AKTHANAKOS(33578, SlayerDungeon.ETHEREAL_DUNGEON),
    ACHIETTIES(38172, SlayerDungeon.DRAGONIC_DUNGEON);

    private final transient int npcId;
    private final transient SlayerDungeon dungeon;

    SlayerMaster(int npcId, SlayerDungeon dungeon) {
        this.npcId = npcId;
        this.dungeon = dungeon;
    }

    public static final Map<Integer, SlayerMaster> VALUES = Arrays.stream(values()).collect(Collectors.toMap(e -> e.npcId, Function.identity()));

    public int getNpcId() {
        return npcId;
    }

    public SlayerDungeon getDungeon() {
        return dungeon;
    }
}
