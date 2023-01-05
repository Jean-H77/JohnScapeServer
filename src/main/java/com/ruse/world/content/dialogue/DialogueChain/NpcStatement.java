package com.ruse.world.content.dialogue.DialogueChain;

import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.dialogue.DialogueExpression;

import static com.ruse.world.content.dialogue.DialogueManager.NPC_DIALOGUE_ID;

public class NpcStatement implements DialoguePart {

    private final int npcId;
    private final DialogueExpression dialogueExpression;
    private final String[] chat;
    private final ClickContinueEvent clickContinueEvent;
    private final OpenDialogueEvent openDialogueEvent;

    public NpcStatement(OpenDialogueEvent openDialogueEvent, DialogueExpression dialogueExpression, int npcId, String... chat) {
        this.clickContinueEvent = null;
        this.openDialogueEvent = openDialogueEvent;
        this.npcId = npcId;
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
    }

    public NpcStatement(ClickContinueEvent clickContinueEvent, OpenDialogueEvent openDialogueEvent, DialogueExpression dialogueExpression, int npcId, String... chat) {
        this.clickContinueEvent = clickContinueEvent;
        this.openDialogueEvent = openDialogueEvent;
        this.npcId = npcId;
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
    }

    public NpcStatement(ClickContinueEvent clickContinueEvent, DialogueExpression dialogueExpression, int npcId, String... chat) {
        this.clickContinueEvent = clickContinueEvent;
        this.openDialogueEvent = null;
        this.npcId = npcId;
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
    }

    public NpcStatement(DialogueExpression dialogueExpression, int npcId, String... chat) {
        this.clickContinueEvent = null;
        this.openDialogueEvent = null;
        this.npcId = npcId;
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
    }

    public OpenDialogueEvent getOpenDialogueEvent() {
        return openDialogueEvent;
    }

    @Override
    public void execute(Player player) {
        int startDialogueChildId = NPC_DIALOGUE_ID[chat.length - 1];
        int headChildId = startDialogueChildId - 2;
        player.getPacketSender().sendNpcHeadOnInterface(npcId, headChildId);
        player.getPacketSender().sendInterfaceAnimation(headChildId, dialogueExpression.getAnimation());
        player.getPacketSender().sendString(startDialogueChildId - 1, NpcDefinition.forId(npcId) != null ? NpcDefinition.forId(npcId).getName().replaceAll("_", " ") : "");
        for (int i = 0; i < chat.length; i++) {
            player.getPacketSender().sendString(startDialogueChildId + i, chat[i]);
        }
        player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
    }

    public ClickContinueEvent getClickContinueEvent() {
        return clickContinueEvent;
    }

}
