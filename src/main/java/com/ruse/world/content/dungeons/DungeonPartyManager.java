package com.ruse.world.content.dungeons;

import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.world.content.dungeons.dungeonImpl.CastleUnderworld;
import com.ruse.world.content.dungeons.dungeonImpl.ForbiddenIsland;
import com.ruse.world.content.dungeons.dungeonImpl.ForgottenTemple;
import com.ruse.world.content.dungeons.dungeonImpl.TowerOfTheUndead;

import java.util.HashMap;
import java.util.Map;

public class DungeonPartyManager {
    public static final int INTERFACE_ID = 55000;

    private final Player p;
    private DungeonParty dungeonParty;
    private Dungeon pickedDungeon;

    public DungeonPartyManager(Player p) {
        this.p = p;
    }

    public void showInterface() {
        p.getPacketSender().sendConfig(375, 0);
        p.getPacketSender().sendConfig(383, 2);
        if(dungeonParty == null) {
            resetInterface();
        } else {
            if(dungeonParty.getLeader().equals(p)) {
                p.getPacketSender().sendString(55071, "Disband Party");
            } else {
                p.getPacketSender().sendString(55071, "Leave Party");
            }
        }
        p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void updatePlayersList() {
        if(dungeonParty != null) {
            for (Map.Entry<Integer, Player> playerEntry : dungeonParty.getPlayersSlotMap().entrySet()) {
                updateDungeonPartyMemberListInterface(playerEntry.getValue(), dungeonParty.getLeader().equals(playerEntry.getValue()));
            }
        }
    }

    public void updateDungeonPartyMemberListInterface(Player p, boolean isLeader) {
        PacketBuilder packetBuilder = new PacketBuilder(10, Packet.PacketType.SHORT);
        for(int i = 0; i < 5; i++) {
            Player player;
            if((player = dungeonParty.getPlayersSlotMap().get(i)) != null) {
                if(isLeader) {
                    packetBuilder.put(1);
                } else {
                    packetBuilder.put(0);
                }
                packetBuilder.putString(player.getUsername());
            } else {
                if(isLeader) {
                    packetBuilder.put(2);
                } else {
                    packetBuilder.put(3);
                }
            }
        }
        p.getSession().queueMessage(packetBuilder);
    }

    public void resetInterface() {
        PacketBuilder packetBuilder = new PacketBuilder(10, Packet.PacketType.SHORT);
        for (int i = 0; i < 5; i++) {
            packetBuilder.put(4);
        }
        p.getSession().queueMessage(packetBuilder);
        p.getPacketSender().sendString(55071, "Create Party");

    }

    public boolean handleButtonClick(int btnId) {
        if(btnId == -10468) {
            if(dungeonParty == null && !p.isInDungeon()) {
                dungeonParty = new DungeonParty(p);
                pickedDungeon = new ForgottenTemple(dungeonParty);
                p.getPacketSender().sendMessage("@red@You have created a dungeon party!");
                p.getPacketSender().sendString(55071, "Disband Party");
                p.getPacketSender().sendConfig(375, 0);
                return true;
            } else {
                if(dungeonParty.getLeader().equals(p)) {
                    dungeonParty.disband();
                } else {
                    if(dungeonParty.getPlayersSlotMap().containsValue(p)) {
                        dungeonParty.removePlayer(p, dungeonParty.getPlayersSlotMap()
                                .entrySet()
                                .stream()
                                .filter(it -> it.getValue() == p).findFirst().get().getKey());
                    }
                }
            }
        }

        if(dungeonParty != null && dungeonParty.getLeader().equals(p)) {
            HashMap<Integer,Player> partyMembers= dungeonParty.getPlayersSlotMap();
            int slot = -1;
            if (btnId == -10459) {
                slot = 0;
            } else if (btnId == -10456) {
                slot = 1;
            } else if (btnId == -10453) {
                slot = 2;
            } else if (btnId == -10450) {
                slot = 3;
            } else if (btnId == -10447) {
                slot = 4;
            } else if(btnId == -10442 && dungeonParty.getCurrentDungeon() == null) {
                dungeonParty.setCurrentDungeon(pickedDungeon);
                dungeonParty.getCurrentDungeon().start();
            } else if(btnId == -10521) {
                pickedDungeon = new ForgottenTemple(dungeonParty);
            } else if(btnId == -10520) {
                pickedDungeon = new TowerOfTheUndead(dungeonParty);
            } else if(btnId == -10519) {
                pickedDungeon = new CastleUnderworld(dungeonParty);
            } else if(btnId == -10518) {
                pickedDungeon = new ForbiddenIsland(dungeonParty);
            }

            if(slot != -1) {
                if(partyMembers.containsKey(slot)) {
                    Player toRemove = partyMembers.get(slot);
                    if(dungeonParty.getLeader().equals(toRemove)) {
                        p.getPacketSender().sendMessage("@red@You will need to disband the party as a leader to remove yourself.");
                    } else {
                        dungeonParty.removePlayer(toRemove, slot);
                        toRemove.getDungeonPartyManager().resetInterface();
                        updatePlayersList();
                    }
                } else {
                    p.setInputHandling(new InviteMemberInput(dungeonParty, slot));
                    p.getPacketSender().sendEnterInputPrompt("Enter player name to invite.");
                }
                return true;
            }
        }

        return false;
    }

    public DungeonParty getDungeonParty() {
        return dungeonParty;
    }

    public void setDungeonParty(DungeonParty dungeonParty) {
        this.dungeonParty = dungeonParty;
    }
}
