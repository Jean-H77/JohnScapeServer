package com.ruse.eventbus.impl;

import com.ruse.model.entity.character.player.Player;

public record ButtonClickEvent(
        Player player,
        int btnId
) {

}
