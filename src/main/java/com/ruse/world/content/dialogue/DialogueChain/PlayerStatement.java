package com.ruse.world.content.dialogue.DialogueChain;

import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.dialogue.DialogueExpression;

public class PlayerStatement implements DialoguePart {

    private final DialogueExpression dialogueExpression;
    private final String[] chat;
    private final ClickContinueEvent clickContinueEvent;
    private final OpenDialogueEvent openDialogueEvent;

    public PlayerStatement(OpenDialogueEvent openDialogueEvent, DialogueExpression dialogueExpression, String... chat) {
        this.clickContinueEvent = null;
        this.openDialogueEvent = openDialogueEvent;
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
    }

    public PlayerStatement(ClickContinueEvent clickContinueEvent, OpenDialogueEvent openDialogueEvent, DialogueExpression dialogueExpression, String... chat) {
        this.clickContinueEvent = clickContinueEvent;
        this.openDialogueEvent = openDialogueEvent;
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
    }

    public PlayerStatement(ClickContinueEvent clickContinueEvent, DialogueExpression dialogueExpression, String... chat) {
        this.clickContinueEvent = clickContinueEvent;
        this.openDialogueEvent = null;
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
    }

    public PlayerStatement(DialogueExpression dialogueExpression, String... chat) {
        this.clickContinueEvent = null;
        this.openDialogueEvent = null;
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
    }

    public OpenDialogueEvent getOpenDialogueEvent() {
        return openDialogueEvent;
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

    public ClickContinueEvent getClickContinueEvent() {
        return clickContinueEvent;
    }

    public static final int[] PLAYER_DIALOGUE_ID = {
            971,
            976,
            982,
            989
    };
}

