package com.ruse.world.content.attendance;

import com.ruse.model.Item;

import java.time.Month;

public class MonthlyReward {
    private final Month month;
    private final Item[] items;

    public MonthlyReward(Month month, Item... items) {
        this.month = month;
        this.items = items;
    }

    public Month getMonth() {
        return month;
    }

    public Item[] getItems() {
        return items;
    }
}
