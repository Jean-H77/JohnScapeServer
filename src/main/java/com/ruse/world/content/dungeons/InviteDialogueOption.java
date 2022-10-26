package com.ruse.world.content.dungeons;

import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueType;

public class InviteDialogueOption extends Dialogue {
    private final DungeonParty dungeonParty;
    private final int slot;
    private final Player p;
    public InviteDialogueOption(DungeonParty dungeonParty, int slot, Player p) {
        this.dungeonParty = dungeonParty;
        this.slot = slot;
        this.p = p;
    }

    @Override
    public DialogueType type() {
        return DialogueType.OPTION;
    }

    @Override
    public DialogueExpression animation() {
        return null;
    }

    @Override
    public String[] dialogue() {
        return new String[] {"Accept Invite to " + dungeonParty.getLeader().getUsername() + "'s" + " Party.", "Decline."};
    }

    @Override
    public void specialAction() {
        p.setDialogueActionId(871);
    }


    public DungeonParty getDungeonParty() {
        return dungeonParty;
    }

    public int getSlot() {
        return slot;
    }

    public Player getP() {
        return p;
    }
}
