package com.ruse.world.content.trading_post.buying_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.EnterAmount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnterAmountToBuyInput extends EnterAmount {

    private final BuyingPage buyingPage;

    @Override
    public void handleAmount(Player player, int amount) {

        if(amount == 0) {

            player.getPacketSender().sendInterface(BuyingPage.INTERFACE_ID);
            player.getPacketSender().sendMessage("@red@Please enter an amount greater than 0.");

            return;
        }

        int itemId = buyingPage.getSelectedListing().getItemId();

        if(player.getInventory().getAmount(itemId) + amount >= Integer.MAX_VALUE) {

            player.getPacketSender().sendMessage("You do not have enough inventory spaces!");

        } else {

            buyingPage.setAmountToBuy(amount);
            buyingPage.sendNpcDialogueChatBox();

        }

    }
}
