package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.crafting.Tanning;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountOfHidesToTan extends EnterAmount {

	private int button;
	public EnterAmountOfHidesToTan(int button) {
		this.button = button;
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		Tanning.tanHide(player, button, amount);
	}

}
