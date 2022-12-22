package com.ruse.world.content.dialogue;

import com.ruse.model.entity.character.player.Player;

public class PlayerStatement implements DialoguePart{

    private final DialogueExpression dialogueExpression;
    private final String[] chat;

    public PlayerStatement(DialogueExpression dialogueExpression, String... chat) {
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
    }

    @Override
    public void execute(Player player) {
        int startDialogueChildId = PLAYER_DIALOGUE_ID[chat.length - 1];
        int headChildId = startDialogueChildId - 2;
        player.getPacketSender().sendPlayerHeadOnInterface(headChildId);
        player.getPacketSender().sendInterfaceAnimation(headChildId, dialogueExpression.getAnimation());
        player.getPacketSender().sendString(startDialogueChildId - 1, player.getUsername());
        for (int i = 0; i < chat.length; i++) {
            player.getPacketSender().sendString(startDialogueChildId + i, chat[i]);
        }
        player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
    }

    public static final int[] PLAYER_DIALOGUE_ID = {
            971,
            976,
            982,
            989
    };
}

