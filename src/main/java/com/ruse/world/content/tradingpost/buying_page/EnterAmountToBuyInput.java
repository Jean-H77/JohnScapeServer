package com.ruse.world.content.tradingpost.buying_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.EnterAmount;

public class EnterAmountToBuyInput extends EnterAmount {

    private final BuyingPage buyingPage;

    public EnterAmountToBuyInput(BuyingPage buyingPage) {
        this.buyingPage = buyingPage;
    }

    @Override
    public void handleAmount(Player player, int amount) {

        if(amount == 0) {

            player.getPacketSender().sendInterface(BuyingPage.INTERFACE_ID);
            player.getPacketSender().sendMessage("@red@Please enter an amount greater than 0.");

            return;
        }

        buyingPage.setAmountToBuy(amount);
        buyingPage.sendNpcDialogueChatBox();

    }
}
