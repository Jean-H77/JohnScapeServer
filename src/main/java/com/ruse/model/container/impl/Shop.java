package com.ruse.model.container.impl;

import com.ruse.model.ShopItem;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.World;

import java.util.ArrayList;
import java.util.List;


public class Shop extends ItemContainer {
	private static final int SHOP_INTERFACE_ID = 88000;
	private static final int ITEM_CONTAINER_ID = 88011;
	public static final int SHOP_ITEM_CONTAINER_ID = 22475;

	private final List<String> playersCurrentlyViewing = new ArrayList<>();

	private final String name;
	private Object currency;
	private final ShopItem[] shopItems;

	private final boolean restocks;
	private final boolean canSell;

	public Shop(String name, Object currency, boolean restocks, boolean canSell, ShopItem... shopItems) {
		super(null);
		this.name = name;
		this.currency = currency;
		this.restocks = restocks;
		this.canSell = canSell;
		this.shopItems = shopItems;
	}

	public Shop() {
		super(null);
		this.name = "None";
		this.currency = null;
		this.restocks = false;
		this.canSell = false;
		this.shopItems = null;
	}

	public void openShop(Player player) {
		if(!playersCurrentlyViewing.contains(player.getUsername())) {
			playersCurrentlyViewing.add(player.getUsername());
		}

		player.getPacketSender().sendInterfaceSet(SHOP_INTERFACE_ID,3822)
				.sendItemContainer(player.getInventory(), 3823)
				.sendString(88002,name)
				.sendString(88010,currency instanceof Integer ? "" : "Points: " + player.getPoints().getOrDefault(currency.toString(),0))
				.sendItemContainer(shopItems, name, ITEM_CONTAINER_ID);
	}

	@Override
	public int capacity() {
		return 50;
	}

	@Override
	public StackType stackType() {
		return StackType.STACKS;
	}

	@Override
	public ItemContainer refreshItems() {
		return null;
	}

	public void refreshItem(ShopItem shopItem) {
		for(int i = 0; i < playersCurrentlyViewing.size(); i++) {
			String playerName = playersCurrentlyViewing.get(i);
			Player player;
			if((player = World.getPlayerByName(playerName)) == null) {
				playersCurrentlyViewing.remove(playerName);
				continue;
			}

			if(!(player.isShopping() && player.getShop() == this)) {
				playersCurrentlyViewing.remove(playerName);
				continue;
			}

			player.getPacketSender().sendItemOnInterface(ITEM_CONTAINER_ID,shopItem.getItemId(),0,shopItem.getAmount());
		}
	}

	@Override
	public ItemContainer full() {
		return null;
	}

	public List<String> getPlayersCurrentlyViewing() {
		return playersCurrentlyViewing;
	}

	public ShopItem[] getShopItems() {
		return shopItems;
	}

	public Object getCurrency() {
		return currency;
	}

	public String getName() {
		return name;
	}

	public void setCurrency(Object currency) {
		this.currency = currency;
	}

	public boolean isRestocks() {
		return restocks;
	}

	public boolean isCanSell() {
		return canSell;
	}

}
