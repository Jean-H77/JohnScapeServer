package com.ruse.world.content.trading_post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Coffer {
    private final String owner;
    private int amount;

    public void addAmount(int amount) {
        this.amount = Math.addExact(this.amount, amount);
    }

}
