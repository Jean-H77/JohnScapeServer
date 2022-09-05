package com.ruse.net.packet.impl;

import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.model.entity.character.player.Player;

public class WithdrawMoneyFromPouchPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int amount = packet.readInt();
	//	MoneyPouch.withdrawMoney(player, amount);
	}

}
