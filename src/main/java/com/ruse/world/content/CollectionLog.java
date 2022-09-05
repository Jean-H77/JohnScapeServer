package com.ruse.world.content;

import com.ruse.model.entity.character.player.Player;

public class CollectionLog {

    private final Player p;

    public CollectionLog(Player p) {
        this.p = p;
    }

    public void open() {
        p.getPacketSender().sendConfig(388, 0);

    }
}
