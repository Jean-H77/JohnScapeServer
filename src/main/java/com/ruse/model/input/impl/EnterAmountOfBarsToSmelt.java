package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.smithing.Smelting;
import com.ruse.world.content.skill.smithing.SmithingData;
import com.ruse.model.entity.character.player.Player;

public class EnterAmountOfBarsToSmelt extends EnterAmount {

	public EnterAmountOfBarsToSmelt(int bar) {
		this.bar = bar;
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		for(int barId : SmithingData.SMELT_BARS) {
			if(barId == bar) {
				Smelting.smeltBar(player, barId, amount);
				break;
			}
		}
	}
	
	private int bar;
	
	public int getBar() {
		return bar;
	}
	
}
