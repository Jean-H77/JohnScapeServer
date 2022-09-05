package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.WellOfGoodwill;
import com.ruse.model.entity.character.player.Player;

public class DonateToWell extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		WellOfGoodwill.donate(player, amount);
	}

}
