package com.ruse.world.content.wogw;

import com.ruse.model.entity.character.player.Player;

public class WellOfGoodwill {

    private static final int INTERFACE_ID = 51065;

    public static void open(Player player) {
        player.getPacketSender().sendInterface(INTERFACE_ID);
    }
}
