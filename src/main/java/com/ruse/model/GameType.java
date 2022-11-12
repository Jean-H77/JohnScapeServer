package com.ruse.model;

public enum GameType {
    NORMAL(new Item(4151,1)),
    REALIST(new Item(6585,1));

    private final transient Item[] inventoryItems;

    GameType(Item... inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public Item[] getInventoryItems() {
        return inventoryItems;
    }
}
