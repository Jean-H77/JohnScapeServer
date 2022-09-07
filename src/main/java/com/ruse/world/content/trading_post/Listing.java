package com.ruse.world.content.trading_post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Listing {
    private int slot;
    private int itemId;
    private int price;
    private int amount;
    private String seller;
    private long age;
}
