package com.ruse.world.content.trading_post.buying_page;

import com.google.common.collect.Lists;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.util.Misc;
import com.ruse.world.content.trading_post.Listing;
import com.ruse.world.content.trading_post.PlayerShopManager;
import com.ruse.world.content.trading_post.ShopUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Setter
public class BuyingPage {

    public static final int INTERFACE_ID = 48811;
    private static final int REFRESH_BUTTON_ID = -16696;
    private static final int BACK_BUTTON_ID = -16710;
    private static final int SEARCH_ITEM_BUTTON_ID = -16716;
    private static final int SEARCH_PLAYER_BUTTON_ID = -16713;
    private static final int RECENT_LISTINGS_BUTTON_ID = -16709;
    private static final int NEXT_PAGE_BUTTON_ID = -16700;
    private static final int PREVIOUS_PAGE_BUTTON_ID = -16703;

    private static final int PAGE_CAPACITY = 50;

    private final Player p;
    private int page = 0;
    private String itemSearch = "Recent";
    private String playerSearch = "";
    private long lastRecentButtonClickedMilli;
    private long lastPageButtonClickedMilli;
    private long lastRefreshButtonClickedMilli;

    private List<Listing> displayedListings = new ArrayList<>();

    public void showInterface() {
        p.getPacketSender().sendString(48839, "Page: " + (page + 1));
        mostRecent();
        p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void mostRecent() {
        itemSearch = "Recent";
        playerSearch = "";
        displayedListings = new ArrayList<>(Lists.reverse(ShopUtils.marketListings));
        displayListings();
    }

    public void getFilteredSearch() {

        if(!itemSearch.equals("Recent")) {

            displayedListings = ShopUtils.marketListings
                    .stream()
                    .filter(listing -> ItemDefinition.forId(listing.getItemId()).getName().startsWith(itemSearch)
                            || ItemDefinition.forId(listing.getItemId()).getName().contains(itemSearch))
                    .collect(Collectors.toList());

        }

        if(!playerSearch.equals("")) {

            displayedListings = displayedListings
                    .stream()
                    .filter(listing -> listing.getSeller().equalsIgnoreCase(playerSearch))
                    .collect(Collectors.toList());

        }

        page = 0;

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

        } else if(btnId == SEARCH_ITEM_BUTTON_ID) {

            p.getPacketSender().sendEnterInputPrompt("Which item would you like to search for?");
            p.setInputHandling(new ItemSearchInput(this));

        } else if(btnId == SEARCH_PLAYER_BUTTON_ID) {

            p.getPacketSender().sendEnterInputPrompt("Which player would you like to search for?");
            p.setInputHandling(new PlayerSearchInput(this));

        } else if(btnId == RECENT_LISTINGS_BUTTON_ID) {

            if(System.currentTimeMillis() < lastRecentButtonClickedMilli + 10000) {

                p.getPacketSender().sendMessage("@red@Please wait 10 seconds between checking most recent listings.");

            } else {

                lastRecentButtonClickedMilli = System.currentTimeMillis();

                mostRecent();
            }
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
        }

        return false;
    }

    public void newPage() {

        displayListings();
        p.getPacketSender().sendString(48839, "Page: " + (page + 1));

    }
}
