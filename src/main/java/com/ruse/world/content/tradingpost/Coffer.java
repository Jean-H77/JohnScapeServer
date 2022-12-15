package com.ruse.world.content.tradingpost;

public class Coffer {
    private final String owner;
    private int amount;

    public Coffer(String owner) {
        this.owner = owner;
    }

    public void addAmount(int amount) {
        this.amount = Math.addExact(this.amount, amount);
    }

    public String getOwner() {
        return this.owner;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
