package com.ruse.world.content.tradingpost.newer;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.EnterAmount;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.util.Misc;
import com.ruse.world.World;

import java.util.Date;
import java.util.List;

public class TradingPostManager {
    private static final int OVERVIEW_INTERFACE_ID = 48500;
    public static final int SELLING_PAGE_INTERFACE_ID = 48930;
    private static final int MAX_LISTINGS_SIZE = 50;
    private static final int OVERVIEW_DATA_OPCODE = 31;

    private final Player player;
    private List<Listing> myListings;
    private UnlistedItem unlistedItem;

    public TradingPostManager(Player player) {
        this.player = player;
    }

    public void openOverviewInterface() {
        sendOverviewData();
        player.getPacketSender().sendInterface(OVERVIEW_INTERFACE_ID);
    }

    public void sendOverviewData() {
        myListings = TradingPostUtils.getListingsByExactSellerName(player.getUsername());
        PacketBuilder packetBuilder = new PacketBuilder(OVERVIEW_DATA_OPCODE, Packet.PacketType.SHORT);
        packetBuilder.put(myListings.size());
        for(int i = 0; i < MAX_LISTINGS_SIZE; i++) {
            if(myListings.size() > i) {
                Listing listing = myListings.get(i);
                packetBuilder.putString(listing.getItemName());
                packetBuilder.putInt(listing.getItemId() + 1);
                packetBuilder.putString(listing.getInitialAmount() - listing.getAmountLeft() + "/" + listing.getInitialAmount() + " | " + Misc.currency(listing.getPrice(), false) + " (ea)");
                packetBuilder.putInt(listing.getInitialAmount());
                packetBuilder.putInt(listing.getAmountSold());
            }
        }
        player.getSession().queueMessage(packetBuilder);
    }

    public void openSellingPageInterface() {
        unlistedItem = null;
        player.getPacketSender().sendString(48974, "0")
                .sendItemOnInterface(48950,0,0)
                .sendString(48951,"")
                .sendString(48973,"")
                .sendString(48955,"")
                .sendString(48953,"")
                .sendString(48974,"")
                .sendInterfaceSet(SELLING_PAGE_INTERFACE_ID,3321)
                .sendItemContainer(player.getInventory(), 3322);
    }

    public boolean handleButtonClick(int btnId) {
        switch (btnId) {
            case -17029 -> openSellingPageInterface();
            case -16593 -> openOverviewInterface();
            case -16561,-16558,-16555,-16552,-16549,-16546,-16543,-16540,-16537,-16580,-16575,-16572,-16569,-16566 -> handleUnlistedItemButtonInputs(btnId);
            default -> {
                return false;
            }
        }
        return true;
    }

    public void handleUnlistedItemButtonInputs(int btnId) {
        if(unlistedItem==null) return;
        switch (btnId) {
            case -16561,-16572 -> unlistedItem.incrementQuantityByAmountAndUpdateInterface(player, 1);
            case -16558 -> unlistedItem.incrementQuantityByAmountAndUpdateInterface(player, 10);
            case -16555 -> unlistedItem.incrementQuantityByAmountAndUpdateInterface(player, 100);
            case -16552 -> {
                player.getPacketSender().sendEnterAmountPrompt("Enter amount to list:");
                player.setInputHandling(new EnterAmountToList(unlistedItem, unlistedItem.itemId));
            }
            case -16575 -> unlistedItem.incrementQuantityByAmountAndUpdateInterface(player, -1);
            case -16566 -> unlistedItem.incrementPriceByAmountAndUpdateInterface(player,1);
            case -16569 -> unlistedItem.incrementPriceByAmountAndUpdateInterface(player,-1);
            case -16537 -> {
                if(TradingPostUtils.getPlayerListingSize(player.getUsername()) < 50) {
                    TradingPostUtils.submitNewListingRequest(new Listing(player.getUsername(), ItemDefinition.forId(unlistedItem.itemId).getName(), unlistedItem.itemId, unlistedItem.quantity, unlistedItem.price, new Date()));
                } else {
                    player.getPacketSender().sendMessage("@red@You have reached the max listing size of " + MAX_LISTINGS_SIZE + ".");
                }
            }
        }
    }

    public void selectUnlistedItem(int itemId, int quantity) {
        if(quantity > player.getInventory().getAmount(itemId)) {
            quantity = player.getInventory().getAmount(itemId);
        }
        unlistedItem = new UnlistedItem(itemId, quantity);
        unlistedItem.setPrice(calculateAverage(itemId));
        player.getPacketSender().sendItemOnInterface(48950,itemId,1)
                .sendString(48974, String.valueOf(quantity))
                .sendString(48951,ItemDefinition.forId(itemId).getName())
                .sendString(48973, String.valueOf(unlistedItem.price))
                .sendString(48955,String.valueOf(unlistedItem.price))
                .sendString(48953,String.valueOf(TradingPostUtils.getCurrentListingAmount(unlistedItem.itemId)));
    }

    public static boolean listItem(Listing listing) {
        if(listing.getListingState() != ListingState.PENDING_ADDITION) return false;
        Player p;
        if((p=World.getPlayerByName(listing.getSeller())) != null) {
            if(p.getInventory().getAmount(listing.getItemId()) >= listing.getInitialAmount()) {
                p.getInventory().delete(listing.getItemId(), listing.getInitialAmount()).refreshItems();
                p.getTradingPostManager().openSellingPageInterface();
                p.getPacketSender().sendMessage("@red@You have successfully listed " + listing.getItemName() + " " + listing.getInitialAmount() + "x.");
                return true;
            }
        }
        return false;
    }

    public int calculateAverage(int itemId) {
        return 69;
    }

    static class UnlistedItem {
        private final int itemId;
        private int price;
        private int quantity;

        public UnlistedItem(int itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getItemId() {
            return itemId;
        }

        public void incrementQuantityByAmountAndUpdateInterface(Player player, int amount) {
            quantity += amount;
            int inventoryAmount = player.getInventory().getAmount(itemId);

            if(quantity > inventoryAmount) {
                quantity = inventoryAmount;
            } else if(quantity < 1) {
                quantity = 1;
            }

            player.getPacketSender().sendString(48974, String.valueOf(quantity));
        }

        public void incrementPriceByAmountAndUpdateInterface(Player player, int amount) {
            price += amount;
            if(price < 0) {
                price = 0;
            }
            player.getPacketSender().sendString(48973, String.valueOf(price));
        }
    }

    public UnlistedItem getUnlistedItem() {
        return unlistedItem;
    }

    public static class EnterAmountToList extends EnterAmount {
        private UnlistedItem unlistedItem;

        public EnterAmountToList(UnlistedItem unlistedItem, int itemId) {
            super(itemId, 0);
            this.unlistedItem = unlistedItem;
        }

        public EnterAmountToList(int itemId, int slot) {
            super(itemId, slot);
        }

        @Override
        public void handleAmount(Player player, int amount) {
            if(amount < 0) return;
            if(amount > player.getInventory().getAmount(getItem())) {
                amount = player.getInventory().getAmount(getItem());
            }
            if(unlistedItem == null) {
                player.getTradingPostManager().selectUnlistedItem(getItem(), amount);
            } else {
                unlistedItem.setQuantity(amount);
            }
            player.getPacketSender().sendString(48974, String.valueOf(amount));
        }
    }
}
