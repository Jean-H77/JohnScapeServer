package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.crafting.LeatherMaking;
import com.ruse.world.content.skill.crafting.leatherData;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountOfLeatherToCraft extends EnterAmount {
	
	@Override
	public void handleAmount(Player player, int amount) {
		for (final leatherData l : leatherData.values()) {
			if (player.getSelectedSkillingItem() == l.getLeather()) {
				LeatherMaking.craftLeather(player, l, amount);
				break;
			}
		}
	}
}
