package com.ruse.world.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ruse.GameSettings;
import com.ruse.model.ShopItem;
import com.ruse.model.container.impl.Shop;
import com.ruse.model.entity.character.player.Player;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class ShopManager {
    private static final String FOLDER_LOCATION = GameSettings.DEFINITION_DIRECTORY + "shops/";
    private static final HashMap<String, Shop> SHOPS = new HashMap<>();

    public static void loadShops() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            File folder = new File(FOLDER_LOCATION);
            File[] files = folder.listFiles();
            assert files != null;
            for(File f : files) {
                Shop result = mapper.readValue(new File(FOLDER_LOCATION + f.getName()), Shop.class);
                SHOPS.put(result.getName(), result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openShop(String name, Player player) {
        player.setShopping(true);
        Shop shop = SHOPS.get(name);
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

    public static void buyItem(Player player, int itemId, int amount) {
        if(player == null) {
            return;
        }

        if(player.getShop() == null) {
            return;
        }

        Shop shop = player.getShop();

        int currentStock = getCurrentStock(shop, itemId);

        if(currentStock <= 0) {
            player.getPacketSender().sendMessage("This item has no stock left");
            return;
        }

        if(amount > currentStock) {
            amount = currentStock;
        }

        if(player.getInventory().getFreeSlots() < amount) {
            amount = player.getInventory().getFreeSlots();
        }

        if(checkRequirements(player, itemId, amount)) {
            player.getInventory().delete(shop.getCurrency(),amount*getPrice(shop,itemId));
            ShopItem shopItem = getShopItem(shop,itemId);
            shopItem.setAmount(shopItem.getAmount()-amount);
            player.getInventory().add(itemId,amount);
            shop.refreshItem(shopItem);
            player.getPacketSender().sendItemContainer(player.getInventory(), 3823);
        }
    }

    public static boolean checkRequirements(Player player, int itemId, int amount) {
        if(player == null) {
            return false;
        }

        if(player.getInventory().isFull()) {
            player.getPacketSender().sendMessage("You don't have enough free inventory spots to buy this amount");
            return false;
        }

        if(player.getShop() == null) {
            return false;
        }

        Shop shop = player.getShop();

        if(getShopItem(shop,itemId) == null) {
            return false;
        }

        int singlePrice = ShopManager.getPrice(shop,itemId);

        if(singlePrice == -1) {
            return false;
        }

        int totalPrice = singlePrice * amount;

        if(player.getInventory().getAmount(shop.getCurrency()) < totalPrice) {
            player.getPacketSender().sendMessage("You cannot afford to buy anymore of that item");
            return false;
        }

        return true;
    }
}
