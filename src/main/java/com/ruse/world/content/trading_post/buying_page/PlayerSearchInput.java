package com.ruse.world.content.trading_post.buying_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.Input;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerSearchInput extends Input {

    private final BuyingPage buyingPage;

    @Override
    public void handleSyntax(Player player, String text) {
        buyingPage.setPlayerSearch(text);
        buyingPage.getFilteredSearch();
    }
}
