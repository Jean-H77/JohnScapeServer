package com.ruse.world.content.trading_post;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class HistoryItem {
    private final int itemId;
    private final int amountSold;
    private final String buyer;
    private final String seller;
    private final long purchasePrice;
    private final Date purchaseDate;
}
