package com.ruse.world.content.gambling;

import com.ruse.model.Item;
import com.ruse.model.entity.character.player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class GamblingGame {

    protected final Player[] gamblers;

    protected final List<Item> pot = new ArrayList<>();

    public GamblingGame(Player[] gamblers) {
        this.gamblers = gamblers;
    }

    public Player[] getGamblers() {
        return gamblers;
    }

    public void payout() {
        Player winner = determineWinner();

        for(Item item : pot) {
            if(winner.getInventory().isFull()) {
                winner.getPacketSender().sendMessage(item.getName() + " " + item.getAmount() + "x has been sent to your bank.");
                winner.getBank(winner.getCurrentBankTab()).add(item);
            } else {
                winner.getInventory().add(item);
            }
        }
    }

    public abstract void start();
    public abstract void end();
    public abstract Player determineWinner();
}
