package com.ruse.world.content.tradingpost.selling_page;

import com.ruse.GameSettings;
import com.ruse.model.Item;
import com.ruse.model.entity.character.player.Player;
import com.ruse.util.Misc;
import com.ruse.world.content.tradingpost.HistoryItem;
import com.ruse.world.content.tradingpost.Listing;
import com.ruse.world.content.tradingpost.ShopUtils;
import com.ruse.world.content.tradingpost.buying_page.BuyingPage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class SellingPage {

    public static final int INTERFACE_ID = 48720;
    public static final int CHANGE_PRICE_BUTTON_ID = -16803;
    public static final int CHANGE_QUANTITY_BUTTON_ID = -16800;
    public static final int CONFIRM_LISTING_BUTTON_ID = -16797;
    public static final int GO_BACK_BUTTON_ID = -16788;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final Player p;
    private final int slot;
    private Item item;
    private int quantity;
    private int price;

    public SellingPage(Player p, int slot) {
        this.p = p;
        this.slot = slot;
    }

    public void clearPage() {
        clearRecentSales();
        price = 0;
        quantity = 0;
        p.getPacketSender().sendItemOnInterface(48725, 0, 1);
        p.getPacketSender().sendString(48745, "Price: 0");
        p.getPacketSender().sendString(48746, "Quantity: 0");
        p.getPacketSender().sendString(48747, "Total: 0");
    }

    public void showPage() {
        clearPage();
        p.getPacketSender().sendString(48732, "Item Listing Slot: " + (slot+1));
        p.getPacketSender().sendInterfaceSet(INTERFACE_ID, 3321);
        p.getPacketSender().sendItemContainer(p.getInventory(), 3322);
    }

    public void setItem(Item item) {
        if(this.item != null && this.item.getId() == item.getId()) return;

        if(item.getId() == BuyingPage.CURRENCY_ID || item.getDefinition().isNoted()
                || Arrays.stream(GameSettings.UNTRADEABLE_ITEMS).anyMatch(i -> i == item.getId())
                ||  Arrays.stream(GameSettings.UNSELLABLE_ITEMS).anyMatch(i -> i == item.getId())) {

            p.getPacketSender().sendMessage("@red@Cannot list this item.");
            return;
        }

        if(ShopUtils.getListings(p.getUsername()).stream().noneMatch(listing -> listing.getItemId() == item.getId())) {
            price = 0;
            this.item = item;
            quantity = 1;
            p.getPacketSender().sendItemOnInterface(48725, item.getId(), quantity);
            price = ShopUtils.getPriceAverage(item.getId());
            update();
            showRecentSales();
        } else {
            String itemName = item.getDefinition().getName();
            p.getPacketSender().sendMessage("@red@You already have " + Misc.anOrA(itemName) + " " + itemName + " listing.");
        }
    }

    public void showRecentSales() {
        if(item == null) return;

        List<HistoryItem> historyList = ShopUtils.getItemHistory(item.getId());

        int stringId = 48781;

        p.getPacketSender().sendScrollMax(48749, Math.max(205, historyList.size() * 41));

        for(int i = 0; i < 30; i++) {

            if(historyList.size() > i) {

                HistoryItem historyItem = historyList.get(i);

                p.getPacketSender().sendString(stringId+i, historyItem.getBuyer() +" bought x" + historyItem.getAmountSold()
                + " for @gre@" + Misc.currency(historyItem.getPurchasePrice(), false) + " ea \\n\\n" +
                        "@yel@( " + ShopUtils.calculateAge(historyItem.getPurchaseTime()) + " )");

            } else {

                p.getPacketSender().sendString(stringId+i, "");
            }
        }
    }

    public void clearRecentSales() {
        for(int i = 0; i < 30; i++) {
            p.getPacketSender().sendString(48781+i, "");
        }
    }

    public boolean handleButtonClick(int btnId) {
        if(btnId == GO_BACK_BUTTON_ID) {
            p.getPlayerShopManager().showInterface();
            return true;
        }

        if(btnId == CHANGE_PRICE_BUTTON_ID|| btnId == CHANGE_QUANTITY_BUTTON_ID || btnId == CONFIRM_LISTING_BUTTON_ID) {
            if (this.item == null) {
                p.getPacketSender().sendMessage("@red@Please select an item to list first.");
                return true;
            }

            if(btnId == CHANGE_PRICE_BUTTON_ID) {

                p.setInputHandling(new ChangePriceInput(this));
                p.getPacketSender().sendEnterAmountPrompt("How much would you like to sell " + item.getDefinition().getName() + " for?");

            } else if(btnId == CHANGE_QUANTITY_BUTTON_ID) {

                p.setInputHandling(new ChangeQuantityInput(this));
                p.getPacketSender().sendEnterAmountPrompt("How many of " + item.getDefinition().getName() + " would you like to sell?");

            } else {

                if(price <= 0) {
                    p.getPacketSender().sendMessage("@red@You need to enter a price greater than 0");
                    return true;
                }

                if(p.getInventory().getAmount(item.getId()) >= quantity) {

                    p.getInventory().delete(item.getId(), quantity);

                    ShopUtils.marketListings.add(new Listing(slot, item.getId(), price, quantity, p.getUsername(), System.nanoTime()));

                    p.getPacketSender().sendMessage("@red@You have successfully listed " + item.getDefinition().getName() + " " + quantity + "x for " + Misc.currency(price, true) + " ea.");

                    clearPage();

                    p.getPlayerShopManager().showInterface();
                }

            }

            return true;
        }
        return false;
    }

    public void update() {
        p.getPacketSender().sendItemOnInterface(48725, item.getId(), quantity);
        p.getPacketSender().sendString(48745, "Price: " + Misc.currency(price,false));
        p.getPacketSender().sendString(48746, "Quantity: " + quantity);
        p.getPacketSender().sendString(48747, "Total: " + Misc.currency(((long) quantity *price), false));
    }

    public Player getP() {
        return this.p;
    }

    public int getSlot() {
        return this.slot;
    }

    public Item getItem() {
        return this.item;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getPrice() {
        return this.price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
