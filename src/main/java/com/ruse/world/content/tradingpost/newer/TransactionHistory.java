package com.ruse.world.content.tradingpost.newer;

import java.util.Date;

public record TransactionHistory(int itemId, String itemName, String buyer, String seller, int quantity, int price, Date timestamp) {
}
