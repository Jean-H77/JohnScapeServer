package com.ruse.world.content.dungeons;

import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.Input;
import com.ruse.world.World;
import com.ruse.world.content.dialogue.DialogueManager;

public class InviteMemberInput extends Input {
    private final DungeonParty dungeonParty;
    private final int slot;
    public InviteMemberInput(DungeonParty dungeonParty, int slot) {
        this.dungeonParty = dungeonParty;
        this.slot = slot;
    }

    @Override
    public void handleSyntax(Player player, String text) {
        Player p = World.getPlayerByName(text);
        if(p == null) {
            player.getPacketSender().sendMessage("@red@This player is either offline or does not exist.");
        } else {
            DungeonPartyManager dungeonPartyManager = p.getDungeonPartyManager();
            if(dungeonPartyManager.getDungeonParty() == null && !p.isInDungeon()) {
                if(dungeonParty != null && dungeonParty.getLeader().equals(p)) {
                    player.getPacketSender().sendMessage("@red@You cannot invite yourself to your own party.");
                } else {
                    DialogueManager.start(p, new InviteDialogueOption(dungeonParty, slot, p));
                }
            } else {
                player.getPacketSender().sendMessage("@red@Cannot invite this player.");
            }
        }
    }
}
