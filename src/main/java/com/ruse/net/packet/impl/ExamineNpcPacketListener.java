package com.ruse.net.packet.impl;

import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.entity.character.npc.NpcItemDropping;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.model.entity.character.player.Player;

public class ExamineNpcPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int npc = packet.readUnsignedShort();
		if(npc <= 0) {
			return;
		}

		NpcDefinition npcDef = NpcDefinition.forId(npc);
		if(npcDef != null && npcDef.getExamine() != null) {
			player.getDropViewer().showNpcDropListById(npc);
			player.getPacketSender().sendMessage(npcDef.getExamine());
		}
	}

}
