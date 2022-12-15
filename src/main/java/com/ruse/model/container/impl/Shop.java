package com.ruse.model.container.impl;

import com.ruse.model.ShopItem;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.World;
import com.ruse.world.content.ShopManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class Shop extends ItemContainer {
	private static final int SHOP_INTERFACE_ID = 88000;
	private static final int ITEM_CONTAINER_ID = 88011;
	public static final int SHOP_ITEM_CONTAINER_ID = 22475;

	private final List<Player> currentlyViewingShopMap = new ArrayList<>();

	private final Queue<ToBuyShopItem> buyingQueue = new ArrayDeque<>();

	private final String name;
	private int currency;
	private final ShopItem[] shopItems;

	private final boolean restocks;
	private final boolean deletes;
	private final boolean canSell;

	public Shop(String name, int currency, boolean restocks, boolean deletes, boolean canSell, ShopItem... shopItems) {
		super(null);
		this.name = name;
		this.currency = currency;
		this.restocks = restocks;
		this.deletes = deletes;
		this.canSell = canSell;
		this.shopItems = shopItems;
	}

	public void openShop(Player player) {
		if(!currentlyViewingShopMap.contains(player)) {
			currentlyViewingShopMap.add(player);
		}

		player.getPacketSender().sendInterfaceSet(SHOP_INTERFACE_ID,3321)
				.sendItemContainer(player.getInventory(), 3322)
				.sendString(88002,name)
				.sendItemContainer(this.shopItems, name, ITEM_CONTAINER_ID);

		player.getPacketSender().sendString(88002,name);
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
		for(int i = 0; i < currentlyViewingShopMap.size(); i++) {
			for (int j = 0; j < shopItems.length; j++) {

				Player player = currentlyViewingShopMap.get(i);

				if(World.getPlayerByName(player.getUsername()) == null) {
					currentlyViewingShopMap.remove(player);
				}

				if(!(player.isShopping() && player.getShop() == this)) {
					currentlyViewingShopMap.remove(player);
				}

				player.getPacketSender().sendInterfaceSet(SHOP_INTERFACE_ID,3321);
				player.getPacketSender().sendItemContainer(player.getInventory(), 3322)
						.sendItemContainer(this,ITEM_CONTAINER_ID);
			}
		}
		return this;
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

	public void addToBuyQueue(Player player, int itemId, int amount) {
		if(ShopManager.checkRequirements(this,player,itemId,amount)) {
			buyingQueue.add(new ToBuyShopItem(player, this, itemId, amount));
		}
	}

	public void sellItem(int itemId, int amount) {

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

	public boolean isDeletes() {
		return deletes;
	}

	public Queue<ToBuyShopItem> getBuyingQueue() {
		return buyingQueue;
	}

	public static class ToBuyShopItem {
		private final Player player;
		private final Shop shop;
		private final int itemId;
		private int amount;

		public ToBuyShopItem(Player player, Shop shop, int itemId, int amount) {
			this.player = player;
			this.shop = shop;
			this.itemId = itemId;
			this.amount = amount;
		}

		public Player getPlayer() {
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
