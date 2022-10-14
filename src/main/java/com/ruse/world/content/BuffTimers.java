package com.ruse.world.content;


import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;

public class BuffTimers {

    private final Player p;

    public BuffTimers(Player p) {
        this.p = p;
    }

    public void sendOverlay(int spriteId, int seconds) {
        long timer = (System.currentTimeMillis() + (seconds * 1000L));
        PacketBuilder packetBuilder = new PacketBuilder(41, Packet.PacketType.SHORT);
        packetBuilder.putLong(timer);
        packetBuilder.putInt(spriteId);
        p.getSession().queueMessage(packetBuilder);
    }

}
