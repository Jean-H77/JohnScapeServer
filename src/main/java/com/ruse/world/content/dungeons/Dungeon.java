package com.ruse.world.content.dungeons;

import com.ruse.model.GameObject;
import com.ruse.model.Position;
import com.ruse.model.entity.character.npc.NPC;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.World;
import com.ruse.world.content.CustomObjects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Dungeon {

    protected final List<NPC> npcList = new ArrayList<>();
    protected final List<GameObject> gameObjectList = new ArrayList<>();
    protected final DungeonParty dungeonParty;
    protected final Position startPosition;
    protected LocalDateTime duration;

    protected Dungeon(DungeonParty dungeonParty, Position startPosition) {
        this.dungeonParty = dungeonParty;
        this.startPosition = startPosition;
    }

    public void end() {
        clear();
        dungeonParty.getPlayerList().forEach(this::leave);
    }

    public void clear() {
        npcList.forEach(World::deregister);
        gameObjectList.forEach(CustomObjects::deleteGlobalObject);
        npcList.clear();
        gameObjectList.clear();
        dungeonParty.setCurrentDungeon(null);
    }

    public void start() {
        dungeonParty.getPlayerList().forEach( player -> {
            player.setInDungeon(true);
            player.setPreviousPosition(player.getPosition());
            player.moveTo(startPosition);
            player.getPacketSender().sendInterfaceRemoval();
        });
        npcList.forEach(World::register);
    }

    public void leave(Player player) {
        dungeonParty.getPlayerList().remove(player);
        if(dungeonParty.getPlayersSlotMap().containsValue(player)) {
            dungeonParty.getPlayersSlotMap().remove(dungeonParty.getPlayersSlotMap()
                    .entrySet()
                    .stream()
                    .filter(it -> it.getValue() == player).findFirst().get().getKey());
        }
        player.setInDungeon(false);
        player.moveTo(player.getPreviousPosition());
        player.getDungeonPartyManager().setDungeonParty(null);

        if(dungeonParty.getPlayerList().isEmpty() && dungeonParty.getPlayersSlotMap().isEmpty()) {
            clear();
        }
    }
}
