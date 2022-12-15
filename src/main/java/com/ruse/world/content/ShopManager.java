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
            player.getShop().addToBuyQueue(player, itemId, amount);
        }
    }

    public static void processPurchaseQueues() {
        Iterator<Map.Entry<Integer, Shop>> it = SHOPS.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, Shop> entry = it.next();

            for(int i = 0; i < entry.getValue().getBuyingQueue().size(); i++) {
                Shop.ToBuyShopItem toBuyShopItem = entry.getValue().getBuyingQueue().poll();
                if(toBuyShopItem != null) {
                    buyItem(toBuyShopItem);
                }
            }

            it.remove();
        }
    }

    public static void buyItem(Shop.ToBuyShopItem toBuyShopItem) {
        Player player = toBuyShopItem.getPlayer();

        if(World.getPlayerByName(player.getUsername()) != null) {
            return;
        }

        int currentStock = getCurrentStock(toBuyShopItem.getShop(),toBuyShopItem.getItemId());

        if(toBuyShopItem.getAmount() > currentStock) {
            toBuyShopItem.setAmount(currentStock);
        }

        Shop shop = toBuyShopItem.getShop();

        if(checkRequirements(toBuyShopItem.getShop(),player,toBuyShopItem.getItemId(),toBuyShopItem.getAmount())) {
            player.getInventory().delete(shop.getCurrency(),toBuyShopItem.getAmount()*getPrice(shop,toBuyShopItem.getItemId()));

            ShopItem shopItem = getShopItem(shop,toBuyShopItem.getItemId());

            shopItem.setAmount(toBuyShopItem.getAmount() - shopItem.getAmount());

            shop.refreshItems();
        }
    }

    public static boolean checkRequirements(Shop shop, Player player, int itemId, int amount) {
        if(getShopItem(shop,itemId) == null) {
            return false;
        }

        if(getCurrentStock(shop, itemId) <= 0) {
            player.getPacketSender().sendMessage("This item has no stock left");
            return false;
        }

        int singlePrice = ShopManager.getPrice(shop,itemId);

        if(singlePrice == -1) {
            return false;
        }

        int totalPrice = singlePrice * amount;

        if(player.getInventory().getFreeSlots() < amount) {
            player.getPacketSender().sendMessage("You don't have enough free inventory spots to buy this amount");
            return false;
        }

        if(player.getInventory().getAmount(shop.getCurrency()) >= totalPrice) {
            player.getPacketSender().sendMessage("You cannot afford to buy anymore of that item");
            return false;
        }

        return true;
    }
}
