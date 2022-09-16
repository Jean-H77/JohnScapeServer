package com.ruse.world.content.teleports;

import com.ruse.model.Item;
import com.ruse.model.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class TeleportMenuItemChild extends TeleportMenuItem{
    private final int npcId;
    private final CombatStyleType combatStyleType;
    private final Position teleportPosition;

    public TeleportMenuItemChild(String teleportName, Position teleportPosition, int npcId, CombatStyleType combatStyleType) {
        super(teleportName);
        this.npcId = npcId;
        this.combatStyleType = combatStyleType;
        this.teleportPosition = teleportPosition;
    }


    @RequiredArgsConstructor
    @Getter
    enum CombatStyleType {
        MELEE(1415),
        MAGIC(1417),
        RANGE(1416),
        MIXED(1404)
        ;

        private final int spriteId;
    }
}
