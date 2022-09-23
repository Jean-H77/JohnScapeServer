package com.ruse.world.content.upgrader;

import com.google.common.collect.ImmutableList;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@RequiredArgsConstructor
public class UpgradeMachineManager {

    private static final int INTERFACE_ID = 50010;
    private static final int SHOWN_ITEM_CONTAINER_ID = 50153;
    private static final int UPGRADEABLES_ITEM_CONTAINER_ID = 50152;
    private static final int REQUIRED_ITEMS_ITEM_CONTAINER_ID = 50155;
    private static final int ITEM_NAME_STRING_ID = 50038;
    private static final int TOTAL_ATTEMPTS_STRING_ID = 50039;
    private static final int SUCCESS_CHANCE_STRING_ID = 50047;
    private static final int UPGRADE_BUTTON_ID = -15493;
    private static final int WEAPON_CATEGORY_BUTTON_ID = -15513;
    private static final int ARMOUR_CATEGORY_BUTTON_ID = -15514;
    private static final int JEWELRY_CATEGORY_BUTTON_ID = -15380;
    private static final int MISCELLANEOUS_CATEGORY_BUTTON_ID = -15379;

    private final Player p;
    private final HashMap<Integer, UpgradeEntry> upgradeAttempts = new HashMap<>();
    private Upgradeable.Category selectedCategory;
    private Upgradeable selectedItem;

    public void openInterface() {
        reset();
        sendCategoryItemData();
        sendSelectedItemData();
        p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void reset() {
        selectedCategory = Upgradeable.Category.ARMOUR;
        selectedItem = Upgradeable.ARMOUR.get(0);
        p.getPacketSender().sendConfig(390, 0);
        p.getPacketSender().sendConfig(331, 0);
    }

    public void sendCategoryItemData() {
        int[] itemIds = Upgradeable.getUpgradeItemIdsByCategory(selectedCategory);
        if(itemIds == null) {
            return;
        }
        p.getPacketSender().sendConfig(331,0);
        PacketBuilder packetBuilder = new PacketBuilder(40, Packet.PacketType.SHORT);
        packetBuilder.putShort(itemIds.length);
        for (int itemId : itemIds) {
            packetBuilder.putInt(itemId + 1);
        }
        p.getSession().queueMessage(packetBuilder);
    }

    public void sendSelectedItemData() {
        UpgradeEntry upgradeEntry = upgradeAttempts.get(selectedItem.getUpgradedItemID());

        p.getPacketSender().sendString(ITEM_NAME_STRING_ID, ItemDefinition.forId(selectedItem.getUpgradedItemID()).getName())
                .sendString(TOTAL_ATTEMPTS_STRING_ID, "Total Attempts: " + ((upgradeEntry == null) ? "0" : upgradeEntry.attempts))
                .sendString(SUCCESS_CHANCE_STRING_ID, "Success Rate: @cya@" + selectedItem.getSuccessChance() + "%")
                .sendItemOnInterface(SHOWN_ITEM_CONTAINER_ID, selectedItem.getUpgradedItemID(), 1)
                .sendItemContainer(selectedItem.getRequiredItems(), REQUIRED_ITEMS_ITEM_CONTAINER_ID);
    }

    public boolean handleButtonClick(int ID) {
        switch (ID) {
            case WEAPON_CATEGORY_BUTTON_ID:
                switchCategory(Upgradeable.Category.WEAPONS);
                return true;
            case ARMOUR_CATEGORY_BUTTON_ID:
                switchCategory(Upgradeable.Category.ARMOUR);
                return true;
            case JEWELRY_CATEGORY_BUTTON_ID:
                switchCategory(Upgradeable.Category.JEWELRY);
                return true;
            case MISCELLANEOUS_CATEGORY_BUTTON_ID:
                switchCategory(Upgradeable.Category.MISCELLANEOUS);
                return true;
        }

        if(ID >= -15484 && ID <= -15385) {
            int index = ID + 15484;
            ImmutableList<Upgradeable> upgradeables = Upgradeable.getUpgradeableListByCategory(selectedCategory);
            if(upgradeables != null && upgradeables.size() > index) {
                Upgradeable temp = upgradeables.get(index);
                if(temp != selectedItem) {
                    selectedItem = upgradeables.get(index);
                    sendSelectedItemData();
                }
            }
        }
        return false;
    }

    public void switchCategory(Upgradeable.Category category) {
        if(category != selectedCategory) {
            ImmutableList<Upgradeable> upgradeables;
            if((upgradeables = Upgradeable.getUpgradeableListByCategory(category)) != null && !upgradeables.isEmpty()) {
                selectedCategory = category;
                selectedItem = upgradeables.get(0);
                sendCategoryItemData();
                sendSelectedItemData();
            } else {
                p.getPacketSender().sendConfig(390, selectedCategory.getRadioButtonId());
                p.getPacketSender().sendMessage("@red@This category does not contain upgradeables");
                reset();
            }
        }
    }

    @Data
    static class UpgradeEntry {
        private final int attempts;
        private final int successfulUpgrades;
    }
}
