package com.ruse.world.content.trading_post;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.util.Misc;
import com.ruse.world.content.trading_post.buying_page.BuyingPage;
import com.ruse.world.content.trading_post.selling_page.SellingPage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
@Setter
public class PlayerShopManager {

    public static final int INTERFACE_ID = 48500;
    private static final int BUY_BUTTON_ID = -16920;
    private static final int HISTORY_BUTTON_ID = -16917;
    private static final int REFRESH_BUTTON_ID = -16912;
    private static final int COFFER_BUTTON_ID = -17027;

    private static final int COFFER_STRING_ID = 48614;

    private static final int LISTING_SIZE = 20;

    private final Player p;

    private long lastRefreshInMilli;

    private SellingPage sellingPage;
    private BuyingPage buyingPage;

    public void showInterface() {
        sellingPage = null;
        buyingPage = null;
        lastRefreshInMilli = 0;
        sendMyListingData();
        Optional<Coffer> coffer = ShopUtils.getCoffer(p.getUsername());
        p.getPacketSender().sendString(COFFER_STRING_ID, "Coffer: " + (coffer.map(value -> Misc.currency(value.getAmount(), false)).orElse("0")));
        p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void clearListing(PacketBuilder packetBuilder, int slot) {
        packetBuilder.putString("Click to list an item.");
        packetBuilder.putInt(slot+48534); // string id for empty text

        packetBuilder.putString("");
        packetBuilder.putInt(slot+48554); // string id for price

        packetBuilder.putString("");
        packetBuilder.putInt(slot+48574); // string id for item name

        packetBuilder.putInt(slot+48594); // send item id and amount
        packetBuilder.putInt(0);
        packetBuilder.putInt(1);

        packetBuilder.putInt(48514+slot); // send whether the rectangle is filled or not
        packetBuilder.put(0);

        packetBuilder.putInt(48648+slot); // send whether the dismiss button is showed or not
        packetBuilder.put(0);

        packetBuilder.putString("");
        packetBuilder.putInt(slot+48668); // string id for dismiss text
    }

    public void sendMyListingData(PacketBuilder packetBuilder, Listing listing, int slot) {
        packetBuilder.putString("");
        packetBuilder.putInt(slot+48534); // string id for empty text

        packetBuilder.putString("Price (ea): " + Misc.currency(listing.getPrice(), false) + " gp");
        packetBuilder.putInt(slot+48554); // string id for price

        packetBuilder.putString(ItemDefinition.forId(listing.getItemId()).getName());
        packetBuilder.putInt(slot+48574); // string id for item name

        packetBuilder.putInt(48594+slot); // send item id and amount
        packetBuilder.putInt(listing.getItemId()+1);
        packetBuilder.putInt(listing.getAmount());

        packetBuilder.putInt(48514+slot); // send whether the rectangle is filled or not
        packetBuilder.put(1);

        packetBuilder.putInt(48648+slot); // send whether the dismiss button is showed or not
        packetBuilder.put(1);

        packetBuilder.putString("Dismiss");
        packetBuilder.putInt(slot+48668); // string id for dismiss text
    }

    public void sendMyListingData() {
        List<Listing> listings = ShopUtils.getListings(p.getUsername());

        PacketBuilder packetBuilder = new PacketBuilder(31, Packet.PacketType.SHORT);

        for(int i = 0; i < LISTING_SIZE; i++) {

            Optional<Listing> listing = Optional.empty();

            if(ShopUtils.isSlotOccupied(listings, i)) {
                listing = ShopUtils.getListing(listings, i);
            }

            if(listing.isEmpty()) {

                clearListing(packetBuilder, i);

            } else {
                int slot = listing.get().getSlot();

                sendMyListingData(packetBuilder, listing.get(), slot);
            }
        }

        p.getSession().queueMessage(packetBuilder);

    }

    public boolean handleButtonClick(int btnId) {
        if(p.getInterfaceId() != INTERFACE_ID && p.getInterfaceId() != SellingPage.INTERFACE_ID && p.getInterfaceId() != BuyingPage.INTERFACE_ID) return false;
        if(sellingPage != null && sellingPage.handleButtonClick(btnId)) return true;
        if(buyingPage != null && buyingPage.handleButtonClick(btnId)) return true;


        if(btnId == COFFER_BUTTON_ID) {

            collectFromCoffer();

            return true;
        }


        List<Listing> listings = ShopUtils.getListings(p.getUsername());

        if(btnId == BUY_BUTTON_ID) {

            buyingPage = new BuyingPage(p);

            buyingPage.showInterface();

        } else if(btnId == HISTORY_BUTTON_ID) {
            int slot = ShopUtils.getNextAvailableSlot(listings);

            if(slot != -1) {

                sellingPage(slot);

            } else {

                p.getPacketSender().sendMessage("@red@You have no more slots left.");
            }

        } else if(btnId == REFRESH_BUTTON_ID) {

            if(System.currentTimeMillis() > lastRefreshInMilli + 10000) {
                lastRefreshInMilli = System.currentTimeMillis();

                sendMyListingData();

            } else {

                p.getPacketSender().sendMessage("@red@You have to wait 10 seconds between refreshes.");
            }

        } else if(btnId >= -17022 && btnId <= -17003) {
            int slotNum = 17022 + btnId;

            if (ShopUtils.isSlotOccupied(listings, slotNum)) {

                p.getPacketSender().sendMessage("@red@This slot already has a listing, click dismiss to cancel your listing.");

            } else {

                sellingPage(slotNum);
            }

            return true;

        } else if(btnId >= -16888 && btnId <= -16869) {
            int slotNum = 16888 + btnId;

            if(ShopUtils.isSlotOccupied(listings, slotNum)) {

                Optional<Listing> listing = ShopUtils.getListing(listings, slotNum);

                if(listing.isPresent()) {

                    Listing _listing = listing.get();

                    ShopUtils.cancelingQueue.add(_listing);

                } else {

                    p.getPacketSender().sendMessage("@red@This listing does not exist, try refreshing");
                }

            }

        }

        return false;
    }

    public void sellingPage(int slot) {
        sellingPage = new SellingPage(p,slot);
        sellingPage.showPage();
    }

    public void collectFromCoffer() {
        Optional<Coffer> coffer = ShopUtils.getCoffer(p.getUsername());

        if(coffer.isPresent()) {
            if(p.getInventory().isFull()) {

                p.getPacketSender().sendMessage("You do not have enough inventory spaces!");

            } else {
                Coffer c = coffer.get();
                int amount = p.getInventory().getAmount(995);

                if((long)(c.getAmount() + amount) >= Integer.MAX_VALUE) {
                    p.getPacketSender().sendMessage("@red@Try banking some coins before collecting.");
                } else {
                    p.getInventory().add(995, coffer.get().getAmount());
                    coffer.get().setAmount(0);
                    p.getPacketSender().sendString(COFFER_STRING_ID, "Coffer: " + (coffer.map(value -> Misc.currency(value.getAmount(), false)).orElse("0")));

                }
            }

        } else {
            p.getPacketSender().sendMessage("@red@Your coffer is currently empty");
        }

    }
}
