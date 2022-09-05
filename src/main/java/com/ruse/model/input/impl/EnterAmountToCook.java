package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.cooking.Cooking;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountToCook extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getSelectedSkillingItem() > 0)
			Cooking.cook(player, player.getSelectedSkillingItem(), amount);
	}

}
