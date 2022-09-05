package com.ruse.world.content.trading_post.selling_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.EnterAmount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangeQuantityInputPrompt extends EnterAmount {

    private final SellingPage sellingPage;

    @Override
    public void handleAmount(Player player, int amount) {
        if(sellingPage.getItem() == null) return;
        if(player.getInventory().getAmount(sellingPage.getItem().getId()) < amount) {
            amount = player.getInventory().getAmount(sellingPage.getItem().getId());
        }
        sellingPage.setQuantity(amount);
        sellingPage.update();
    }
}
