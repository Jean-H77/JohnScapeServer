package com.ruse.net.packet.impl;

import com.ruse.model.PlayerRelations.PrivateChatStatus;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.model.entity.character.player.Player;

public class ChangeRelationStatusPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int actionId = packet.readInt();
		player.getRelations().setStatus(PrivateChatStatus.forActionId(actionId), true);
	}

}
