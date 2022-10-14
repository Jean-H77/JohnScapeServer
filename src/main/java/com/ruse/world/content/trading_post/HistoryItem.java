package com.ruse.world.content.trading_post;

public class HistoryItem {
    private final int itemId;
    private final int amountSold;
    private final String buyer;
    private final String seller;
    private final int purchasePrice;
    private final long purchaseTime;

    public HistoryItem(int itemId, int amountSold, String buyer, String seller, int purchasePrice, long purchaseTime) {
        this.itemId = itemId;
        this.amountSold = amountSold;
        this.buyer = buyer;
        this.seller = seller;
        this.purchasePrice = purchasePrice;
        this.purchaseTime = purchaseTime;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getAmountSold() {
        return this.amountSold;
    }

    public String getBuyer() {
        return this.buyer;
    }

    public String getSeller() {
        return this.seller;
    }

    public int getPurchasePrice() {
        return this.purchasePrice;
    }

    public long getPurchaseTime() {
        return this.purchaseTime;
    }
}
