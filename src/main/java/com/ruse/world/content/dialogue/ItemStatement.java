package com.ruse.world.content.dialogue;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;

import static com.ruse.world.content.dialogue.DialogueManager.NPC_DIALOGUE_ID;

public class ItemStatement implements DialoguePart {
    private final int itemId;
    private final int itemAmount;
    private final String[] chat;

    public ItemStatement(int itemId, int itemAmount, String... chat) {
        this.itemId = itemId;
        this.itemAmount = itemAmount;
        this.chat = chat;
    }

    @Override
    public void execute(Player player) {
        int startDialogueChildId = NPC_DIALOGUE_ID[chat.length - 1];
        int headChildId = startDialogueChildId - 2;
        player.getPacketSender().sendInterfaceModel(headChildId, itemId, itemAmount);
        player.getPacketSender().sendString(startDialogueChildId - 1, ItemDefinition.forId(itemId).getName());
        for (int i = 0; i < chat.length; i++) {
            player.getPacketSender().sendString(startDialogueChildId + i, chat[i]);
        }
        player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
    }
}
