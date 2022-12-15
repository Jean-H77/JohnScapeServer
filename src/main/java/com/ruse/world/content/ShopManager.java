package com.ruse.world.content;

import com.ruse.model.ShopItem;
import com.ruse.model.container.impl.Shop;
import com.ruse.model.container.impl.shopImpl.TestShop;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.World;

import java.util.*;

public class ShopManager {

    private static final HashMap<Integer, Shop> SHOPS = new HashMap<>();

    static {
        SHOPS.put(0,new TestShop());
    }

    public static void openShop(int shopId, Player player) {
        player.setShopping(true);
        Shop shop = SHOPS.get(shopId);
        player.setShop(shop);
        shop.openShop(player);
    }

    public static int getPrice(Shop shop, int itemId) {
        Optional<ShopItem> shopItemOptional = Arrays.stream(shop.getShopItems())
                .filter(it -> it.getItemId() == itemId)
                .findFirst();

        return shopItemOptional.map(ShopItem::getCost).orElse(-1);
    }

    public static int getCurrentStock(Shop shop, int itemId) {
        Optional<ShopItem> shopItemOptional = Arrays.stream(shop.getShopItems())
                .filter(it -> it.getItemId() == itemId)
                .findFirst();

        return shopItemOptional.map(ShopItem::getAmount).orElse(-1);
    }

    public static ShopItem getShopItem(Shop shop, int itemId) {
        Optional<ShopItem> shopItemOptional = Arrays.stream(shop.getShopItems())
                .filter(it -> it.getItemId() == itemId)
                .findFirst();

        return shopItemOptional.orElse(null);
    }

    public static void addToBuyingQueue(Player player, int itemId, int amount) {
        if(player.isShopping() && player.getShop() != null) {
            selectToBuy(player.getShop(),player, itemId, amount);
        }
    }

    public static void processPurchaseQueues() {
        List<Shop> shops = new ArrayList<>(SHOPS.values());
        for(int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);
            for (int j = 0; j < shop.getBuyingQueue().size(); i++) {
                Shop.ToBuyShopItem toBuyShopItem = shop.getBuyingQueue().poll();
                if (toBuyShopItem != null) {
                    buyItem(toBuyShopItem);
                }
            }
        }
    }

    public static void buyItem(Shop.ToBuyShopItem toBuyShopItem) {
        Player player = World.getPlayerByName(toBuyShopItem.getPlayer());

        if(player == null) {
            return;
        }

        Shop shop = toBuyShopItem.getShop();

        if(checkRequirements(toBuyShopItem)) {
            player.getInventory().delete(shop.getCurrency(),toBuyShopItem.getAmount()*getPrice(shop,toBuyShopItem.getItemId()));
            ShopItem shopItem = getShopItem(shop,toBuyShopItem.getItemId());
            shopItem.setAmount(shopItem.getAmount()-toBuyShopItem.getAmount());
            player.getInventory().add(toBuyShopItem.getItemId(),toBuyShopItem.getAmount());
            shop.refreshItem(shopItem);
            player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
        }
    }

    public static boolean checkRequirements(Shop.ToBuyShopItem toBuyShopItem) {
        int itemId = toBuyShopItem.getItemId();
        Player player = World.getPlayerByName(toBuyShopItem.getPlayer());

        if(player == null) return false;

        if(player.getInventory().isFull()) {
            player.getPacketSender().sendMessage("You don't have enough free inventory spots to buy this amount");
            return false;
        }

        int amount = toBuyShopItem.getAmount();
        Shop shop = toBuyShopItem.getShop();

        if(getShopItem(shop,itemId) == null) {
            return false;
        }

        int currentStock = getCurrentStock(shop, itemId);

        if(currentStock <= 0) {
            player.getPacketSender().sendMessage("This item has no stock left");
            return false;
        }

        if(toBuyShopItem.getAmount() > currentStock) {
            toBuyShopItem.setAmount(currentStock);
        }

        int singlePrice = ShopManager.getPrice(shop,itemId);

        if(singlePrice == -1) {
            return false;
        }

        int totalPrice = singlePrice * amount;

        if(player.getInventory().getFreeSlots() < amount) {
            toBuyShopItem.setAmount(player.getInventory().getFreeSlots());
        }

        if(player.getInventory().getAmount(shop.getCurrency()) < totalPrice) {
            player.getPacketSender().sendMessage("You cannot afford to buy anymore of that item");
            return false;
        }

        return true;
    }

    public static void selectToBuy(Shop shop, Player player, int itemId, int amount) {
        Shop.ToBuyShopItem toBuyShopItem = new Shop.ToBuyShopItem(player.getUsername(), shop, itemId, amount);
        if(ShopManager.checkRequirements(toBuyShopItem)) {
            shop.getBuyingQueue().add(toBuyShopItem);
        }
    }
}
