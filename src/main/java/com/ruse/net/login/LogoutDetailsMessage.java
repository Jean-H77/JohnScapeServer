package com.ruse.net.login;

import com.ruse.model.entity.character.player.Player;

public final class LogoutDetailsMessage implements AuthenticationMessage {

    private final Player player;

    public LogoutDetailsMessage(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
