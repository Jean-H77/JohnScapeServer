package com.ruse.model;

public class ShopItem {
    private final int itemId;
    private final int cost;
    private final int maxStock;
    private int amount;

    public ShopItem(int itemId, int cost, int amount) {
        this.itemId = itemId;
        this.cost = cost;
        this.amount = amount;
        this.maxStock = amount;
    }

    public int getItemId() {
        return itemId;
    }

    public int getCost() {
        return cost;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMaxStock() {
        return maxStock;
    }
}
