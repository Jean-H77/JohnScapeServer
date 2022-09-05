package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.skill.fletching.Fletching;
import com.ruse.model.entity.character.player.Player;

/**
 * Created by brandon on 4/19/2017.
 */
public class EnterGemAmount extends EnterAmount {
    public void handleAmount(Player player, int amount) {
        if(player.getSelectedSkillingItem() > 0) {
            Fletching.crushGems(player, amount, player.getSelectedSkillingItem());
        }
    }
}