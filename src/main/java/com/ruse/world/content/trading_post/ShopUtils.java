package com.ruse.world.content.trading_post;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;


public class ShopUtils {

    public static List<Listing> marketListings = new ArrayList<>();
    public static List<HistoryItem> marketHistory = new ArrayList<>();
    public static List<Coffer> marketCoffers = new ArrayList<>();

    public static Queue<Listing> buyingQueue = new ConcurrentLinkedQueue<>();
    public static Queue<Listing> cancelingQueue = new ConcurrentLinkedQueue<>();

    static {
        testData();
    }

    public static Optional<Coffer> getCoffer(String playerName) {
        return marketCoffers
                .stream()
                .filter(coffer -> coffer.getOwner().equals(playerName))
                .findFirst();
    }

    public static List<Listing> getListings(String playerName) {
        return marketListings
                .stream()
                .filter(listing -> listing.getSeller().equals(playerName))
                .collect(Collectors.toList());
    }

    public static boolean isSlotOccupied(List<Listing> listings, int slot) {
        return listings != null && listings
                .parallelStream()
                .anyMatch(listing -> listing.getSlot() == slot && listing.getAmount() > 0);
    }

    public static Optional<Listing> getListing(List<Listing> listings, int slot) {
        return listings
                .parallelStream()
                .filter(listing -> listing.getSlot() == slot)
                .findFirst();
    }

    public static List<HistoryItem> getItemHistory(int itemId) {
        return marketHistory
                .stream()
                .filter(history -> history.getItemId() == itemId)
                .limit(30)
                .collect(Collectors.toList());
    }

    public static int getNextAvailableSlot(List<Listing> listings) {
        int maxSlots = 20;
        for(int i = 0; i < maxSlots; i++) {
            if(!isSlotOccupied(listings, i)) {
                return i;
            }
        }
        return -1;
    }

    public static void processCancels() {
        if(cancelingQueue.isEmpty()) return;
        Listing listing = cancelingQueue.poll();
        if(listing == null) return;
        if(!marketListings.contains(listing)) return;

        Player player = World.getPlayerByName(listing.getSeller());

        if(player != null && player.isRegistered()) {

            if(ItemDefinition.forId(listing.getItemId()).isStackable()) {
                if(player.getInventory().isFull()) {
                    if(player.getInventory().contains(listing.getItemId())) {

                        player.getInventory().add(listing.getItemId(), listing.getAmount());

                        marketListings.remove(listing);

                        player.getPlayerShopManager().showInterface();

                    } else {
                        player.getPacketSender().sendMessage("You do not have enough inventory spaces!");
                    }
                }

            } else {

                if(player.getInventory().getFreeSlots() >= listing.getAmount()) {

                    player.getInventory().add(listing.getItemId(), listing.getAmount());

                    marketListings.remove(listing);

                    player.getPlayerShopManager().showInterface();

                } else {
                    player.getPacketSender().sendMessage("You do not have enough inventory spaces!");
                }
            }
        }
    }

    public static void processBuys() {
        if(buyingQueue.isEmpty()) return;
        Listing listing = cancelingQueue.poll();
        if(!marketListings.contains(listing)) return;

    }

    public static void processQueues() {
        processCancels();
        processBuys();
    }

    public static void testData() {
        marketHistory.add(new HistoryItem(4151, 2, "John", "John1", 100, new Date()));
        marketHistory.add(new HistoryItem(4151, 4, "John", "John1", 200, new Date()));
        marketHistory.add(new HistoryItem(4151, 51, "John", "John1", 3100, new Date()));
        marketHistory.add(new HistoryItem(4151, 53, "John", "John1", 434300, new Date()));
        marketHistory.add(new HistoryItem(4151, 11, "John", "John1", 1043110, new Date()));
        marketCoffers.add(new Coffer("John"));
    }

    /*
     * private constructor to avoid instantiation
     */
    private ShopUtils() {

    }
}
