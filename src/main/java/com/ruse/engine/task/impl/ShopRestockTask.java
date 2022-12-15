package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.model.Item;
import com.ruse.model.container.impl.Shop;

public class ShopRestockTask extends Task {

	public ShopRestockTask(Shop shop) {
		super(5);
		this.shop = shop;
	}

	private final Shop shop;

	@Override
	protected void execute() {
			stop();
	}

	@Override
	public void stop() {
		setEventRunning(false);
	}

	public int getRestockAmount(int amountMissing) {
		return (int)(Math.pow(amountMissing, 1.2)/30+1);

		//return (int) 1;
	}

	public static int getDeleteRatio(int x) {
		return (int)(Math.pow(x, 1.05)/50+1);
	}
}
