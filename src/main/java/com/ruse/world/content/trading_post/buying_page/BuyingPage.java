package com.ruse.world.content.trading_post.buying_page;

import com.google.common.collect.Lists;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.trading_post.Listing;
import com.ruse.world.content.trading_post.ShopUtils;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BuyingPage {

    public static final int INTERFACE_ID = 48811;
    private static final int REFRESH_BUTTON_ID = -16696;
    private static final int BACK_BUTTON_ID = -16710;
    private static final int SEARCH_ITEM_BUTTON_ID = -16716;
    private static final int SEARCH_PLAYER_BUTTON_ID = -16713;
    private static final int RECENT_LISTINGS_BUTTON_ID = -16709;
    private static final int NEXT_PAGE_BUTTON_ID = -16700;
    private static final int PREVIOUS_PAGE_BUTTON_ID = -16703;
    private static final int CONFIRM_BUY_BUTTON_ID = -16119;

    private static final int PAGE_CAPACITY = 50;
    public static final int CURRENCY_ID = 995;

    private final Player p;
    private int page = 0;
    private String itemSearch = "Recent";
    private String playerSearch = "";
    private long lastRecentButtonClickedMilli;
    private long lastPageButtonClickedMilli;
    private long lastRefreshButtonClickedMilli;
    private Listing selectedListing;
    private int amountToBuy;
    private List<Listing> displayedListings = new ArrayList<>();

    public BuyingPage(Player p) {
        this.p = p;
    }

    public void showInterface() {
        p.getPacketSender().sendString(48839, "Page: " + (page + 1));
        mostRecent();
        p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void mostRecent() {
         getNewMarketListings();
         displayListings();
    }

    public void getNewMarketListings() {
        displayedListings = new ArrayList<>(Lists.reverse(ShopUtils.marketListings));
    }

    public void getFilteredSearch() {
        displayedListings.clear();
        page = 0;
        if(!itemSearch.equals("Recent") && playerSearch.equals("")) {
                Trie trie = Trie.builder().addKeyword(itemSearch.toLowerCase()).build();
                for(Listing l : ShopUtils.marketListings) {
                    Collection<Emit> emits = trie.parseText(ItemDefinition.forId(l.getItemId()).getName().toLowerCase());
                    if(!emits.isEmpty()) {
                        displayedListings.add(l);
                    }
                }
        } else if(itemSearch.equals("Recent") && !playerSearch.equals("")) {
            Trie trie = Trie.builder().addKeyword(playerSearch.toLowerCase()).build();
            for(Listing l : ShopUtils.marketListings) {
                Collection<Emit> emits = trie.parseText(l.getSeller().toLowerCase());
                if(!emits.isEmpty()) {
                    if(!displayedListings.contains(l)) {
                        displayedListings.add(l);
                    }
                }
            }
        } else if(!itemSearch.equals("Recent")) {
            Trie trie = Trie.builder().addKeyword(playerSearch.toLowerCase()).build();
            Trie trie1 = Trie.builder().addKeyword(itemSearch.toLowerCase()).build();
            for(Listing l : ShopUtils.marketListings) {
                Collection<Emit> emits = trie.parseText(l.getSeller().toLowerCase());
                Collection<Emit> emits1 = trie1.parseText(ItemDefinition.forId(l.getItemId()).getName().toLowerCase());
                if(!emits.isEmpty() && !emits1.isEmpty()) {
                    displayedListings.add(l);
                }
            }
        } else {
            getNewMarketListings();
        }
        displayListings();
    }

    public void displayListings() {
        sendListingData(displayedListings
                .stream()
                .skip(page* 50L)
                .limit(50)
                .collect(Collectors.toList()));
    }


    public void sendListingData(List<Listing> displayedListings) {

        p.getPacketSender().sendScrollMax(48848, Math.max(displayedListings.size()*41, 248));

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Trading Post: ")
                .append(itemSearch);

        if(!playerSearch.equals("")) {

            stringBuilder.append(" ( ").append(playerSearch).append(" )");
        }

        p.getPacketSender().sendString(48813, stringBuilder.toString());

        PacketBuilder packetBuilder = new PacketBuilder(33, Packet.PacketType.SHORT);

        for(int i = 0; i < PAGE_CAPACITY; i++) {

            if(displayedListings.size() > i) {

                Listing listing = displayedListings.get(i);

                packetBuilder.putInt(48900+i);
                packetBuilder.putInt(listing.getItemId()+1);
                packetBuilder.putInt(listing.getAmount());

                packetBuilder.putString(Misc.currency(listing.getPrice(), false));
                packetBuilder.putInt(48950+i);

                packetBuilder.putString("="+ Misc.currency((long) listing.getPrice() * listing.getAmount(), false) + " total");
                packetBuilder.putInt(49000+i);

                packetBuilder.putString(listing.getSeller());
                packetBuilder.putInt(49050+i);

                packetBuilder.putString(ShopUtils.calculateAge(listing.getAge()));
                packetBuilder.putInt(49100+i);


            } else {

                packetBuilder.putInt(48900+i);
                packetBuilder.putInt(0);
                packetBuilder.putInt(1);

                packetBuilder.putString("");
                packetBuilder.putInt(48950+i);

                packetBuilder.putString("");
                packetBuilder.putInt(49000+i);

                packetBuilder.putString("");
                packetBuilder.putInt(49050+i);

                packetBuilder.putString("");
                packetBuilder.putInt(49100+i);

            }
        }

        p.getSession().queueMessage(packetBuilder);
    }

    public boolean handleButtonClick(int btnId) {

        if(handleBuyingOptionsButton(btnId)) return true;

        if(btnId == REFRESH_BUTTON_ID) {

            if(System.currentTimeMillis() < lastRefreshButtonClickedMilli + 10000) {

                p.getPacketSender().sendMessage("@red@Please wait 10 seconds between refreshes.");

            } else {

                lastRefreshButtonClickedMilli = System.currentTimeMillis();

                displayListings();
            }

            return true;

        } else if(btnId == BACK_BUTTON_ID) {

            p.getPlayerShopManager().showInterface();

            return true;

        } else if(btnId == SEARCH_ITEM_BUTTON_ID) {

            p.getPacketSender().sendEnterInputPrompt("Which item would you like to search for?");
            p.setInputHandling(new ItemSearchInput(this));

            return true;

        } else if(btnId == SEARCH_PLAYER_BUTTON_ID) {

            p.getPacketSender().sendEnterInputPrompt("Which player would you like to search for?");
            p.setInputHandling(new PlayerSearchInput(this));

            return true;

        } else if(btnId == RECENT_LISTINGS_BUTTON_ID) {

            if(System.currentTimeMillis() < lastRecentButtonClickedMilli + 10000) {

                p.getPacketSender().sendMessage("@red@Please wait 10 seconds between checking most recent listings.");

            } else {

                lastRecentButtonClickedMilli = System.currentTimeMillis();
                itemSearch = "Recent";
                playerSearch = "";
                mostRecent();
            }

            return true;

        } else if(btnId == CONFIRM_BUY_BUTTON_ID) {

            if(selectedListing != null && selectedListing.getAmount() != 0) {

                if(p.getInventory().getAmount(CURRENCY_ID) >= (selectedListing.getAmount() * amountToBuy)) {

                    ShopUtils.buyingQueue.add(new Buyer(selectedListing, p, amountToBuy));

                } else {

                    p.getPacketSender().sendMessage("You need " + Misc.currency(((long) amountToBuy * selectedListing.getPrice()), true) + " to buy this item.");
                }

                p.getPacketSender().sendInterface(INTERFACE_ID);

            } else {

                p.getPacketSender().sendMessage("@red@This item does not exist anymore. try refreshing your page");
                selectedListing = null;
                p.getPacketSender().sendInterface(INTERFACE_ID);
                showInterface();
            }

            return true;

        }

        if((btnId == NEXT_PAGE_BUTTON_ID || btnId == PREVIOUS_PAGE_BUTTON_ID)) {

            if(System.currentTimeMillis() < lastPageButtonClickedMilli + 500) {

                p.getPacketSender().sendMessage("@red@Please wait 500 ms between page switching");

            } else {

                lastPageButtonClickedMilli = System.currentTimeMillis();

                if(btnId == NEXT_PAGE_BUTTON_ID) {

                        page++;
                        newPage();

                } else {

                    if(page != 0) {

                        page--;
                        newPage();

                    }
                }
            }

            return true;
        }

        if(btnId >= -16687 && btnId <= -16638) {

            if(ShopUtils.buyingQueue.stream().anyMatch(i -> i.getListing() == selectedListing)) {
                p.getPacketSender().sendMessage("@red@This item currently has a buy offer.");
                return true;
            }

            int listingPosition = 16687 + btnId;

            if(displayedListings.size() >= listingPosition) {

                Listing listing = displayedListings.get(listingPosition);

                if(listing != null) {

                    selectedListing = listing;

                    p.getPacketSender().sendChatboxInterface(49400);

                } else {

                    System.out.println("Error setting dialogue: Listing is NULL");
                }
            }
                return true;
        }

        return false;
    }

    public void newPage() {

        displayListings();
        p.getPacketSender().sendString(48839, "Page: " + (page + 1));

    }


    public boolean handleBuyingOptionsButton(int btnId) {
        if(selectedListing == null) return false;

        boolean result = false;

        if(btnId == -16135) {

            amountToBuy = 1;
            result = true;

        } else if(btnId == -16134) {

            p.getPacketSender().sendEnterAmountPrompt("How many of " + ItemDefinition.forId(selectedListing.getItemId()).getName() + " would you like to buy?");
            p.setInputHandling(new EnterAmountToBuyInput(this));

            return true;

        } else if(btnId == -16133) {

            amountToBuy = selectedListing.getAmount();
            result = true;

        } else if(btnId == -16128 || btnId == -16132 || btnId == -16124) {

            selectedListing = null;
            p.getPacketSender().sendInterface(INTERFACE_ID);

            return true;
        }

        if(result) {

            sendNpcDialogueChatBox();
        }

        return result;
    }


    public void sendNpcDialogueChatBox() {
        p.getPacketSender().sendNpcHeadOnInterface(947, 49411)
                .sendString(49415, "@blu@"+Misc.currency((long) amountToBuy *selectedListing.getPrice(), true))
                .sendString(49416, "for @red@x"+ amountToBuy + " " + ItemDefinition.forId(selectedListing.getItemId()).getName() + "?")
                .sendInterfaceAnimation(49411, DialogueExpression.NORMAL.getAnimation())
                .sendChatboxInterface(49409);
    }

    public Player getP() {
        return this.p;
    }

    public int getPage() {
        return this.page;
    }

    public String getItemSearch() {
        return this.itemSearch;
    }

    public String getPlayerSearch() {
        return this.playerSearch;
    }

    public long getLastRecentButtonClickedMilli() {
        return this.lastRecentButtonClickedMilli;
    }

    public long getLastPageButtonClickedMilli() {
        return this.lastPageButtonClickedMilli;
    }

    public long getLastRefreshButtonClickedMilli() {
        return this.lastRefreshButtonClickedMilli;
    }

    public Listing getSelectedListing() {
        return this.selectedListing;
    }

    public int getAmountToBuy() {
        return this.amountToBuy;
    }

    public List<Listing> getDisplayedListings() {
        return this.displayedListings;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setItemSearch(String itemSearch) {
        this.itemSearch = itemSearch;
    }

    public void setPlayerSearch(String playerSearch) {
        this.playerSearch = playerSearch;
    }

    public void setLastRecentButtonClickedMilli(long lastRecentButtonClickedMilli) {
        this.lastRecentButtonClickedMilli = lastRecentButtonClickedMilli;
    }

    public void setLastPageButtonClickedMilli(long lastPageButtonClickedMilli) {
        this.lastPageButtonClickedMilli = lastPageButtonClickedMilli;
    }

    public void setLastRefreshButtonClickedMilli(long lastRefreshButtonClickedMilli) {
        this.lastRefreshButtonClickedMilli = lastRefreshButtonClickedMilli;
    }

    public void setSelectedListing(Listing selectedListing) {
        this.selectedListing = selectedListing;
    }

    public void setAmountToBuy(int amountToBuy) {
        this.amountToBuy = amountToBuy;
    }

    public void setDisplayedListings(List<Listing> displayedListings) {
        this.displayedListings = displayedListings;
    }

    public String toString() {
        return "BuyingPage(p=" + this.getP() + ", page=" + this.getPage() + ", itemSearch=" + this.getItemSearch() + ", playerSearch=" + this.getPlayerSearch() + ", lastRecentButtonClickedMilli=" + this.getLastRecentButtonClickedMilli() + ", lastPageButtonClickedMilli=" + this.getLastPageButtonClickedMilli() + ", lastRefreshButtonClickedMilli=" + this.getLastRefreshButtonClickedMilli() + ", selectedListing=" + this.getSelectedListing() + ", amountToBuy=" + this.getAmountToBuy() + ", displayedListings=" + this.getDisplayedListings() + ")";
    }
}
