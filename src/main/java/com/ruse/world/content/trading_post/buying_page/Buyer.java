package com.ruse.world.content.trading_post.buying_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.trading_post.Listing;

public class Buyer {

    private final Listing listing;
    private final Player buyer;
    private final int amountToBuy;

    public Buyer(Listing listing, Player buyer, int amountToBuy) {
        this.listing = listing;
        this.buyer = buyer;
        this.amountToBuy = amountToBuy;
    }

    public Listing getListing() {
        return this.listing;
    }

    public Player getBuyer() {
        return this.buyer;
    }

    public int getAmountToBuy() {
        return this.amountToBuy;
    }
}
