package com.ruse.net.packet.impl;

import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;

public class TeleportPacketListener implements PacketListener {
    public static final int PACKET_OPCODE = 242;

    @Override
    public void handleMessage(Player player, Packet packet) {
        int x = packet.readByte();
        int y = packet.readByte();
        if (player.getRights() == PlayerRights.DEVELOPER) {
            Position teleport = new Position(
                    player.getPosition().getX() - (player.getPosition().getLocalX(player.getLastKnownRegion()) - x),
                    player.getPosition().getY() - (player.getPosition().getLocalY(player.getLastKnownRegion()) - y),
                    player.getPosition().getZ()
            );

            player.moveTo(teleport);
            player.getPacketSender().sendMessage("Teleported to: " + teleport.toString());
        }
    }
}
