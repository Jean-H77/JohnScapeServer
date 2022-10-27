package com.ruse.world.content.teleports;

import com.ruse.model.Item;
import com.ruse.model.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class TeleportMenuItemChild extends TeleportMenuItem{
    private final int npcId;
    private final Position teleportPosition;

    public TeleportMenuItemChild(String teleportName, Position teleportPosition, int npcId) {
        super(teleportName);
        this.npcId = npcId;
        this.teleportPosition = teleportPosition;
    }

    public int getNpcId() {
        return this.npcId;
    }

    public Position getTeleportPosition() {
        return this.teleportPosition;
    }

}
