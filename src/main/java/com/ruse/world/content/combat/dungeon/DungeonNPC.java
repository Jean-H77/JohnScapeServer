package com.ruse.world.content.combat.dungeon;

import com.ruse.model.Position;
import com.ruse.model.entity.character.npc.NPC;

public class DungeonNPC extends NPC {

    public DungeonNPC(int id, Position position) {
        super(id, position);
    }

    @Override
    public void appendDeath() {
        System.out.println("Killed");
        super.appendDeath();
    }
}
