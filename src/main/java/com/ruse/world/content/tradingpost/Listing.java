package com.ruse.world.content.tradingpost;

public class Listing {
    private int slot;
    private int itemId;
    private int price;
    private int amount;
    private String seller;
    private long age;

    public Listing(int slot, int itemId, int price, int amount, String seller, long age) {
        this.slot = slot;
        this.itemId = itemId;
        this.price = price;
        this.amount = amount;
        this.seller = seller;
        this.age = age;
    }

    public Listing() {
    }

    public int getSlot() {
        return this.slot;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getPrice() {
        return this.price;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getSeller() {
        return this.seller;
    }

    public long getAge() {
        return this.age;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setAge(long age) {
        this.age = age;
    }
}
