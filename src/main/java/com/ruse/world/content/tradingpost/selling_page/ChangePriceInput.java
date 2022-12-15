package com.ruse.world.content.tradingpost.selling_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.EnterAmount;

public class ChangePriceInput extends EnterAmount {

    private final SellingPage sellingPage;

    public ChangePriceInput(SellingPage sellingPage) {
        this.sellingPage = sellingPage;
    }

    @Override
    public void handleAmount(Player player, int amount) {
        if(sellingPage.getItem() == null) return;
        sellingPage.setPrice(amount);
        sellingPage.update();
    }
}
