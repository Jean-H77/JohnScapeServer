package com.ruse.model.container.impl;

import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.model.entity.character.player.Player;


public class Shop extends ItemContainer {


	public Shop(Player player) {
		super(player);
	}

	@Override
	public int capacity() {
		return 0;
	}

	@Override
	public StackType stackType() {
		return null;
	}

	@Override
	public ItemContainer refreshItems() {
		return null;
	}

	@Override
	public ItemContainer full() {
		return null;
	}
}
