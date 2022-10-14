package com.ruse.world.content.combat.dungeon;

import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.clan.ClanChat;

import java.time.Duration;
import java.util.List;

public class DungeonViewer {

    private static final int INTERFACE_ID = 41198;
    private static final int SCROLL_BAR_ID = 41206;
    private static final int MAX_DUNGEON_LIST_SIZE = 40;

    private final Player p;

    public DungeonViewer(Player p) {
        this.p = p;
    }

    public boolean handleButton(int btn) {
        if(p.getInterfaceId() != INTERFACE_ID) return false;
        if(btn >= -24329 && btn <= -24289) {
            int index = 24329 + btn;
            ClanChat clanChat = p.getCurrentClanChat();
            if(p.getDungeonManager().hasValidClanChat(clanChat)) {
                if(p.getCurrentDungeon() == null && !p.isInDungeon()) {
                    List<Dungeon> dungeons = clanChat.getDungeons();
                    if (!dungeons.isEmpty() && dungeons.size() > index) {
                        Dungeon dungeon = dungeons.get(index);
                        p.getDungeonManager().enterDungeon(dungeon);
                    }
                } else {
                    p.getPacketSender().sendMessage("@red@Please leave your current dungeon before joining another.");
                }
            }
        }
        return false;
    }

    public void displayActiveDungeonList() {
        ClanChat clanChat = p.getCurrentClanChat();
        if(p.getDungeonManager().hasValidClanChat(clanChat)) {
            displayContents(clanChat);
        }
    }

    public void displayContents(ClanChat clanChat) {
        List<Dungeon> dungeons = clanChat.getDungeons();

        int dungeonNameId = 41247;
        int timerId = 41287;

        for(int i = 0; i < MAX_DUNGEON_LIST_SIZE; i++) {
            if(dungeons.size() > i) {
                Dungeon dungeon = dungeons.get(i);
                if(dungeon != null) {
                    p.getPacketSender().sendString(dungeonNameId, dungeon.getDungeonName());
                    Duration duration = dungeon.timeLeft();
                  //  duration.toMinutes()
                 //   p.getPacketSender().sendString(timerId, String.format("%02d:%02d", duration.toMinutes(), duration.to()));
                }
            } else {
                p.getPacketSender().sendString(dungeonNameId, "");
                p.getPacketSender().sendString(timerId, "");
            }
            dungeonNameId++;
            timerId++;
        }
        p.getPacketSender().sendScrollMax(SCROLL_BAR_ID, Math.max(281, dungeons.size()*28))
                .sendInterface(INTERFACE_ID);
    }

    public Player getP() {
        return this.p;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DungeonViewer)) return false;
        final DungeonViewer other = (DungeonViewer) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$p = this.getP();
        final Object other$p = other.getP();
        if (this$p == null ? other$p != null : !this$p.equals(other$p)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DungeonViewer;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $p = this.getP();
        result = result * PRIME + ($p == null ? 43 : $p.hashCode());
        return result;
    }

    public String toString() {
        return "DungeonViewer(p=" + this.getP() + ")";
    }
}
