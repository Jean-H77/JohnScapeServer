package com.ruse.world.content.trading_post.selling_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.EnterAmount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangePriceInputPrompt extends EnterAmount {

    private final SellingPage sellingPage;

    @Override
    public void handleAmount(Player player, int amount) {
        if(sellingPage.getItem() == null) return;
        sellingPage.setPrice(amount);
        sellingPage.update();
    }
}
