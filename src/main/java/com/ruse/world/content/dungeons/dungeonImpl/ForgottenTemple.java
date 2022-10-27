package com.ruse.world.content.dungeons.dungeonImpl;

import com.ruse.model.Position;
import com.ruse.world.content.dungeons.Dungeon;
import com.ruse.world.content.dungeons.DungeonParty;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ForgottenTemple extends Dungeon {
    public static final Position START_POSITION = new Position(3333,3333,0);

    public ForgottenTemple(DungeonParty dungeonParty) {
        super(dungeonParty, START_POSITION);
        duration = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(60);
    }

}
