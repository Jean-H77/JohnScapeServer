package com.ruse.model.container.impl;

import com.ruse.model.ShopItem;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class Shop extends ItemContainer {
	private static final int SHOP_INTERFACE_ID = 88000;
	private static final int ITEM_CONTAINER_ID = 88011;
	public static final int SHOP_ITEM_CONTAINER_ID = 22475;

	private final List<Player> currentlyViewingShopMap = new ArrayList<>();

	private final String name;
	private int currency;
	private final ShopItem[] shopItems;

	private final boolean restocks;
	private final boolean canSell;

	public Shop(String name, int currency, boolean restocks, boolean canSell, ShopItem... shopItems) {
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
		this.currency = -1;
		this.restocks = false;
		this.canSell = false;
		this.shopItems = null;
	}

	public void openShop(Player player) {
		if(!currentlyViewingShopMap.contains(player)) {
			currentlyViewingShopMap.add(player);
		}

		player.getPacketSender().sendInterfaceSet(SHOP_INTERFACE_ID,3822)
				.sendItemContainer(player.getInventory(), 3823)
				.sendString(88002,name)
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
		for(int i = 0; i < currentlyViewingShopMap.size(); i++) {
			Player player = currentlyViewingShopMap.get(i);
			if(World.getPlayerByName(player.getUsername()) == null) {
				currentlyViewingShopMap.remove(player);
				continue;
			}

			if(!(player.isShopping() && player.getShop() == this)) {
				currentlyViewingShopMap.remove(player);
				continue;
			}

			player.getPacketSender().sendItemOnInterface(ITEM_CONTAINER_ID,shopItem.getItemId(),0,shopItem.getAmount());
		}
	}

	@Override
	public ItemContainer full() {
		return null;
	}

	public List<Player> getCurrentlyViewingShopMap() {
		return currentlyViewingShopMap;
	}

	public ShopItem[] getShopItems() {
		return shopItems;
	}

	public int getCurrency() {
		return currency;
	}

	public String getName() {
		return name;
	}

	public void setCurrency(int currency) {
		this.currency = currency;
	}

	public boolean isRestocks() {
		return restocks;
	}

	public boolean isCanSell() {
		return canSell;
	}

	public static class ToBuyShopItem {
		private final String player;
		private final Shop shop;
		private final int itemId;
		private int amount;

		public ToBuyShopItem(String player, Shop shop, int itemId, int amount) {
			this.player = player;
			this.shop = shop;
			this.itemId = itemId;
			this.amount = amount;
		}

		public String getPlayer() {
			return player;
		}

		public Shop getShop() {
			return shop;
		}

		public int getItemId() {
			return itemId;
		}

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}
	}
}
