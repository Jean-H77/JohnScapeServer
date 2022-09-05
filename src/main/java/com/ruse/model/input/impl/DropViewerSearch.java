package com.ruse.model.input.impl;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.Input;

public class DropViewerSearch extends Input {

    @Override
    public void handleSyntax(Player player, String text) {
        player.getDropViewer().displayNewSearch(player.getDropViewer().getFilteredNpcDropList(text.toLowerCase()));
    }
}
