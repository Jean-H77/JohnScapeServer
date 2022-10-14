package com.ruse.world.content.combat.dungeon;

import com.ruse.model.Position;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.clan.ClanChat;
import com.ruse.world.content.clan.ClanChatManager;

import java.time.Duration;

public class DungeonManager {

    private static final int TIMER_OVERLAY_STRING_ID = 41563;
    private static final int TIMER_INTERFACE_ID = 41560;

    private final Player p;

    public DungeonManager(Player p) {
        this.p = p;
    }

    public void startDungeon(Dungeon dungeon) {
        if(!p.isInDungeon()) {
            ClanChat clanChat = p.getCurrentClanChat();
            if (hasValidClanChat(clanChat)) {
                dungeon.setClanChat(clanChat);
                dungeon.start();
                sendStartAlert(clanChat, dungeon.getDungeonName());
                enterDungeon(dungeon);
            }
        } else {
            p.getPacketSender().sendMessage("@red@You are already in a dungeon.");
        }
    }

    public void enterDungeon(Dungeon dungeon) {
        ClanChat clanChat = p.getCurrentClanChat();
        if(hasValidClanChat(clanChat)) {
            p.setInDungeon(true);
            p.setCurrentDungeon(dungeon);
            p.moveTo(dungeon.getStartPosition().setZ(dungeon.getHeight()));
            sendJoinAlert(clanChat, dungeon.getDungeonName());
            p.getPacketSender().sendWalkableInterface(TIMER_INTERFACE_ID);
        }
    }

    public void leaveDungeon(boolean moveToDefaultPosition) {
        if(p.isInDungeon() && p.getCurrentDungeon() != null) {
            p.getPacketSender().sendMessage("@red@You have left the " + p.getCurrentDungeon().getDungeonName() + " dungeon");
            if(moveToDefaultPosition) {
                Position toMoveTo = p.getCurrentDungeon().getExitPosition().setZ(0);
                p.moveTo(toMoveTo).setPosition(toMoveTo);
            }
            p.setInDungeon(false);
            p.setCurrentDungeon(null);
            p.getPacketSender().sendWalkableInterface(-1);
        }
    }

    public boolean dungeonEnteranceClick(int objId) {
        switch (objId) {
            case 36000:

                System.out.println("Hello");

            break;
        }
        return false;
    }

    public void handleTimerOverlay() {
        if(p.getCurrentDungeon() != null && p.isInDungeon()) {
            Duration duration = p.getCurrentDungeon().timeLeft();
            p.getPacketSender().sendString(TIMER_OVERLAY_STRING_ID, String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart()));
        }
    }

    private void sendStartAlert(ClanChat clanChat, String dungeonName) {
        ClanChatManager.sendMessage(clanChat, "@dre@[ClanChat] " + p.getUsername() + " has started the " + dungeonName);
    }

    private void sendJoinAlert(ClanChat clanChat, String dungeonName) {
        ClanChatManager.sendMessage(clanChat, "@dre@[ClanChat] " + p.getUsername() + " has joined the " + dungeonName);
    }

    public boolean hasValidClanChat(ClanChat clanChat) {
        if(clanChat != null)
            return true;
        p.getPacketSender().sendMessage("@red@You need to be in a clan to start or join a dungeon.");
        return false;
    }
}
