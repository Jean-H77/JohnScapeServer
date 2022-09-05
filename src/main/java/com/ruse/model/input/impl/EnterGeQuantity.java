package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.grandexchange.GrandExchange;
import com.ruse.model.entity.character.player.Player;

public class EnterGeQuantity extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		GrandExchange.setQuantity(player, amount);
	}

}
