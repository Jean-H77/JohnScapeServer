package com.ruse.world.content.dungeons;

import com.ruse.model.entity.character.npc.NPC;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Dungeon {

    protected final List<NPC> npcList = new ArrayList<>();
    protected final DungeonParty dungeonParty;
    protected LocalDateTime duration;

    protected Dungeon(DungeonParty dungeonParty) {
        this.dungeonParty = dungeonParty;
    }
}
