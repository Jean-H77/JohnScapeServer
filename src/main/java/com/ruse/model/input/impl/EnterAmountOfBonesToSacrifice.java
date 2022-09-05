package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.prayer.BonesOnAltar;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountOfBonesToSacrifice extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		BonesOnAltar.offerBones(player, amount);
	}

}
