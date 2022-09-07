package com.ruse.world.content.trading_post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HistoryItem {
    private final int itemId;
    private final int amountSold;
    private final String buyer;
    private final String seller;
    private final int purchasePrice;
    private final long purchaseTime;
}
