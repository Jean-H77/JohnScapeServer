package com.ruse.world.content.dialogue.DialogueChain;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;

public class ItemStatement implements DialoguePart {
    private final int itemId;
    private final int itemAmount;
    private final String[] chat;
    private final ClickContinueEvent clickContinueEvent;
    private final OpenDialogueEvent openDialogueEvent;

    public ItemStatement(OpenDialogueEvent openDialogueEvent, int itemId, int itemAmount, String... chat) {
        this.clickContinueEvent = null;
        this.openDialogueEvent = openDialogueEvent;
        this.itemId = itemId;
        this.itemAmount = itemAmount;
        this.chat = chat;
    }

    public ItemStatement(ClickContinueEvent clickContinueEvent, OpenDialogueEvent openDialogueEvent, int itemId, int itemAmount, String... chat) {
        this.clickContinueEvent = clickContinueEvent;
        this.openDialogueEvent = openDialogueEvent;
        this.itemId = itemId;
        this.itemAmount = itemAmount;
        this.chat = chat;
    }

    public ItemStatement(ClickContinueEvent clickContinueEvent, int itemId, int itemAmount, String... chat) {
        this.clickContinueEvent = clickContinueEvent;
        this.openDialogueEvent = null;
        this.itemId = itemId;
        this.itemAmount = itemAmount;
        this.chat = chat;
    }

    public ItemStatement(int itemId, int itemAmount, String... chat) {
        this.clickContinueEvent = null;
        this.openDialogueEvent = null;
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

    public ClickContinueEvent getClickContinueEvent() {
        return clickContinueEvent;
    }

    public OpenDialogueEvent getOpenDialogueEvent() {
        return openDialogueEvent;
    }

    private static final int[] NPC_DIALOGUE_ID = {
            4885,
            4890,
            4896,
            4903
    };

}
