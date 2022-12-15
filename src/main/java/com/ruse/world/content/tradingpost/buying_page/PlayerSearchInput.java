package com.ruse.world.content.tradingpost.buying_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.Input;

public class PlayerSearchInput extends Input {

    private final BuyingPage buyingPage;

    public PlayerSearchInput(BuyingPage buyingPage) {
        this.buyingPage = buyingPage;
    }

    @Override
    public void handleSyntax(Player player, String text) {
        buyingPage.setPlayerSearch(text);
        buyingPage.getFilteredSearch();
    }
}
