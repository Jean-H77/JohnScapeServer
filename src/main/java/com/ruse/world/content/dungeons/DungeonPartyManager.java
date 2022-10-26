package com.ruse.world.content.dungeons;

import com.ruse.model.entity.character.player.Player;

import java.util.HashMap;
import java.util.Map;

public class DungeonPartyManager {
    public static final int INTERFACE_ID = 55000;

    private final Player p;
    private DungeonParty dungeonParty;

    public DungeonPartyManager(Player p) {
        this.p = p;
    }

    public void showInterface() {
        p.getPacketSender().sendConfig(383, 0);
        if(dungeonParty == null) {
            resetInterface();
        } else {
            if(dungeonParty.getLeader().equals(p)) {
                p.getPacketSender().sendString(55071, "Disband Party");
            } else {
                p.getPacketSender().sendString(55071, "Leave Party");
            }
            updatePlayersList();
        }
        p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void updatePlayersList() {
        if(dungeonParty != null) {
            for (Map.Entry<Integer, Player> playerEntry : dungeonParty.getPlayers().entrySet()) {
                updateDungeonPartyMemberListInterface(playerEntry.getValue(), dungeonParty.getLeader().equals(playerEntry.getValue()));
            }
        }
    }

    public void updateDungeonPartyMemberListInterface(Player p, boolean isLeader) {
        for(int i = 0; i < 5; i++) {
            Player player;
            if((player = dungeonParty.getPlayers().get(i)) != null) {
                if(isLeader) {
                    p.getPacketSender().sendInterfaceVisibility(55077+(i*3), true)
                            .sendInterfaceVisibility(55077+((i*3)+2), true)
                                    .changeButtonHover(55077+(i*3), 55077+((i*3)+2), 1515, 1514, "Remove");
                } else {
                    p.getPacketSender().sendInterfaceVisibility(55077+(i*3), false)
                            .sendInterfaceVisibility(55077+((i*3)+2), false);
                }
                p.getPacketSender().sendString(55072+i, player.getUsername());
            } else {
                if(isLeader) {
                    p.getPacketSender().sendInterfaceVisibility(55077+(i*3), true)
                            .sendInterfaceVisibility(55077+((i*3)+2), true)
                            .changeButtonHover(55077+(i*3), 55077+((i*3)+2), 1516, 1517, "Invite")
                            .sendString(55072+i, "Invite a Player...");
                } else {
                    p.getPacketSender().sendInterfaceVisibility(55077+(i*3), false)
                            .sendInterfaceVisibility(55077+((i*3)+2), false)
                            .sendString(55072+i, "");
                }
            }
        }
    }

    public void resetInterface() {
        for(int i = 0; i < 5; i++) {
            p.getPacketSender().sendInterfaceVisibility(55077+(i*3), false)
                    .sendInterfaceVisibility(55079+((i*3)+2), false)
                    .sendString(55072+i, "")
                    .sendString(55071, "Create Party");
        }
    }

    public boolean handleButtonClick(int btnId) {
        if(btnId == -10468) {
            if(dungeonParty == null && !p.isInDungeon()) {
                dungeonParty = new DungeonParty(p);
                p.getPacketSender().sendMessage("@red@You have created a dungeon party!");
                p.getPacketSender().sendString(55071, "Disband Party");
                updatePlayersList();
                return true;
            } else {
                dungeonParty.disband();
            }
        }

        if(dungeonParty != null && dungeonParty.getLeader().equals(p)) {
            HashMap<Integer,Player> partyMembers= dungeonParty.getPlayers();
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
