package com.ruse.world.content.dungeons;

import com.ruse.model.entity.character.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonParty {
    private final Player leader;
    private final HashMap<Integer, Player> playersSlotMap = new HashMap<>();
    private final List<Player> playerList = new ArrayList<>();
    private Dungeon currentDungeon;

    public DungeonParty(Player leader) {
        this.leader = leader;
        playersSlotMap.put(0, leader);
        playerList.add(leader);
    }

    public Player getLeader() {
        return leader;
    }

    public HashMap<Integer, Player> getPlayers() {
        return playersSlotMap;
    }

    public Dungeon getCurrentDungeon() {
        return currentDungeon;
    }

    public void setCurrentDungeon(Dungeon currentDungeon) {
        this.currentDungeon = currentDungeon;
    }

    public void removePlayer(Player player, int slot) {
        playerList.remove(player);
        playersSlotMap.remove(slot);
        player.setInDungeon(false);
        player.getDungeonPartyManager().setDungeonParty(null);
        player.getDungeonPartyManager().resetInterface();
    }

    public void disband() {
        for(Player player : playerList) {
            player.getDungeonPartyManager().setDungeonParty(null);
            player.setInDungeon(false);
            player.getDungeonPartyManager().setDungeonParty(null);
            player.getDungeonPartyManager().resetInterface();
            player.getPacketSender().sendMessage("@red@Your dungeon party has been disbanded.");
        }
        playersSlotMap.clear();
        playerList.clear();
    }

    public void addPlayer(Player player, int slot) {
        playerList.add(player);
        playersSlotMap.put(slot, player);
        player.getDungeonPartyManager().setDungeonParty(this);
    }

    public boolean slotOpen(int slot) {
        return !playersSlotMap.containsKey(slot);
    }
}
