package com.ruse.world.content.teleports;

import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDropItem;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.world.content.transportation.TeleportHandler;

public class TeleportMenuManager {
    private static final int INTERFACE_ID = 49550;

    private final Player player;

    private TeleportMenuItemChild selectedItem;
    private TeleportMenuItemParent selectedParent;
    private TeleportType currentType;
    private int page;

    public TeleportMenuManager(Player player) {
        this.player = player;
    }

    public void showInterface() {
        player.getPacketSender().sendConfig(385, 0);
        player.getPacketSender().sendConfig(374, 0);
        reset();
        sendTabChangeData();
        player.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void reset() {
        currentType = TeleportType.AREAS;
        selectedItem = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]).getChildren()[0];
        selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]);
        page = 0;
    }

    public void sendTabChangeData() {
        PacketBuilder packetBuilder = new PacketBuilder(20, Packet.PacketType.SHORT);

        for(int i = 0; i < 50; i++) {
            packetBuilder.putInt(49629+i);
            if(i >= TeleportData.TELEPORTS[currentType.getIndex()].length) {
                packetBuilder.putString("");
            } else {
               packetBuilder.putString(TeleportData.TELEPORTS[currentType.getIndex()][i].getTeleportName());
            }
        }
        player.getSession().queueMessage(packetBuilder);
        player.getPacketSender().sendScrollMax(49578, Math.max(246,TeleportData.TELEPORTS[currentType.getIndex()].length*22));
        sendTeleportData();
    }

    public void sendTeleportData() {
        player.getPacketSender().sendNpcHeadOnInterface(selectedItem.getNpcId(), 49573)
                        .sendString(49571, selectedItem.getTeleportName())
                                .sendItemContainer(NPCDrops.forId(selectedItem.getNpcId()) != null ? NPCDrops.forId(selectedItem.getNpcId()).getDropList() : new NpcDropItem[] {}, 49730)
                .sendString(49572, "("+(page+1)+"/"+selectedParent.getChildren().length+")");
    }

    public boolean handleButtonClick(int btnId) {
        if(handleButtonClickTabChange(btnId)) return true;
        if(handleParentClick(btnId)) return true;
        if(handleChildButtonClick(btnId)) return true;

        // next page
        if(btnId == -15968) {
            if(page < (selectedParent.getChildren().length-1)) {
                page++;
                selectedItem = selectedParent.getChildren()[page];
                sendTeleportData();
            }
            return true;
        } else if(btnId == -15971) { //previous page
            if(page != 0) {
                page--;
                selectedItem = selectedParent.getChildren()[page];
                sendTeleportData();
            }
            return true;
        } else {
            if(selectedItem != null) {
                if(btnId == -15962) {
                    TeleportHandler.teleportPlayer(player, selectedItem.getTeleportPosition(), com.ruse.world.content.transportation.TeleportType.TELE_TAB);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleButtonClickTabChange(int btnId) {

        boolean result = false;

        if(btnId == -15980) {

            currentType = TeleportType.AREAS;
            selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]);
            result = true;
            System.out.println("Here");
        } else if(btnId == -15979) {

            currentType = TeleportType.MINI_GAMES;
            selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]);
            result = true;
            System.out.println("Here2");
        }

        if(result) {
            selectedItem = selectedParent.getChildren()[0];
            sendTabChangeData();
        }

        return result;
    }

    private boolean handleChildButtonClick(int btnId) {
        if(btnId >= -15927 && btnId <= -15918) {
            int index = 15927 + btnId;
            if (index >= selectedParent.getChildren().length) return true;
            selectedItem = selectedParent.getChildren()[index];
            sendTeleportData();
            return true;
        }

        return false;
    }

    private boolean handleParentClick(int btnId) {
        if(btnId >= -15957 && btnId <= -15908) {
            int index = 15957 + btnId;
            if (index >= TeleportData.TELEPORTS[currentType.getIndex()].length) return true;
            page = 0;
            selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][index]);
            selectedItem = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][index]).getChildren()[page];
            sendTeleportData();
            return true;
        }

        return false;
    }
}
