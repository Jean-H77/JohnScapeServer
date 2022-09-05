package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.crafting.Gems;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountOfGemsToCut extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getSelectedSkillingItem() > 0)
			Gems.cutGem(player, amount, player.getSelectedSkillingItem());
	}

}
