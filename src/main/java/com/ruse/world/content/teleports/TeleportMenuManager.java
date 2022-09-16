package com.ruse.world.content.teleports;

import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDropItem;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.world.content.transportation.TeleportHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TeleportMenuManager {
    private static final int INTERFACE_ID = 49550;

    private final Player player;

    private TeleportMenuItemChild selectedItem;
    private TeleportMenuItemParent selectedParent;
    private TeleportType currentType;

    public void showInterface() {
        player.getPacketSender().sendConfig(389, 0);
        reset();
        sendTabChangeData();
        player.getPacketSender().sendInterface(49550);
    }

    public void reset() {
        currentType = TeleportType.AREAS;
        selectedItem = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]).getChildren()[0];
        selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]);
    }

    public void sendTabChangeData() {
        if(currentType == null) {
            reset();
        }

        PacketBuilder packetBuilder = new PacketBuilder(20, Packet.PacketType.SHORT);

        for(int i = 0; i < 30; i++) {
            packetBuilder.putInt(49570+i);
            if(i >= TeleportData.TELEPORTS[currentType.getIndex()].length) {
                packetBuilder.putString("");
            } else {
               packetBuilder.putString(TeleportData.TELEPORTS[currentType.getIndex()][i].getTeleportName());
            }
        }
        player.getSession().queueMessage(packetBuilder);
        sendTeleportData();
    }

    public void sendTeleportData() {
        player.getPacketSender().sendNpcHeadOnInterface(selectedItem.getNpcId(), 49619);
        player.getPacketSender().sendSpriteChange(49619, selectedItem.getCombatStyleType().getSpriteId());
        player.getPacketSender().sendString(49566, selectedItem.getTeleportName());
        player.getPacketSender().sendItemContainer(NPCDrops.forId(selectedItem.getNpcId()) != null ? NPCDrops.forId(selectedItem.getNpcId()).getDropList() : new NpcDropItem[] {}, 49601);

        for(int i = 0; i < 10; i++) {
            if(i >= selectedParent.getChildren().length) {
                player.getPacketSender().sendString(49609+i, "");
            } else {
                player.getPacketSender().sendString(49609+i, selectedParent.getChildren()[i].getTeleportName());
            }
        }
    }

    public boolean handleButtonClick(int btnId) {
        if(handleButtonClickTabChange(btnId)) return true;
        if(handleParentClick(btnId)) return true;
        if(handleChildButtonClick(btnId)) return true;

        if(btnId == -15915 && player.getInterfaceId() == INTERFACE_ID && selectedItem != null) {
            TeleportHandler.teleportPlayer(player, selectedItem.getTeleportPosition(), com.ruse.world.content.transportation.TeleportType.TELE_TAB);
            return true;
        }
        return false;
    }

    private boolean handleButtonClickTabChange(int btnId) {

        boolean result = false;

        if(btnId == -15980) {

            currentType = TeleportType.AREAS;
            selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]);
            result = true;

        } else if(btnId == -15979) {

            currentType = TeleportType.MINI_GAMES;
            selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]);
            result = true;

        } else if(btnId == -15978) {

            currentType = TeleportType.RAIDS;
            selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]);
            result = true;

        } else if(btnId == -15977) {

            currentType = TeleportType.MISC;
            selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][0]);
            result = true;

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
        if(btnId >= -15966 && btnId <= -15937) {
            int index = 15966 + btnId;
            if (index >= TeleportData.TELEPORTS[currentType.getIndex()].length) return true;
            selectedParent = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][index]);
            selectedItem = ((TeleportMenuItemParent) TeleportData.TELEPORTS[currentType.getIndex()][index]).getChildren()[0];
            sendTeleportData();
            return true;
        }

        return false;
    }
}
