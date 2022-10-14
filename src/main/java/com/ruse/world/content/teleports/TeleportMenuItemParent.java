package com.ruse.world.content.teleports;

public class TeleportMenuItemParent extends TeleportMenuItem{
    private final TeleportType teleportType;
    private final TeleportMenuItemChild[] children;

    public TeleportMenuItemParent(String teleportName, TeleportType teleportType, TeleportMenuItemChild[] children) {
        super(teleportName);
        this.teleportType = teleportType;
        this.children = children;
    }

    public TeleportType getTeleportType() {
        return this.teleportType;
    }

    public TeleportMenuItemChild[] getChildren() {
        return this.children;
    }
}
