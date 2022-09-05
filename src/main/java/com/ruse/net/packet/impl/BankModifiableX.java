package com.ruse.net.packet.impl;

import com.ruse.model.Item;
import com.ruse.model.container.impl.Bank;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.model.entity.character.player.Player;

public class BankModifiableX implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int slot = packet.readUnsignedShortA();
		int component = packet.readShort();
		int item = packet.readUnsignedShortA();
		int amount = packet.readInt();
		switch(component) {
			case 5064:
				if (slot >= 0 && slot < player.getInventory().capacity()) {
					Item storing = player.getInventory().forSlot(slot);
					if (!storing.getAttributes().hasAttributes())
						storing = new Item(item, amount);
					if (!player.isBanking() || storing.getId() != item || !player.getInventory().contains(storing.getId()) || player.getInterfaceId() != 5292)
						return;
					player.setCurrentBankTab(Bank.getTabToDepositItem(player, storing));
					player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), storing, slot, false, true);
				}
				break;
			case 5382:
				int tab = Bank.getTabForItem(player, item);
				if (slot >= 0 && slot < player.getBank(tab).capacity()) {
					Item outcome = player.getBankSearchingAttribtues().isSearchingBank()
							&& player.getBankSearchingAttribtues().getSearchedBank() != null
							? player.getBankSearchingAttribtues().getSearchedBank().getItems()[slot]
							: player.getBank(tab).getItems()[slot];
					if (item != outcome.getId())
						return;
					if (!player.getBank(tab).contains(outcome.getId()))
						return;

					if (!outcome.getAttributes().hasAttributes())
						outcome = new Item(item, amount);
					player.getBank(tab).setPlayer(player).switchItem(player.getInventory(),
							outcome, player.getBank(tab).getSlot(outcome.getId()), false, true);
				}
				break;
		}
	}

}
