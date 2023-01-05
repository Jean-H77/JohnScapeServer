package com.ruse.world.content.dialogue.DialogueChain;

import com.ruse.model.entity.character.player.Player;

@FunctionalInterface
public interface DialoguePart {
    void execute(Player player);
}
