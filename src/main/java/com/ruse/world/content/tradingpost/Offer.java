package com.ruse.world.content.tradingpost;

public class Offer {
    private final String lister;
    private final int price;
    private final int initialQuantity;
    private int amountSold;

    public Offer(String lister, int price, int initialQuantity) {
        this.lister = lister;
        this.price = price;
        this.initialQuantity = initialQuantity;
    }

    public String getLister() {
        return lister;
    }

    public int getPrice() {
        return price;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public int getAmountSold() {
        return amountSold;
    }

    public void setAmountSold(int amountSold) {
        this.amountSold = amountSold;
    }
}
