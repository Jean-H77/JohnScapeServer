package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.crafting.Flax;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountToSpin extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		Flax.spinFlax(player, amount);
	}

}
