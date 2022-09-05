package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.fletching.Fletching;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountOfBowsToString extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		Fletching.stringBow(player, amount);
	}

}
