package com.ruse.world.content.dialogue;

import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.entity.character.player.Player;

public class NpcStatement implements DialoguePart {

    private final int npcId;
    private final DialogueExpression dialogueExpression;
    private final String[] chat;

    public NpcStatement(DialogueExpression dialogueExpression, int npcId, String... chat) {
        this.npcId = npcId;
        this.dialogueExpression = dialogueExpression;
        this.chat = chat;
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

    public static final int[] NPC_DIALOGUE_ID = {
            4885,
            4890,
            4896,
            4903
    };
}
