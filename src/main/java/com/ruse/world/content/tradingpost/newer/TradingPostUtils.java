package com.ruse.world.content.tradingpost.newer;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.*;

public final class TradingPostUtils {

    private static final List<Listing> ALL_LISTINGS = new ArrayList<>();

    private static final Deque<Listing> newListingQueue = new ArrayDeque<>();
    private static final Deque<Buyer> purchaseQueue = new ArrayDeque<>();
    private static final Deque<Listing> cancellationQueue = new ArrayDeque<>();

    public static List<Listing> getListingsByExactSellerName(String name) {
        List<Listing> listings = new ArrayList<>();
        for(int i = 0; i < ALL_LISTINGS.size(); i++) {
            if(ALL_LISTINGS.get(i).getSeller().equals(name)) {
                listings.add(ALL_LISTINGS.get(i));
            }
        }
        return listings;
    }

    public static List<Listing> getListingsByFilteredSellerName(String name) {
        List<Listing> listings = new ArrayList<>();
        Trie trie = Trie.builder().addKeyword(name.toLowerCase()).build();
        for(int i = 0; i < ALL_LISTINGS.size(); i++) {
            Listing l = ALL_LISTINGS.get(i);
            Collection<Emit> emits = trie.parseText(l.getSeller().toLowerCase());
            if(!emits.isEmpty()) {
                listings.add(l);
            }
        }
        return listings;
    }

    public static List<Listing> getListingsByFilteredItemName(String name) {
        List<Listing> listings = new ArrayList<>();
        Trie trie = Trie.builder().addKeyword(name.toLowerCase()).build();
        for(int i = 0; i < ALL_LISTINGS.size(); i++) {
            Listing l = ALL_LISTINGS.get(i);
            Collection<Emit> emits = trie.parseText(l.getItemName().toLowerCase());
            if(!emits.isEmpty()) {
                listings.add(l);
            }
        }
        return listings;
    }

    public static boolean submitNewListingRequest(Listing listing) {
        listing.setListingState(ListingState.PENDING_ADDITION);
        newListingQueue.add(listing);
        return false;
    }

    public static boolean submitPurchaseRequest(Buyer buyer) {
        buyer.buyingListing().setListingState(ListingState.PURCHASE_REQUESTED);
        purchaseQueue.add(buyer);
        return false;
    }

    public static boolean submitListingCancellationRequest(Listing listing) {
        listing.setListingState(ListingState.CANCELLED_REQUESTED);
        cancellationQueue.add(listing);
        return false;
    }

    public static void processQueues() {
        for(int i = 0; i < newListingQueue.size(); i++) {
            Listing listing = newListingQueue.poll();
            if(TradingPostManager.listItem(listing)) {
                listing.setListingState(ListingState.NEUTRAL);
                ALL_LISTINGS.add(listing);
                /*Thread.startVirtualThread(() -> {
                   try (Connection conn = DataSource.ds.getConnection();
                        PreparedStatement prepStmt = conn.prepareStatement("OK")){

                        prepStmt.executeUpdate();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                });*/
            }
        }

        for(int i = 0; i < purchaseQueue.size(); i++) {
            Buyer listing = purchaseQueue.poll();
        }

        for(int i = 0; i < cancellationQueue.size(); i++) {
            Listing listing = cancellationQueue.poll();
        }
    }

    public static int getPlayerListingSize(String sellerName) {
        int count = 0;
        for(int i = 0; i < ALL_LISTINGS.size(); i++) {
            if(ALL_LISTINGS.get(i).getSeller().equals(sellerName)) {
                count++;
            }
        }
            return count;
    }

    public static int getCurrentListingAmount(int itemId) {
        int count = 0;
        for(int i = 0; i < ALL_LISTINGS.size(); i++) {
            if(ALL_LISTINGS.get(i).getItemId() == itemId) {
                count++;
            }
        }
        return count;
    }

    private TradingPostUtils() {}
}
