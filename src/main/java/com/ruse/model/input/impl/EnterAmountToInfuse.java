package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.summoning.PouchMaking;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountToInfuse extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getInterfaceId() != 63471) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		PouchMaking.infusePouches(player, amount);
	}

}
