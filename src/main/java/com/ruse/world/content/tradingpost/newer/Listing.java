package com.ruse.world.content.tradingpost.newer;

import java.util.Date;

public class Listing {
    private final String seller;
    private final String itemName;
    private final int itemId;
    private final int initialAmount;
    private final int price;
    private final Date listingTimestamp;
    private int amountSold;
    private ListingState listingState;

    public Listing(String seller, String itemName, int itemId, int initialAmount, int price, Date listingTimestamp) {
        this.seller = seller;
        this.itemName = itemName;
        this.itemId = itemId;
        this.initialAmount = initialAmount;
        this.price = price;
        this.listingTimestamp = listingTimestamp;
        this.amountSold = 0;
        listingState = ListingState.PENDING_ADDITION;
    }

    public String getSeller() {
        return seller;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public int getInitialAmount() {
        return initialAmount;
    }

    public int getPrice() {
        return price;
    }

    public int getAmountSold() {
        return amountSold;
    }

    public void setAmountSold(int amountSold) {
        this.amountSold = amountSold;
    }

    public Date getListingTimestamp() {
        return listingTimestamp;
    }

    public ListingState getListingState() {
        return listingState;
    }

    public void setListingState(ListingState listingState) {
        this.listingState = listingState;
    }

    public int getAmountLeft() {
        return initialAmount - amountSold;
    }
}
