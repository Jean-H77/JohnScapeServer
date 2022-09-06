package com.ruse.world.content.trading_post.buying_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.Input;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@RequiredArgsConstructor
public class ItemSearchInput extends Input {

    private final BuyingPage buyingPage;

    @Override
    public void handleSyntax(Player player, String text) {
        if(text.length() < 3) {
            player.getPacketSender().sendMessage("@red@Please enter more than 3 letters");
            return;
        }
        text = text.substring(0,1).toUpperCase(Locale.ROOT) + text.substring(1);
        buyingPage.setItemSearch(text);
        buyingPage.getFilteredSearch();
    }
}
