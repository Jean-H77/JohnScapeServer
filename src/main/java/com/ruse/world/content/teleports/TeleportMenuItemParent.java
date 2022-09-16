package com.ruse.world.content.teleports;

import com.ruse.model.Position;
import lombok.Getter;

@Getter
public class TeleportMenuItemParent extends TeleportMenuItem{
    private final TeleportType teleportType;
    private final TeleportMenuItemChild[] children;

    public TeleportMenuItemParent(String teleportName, TeleportType teleportType, TeleportMenuItemChild[] children) {
        super(teleportName);
        this.teleportType = teleportType;
        this.children = children;
    }
}
