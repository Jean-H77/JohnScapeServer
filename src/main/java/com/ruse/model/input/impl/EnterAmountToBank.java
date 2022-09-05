package com.ruse.model.input.impl;

import com.ruse.model.Item;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.input.EnterAmount;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountToBank extends EnterAmount {

	public EnterAmountToBank(int item, int slot) {
		super(item, slot);
	}

	@Override
	public void handleAmount(Player player, int amount) {
		if(!player.isBanking())
			return;
		int item = player.getInventory().getItems()[getSlot()].getId();
		if(item != getItem())
			return;
		int invAmount = player.getInventory().getAmount(item);
		if(amount > invAmount) 
			amount = invAmount;
		if(amount <= 0)
			return;
		Item itemObj = player.getInventory().getItems()[getSlot()];
		if (itemObj.getAttributes().hasAttributes()) {
			player.getInventory().switchItem(player.getBank(Bank.getTabToDepositItem(player, itemObj)),
					itemObj, getSlot(), false, true);
		} else {
			player.getInventory().switchItem(player.getBank(Bank.getTabForItem(player, item)),
					new Item(item, amount), getSlot(), false, true);
		}

	}
}
