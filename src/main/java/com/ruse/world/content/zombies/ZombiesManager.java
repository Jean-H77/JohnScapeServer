package com.ruse.world.content.zombies;

import com.ruse.model.Item;
import com.ruse.model.entity.character.player.Player;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ZombiesManager {
    transient private static final int INTERFACE_ID = 51065;
    transient private static final int INVENTORY_ITEM_CONTAINER_ID = 51152;
    transient private static final int WARRIOR_PROGRESS_BAR_ID = 51132;
    transient private static final int RANGER_PROGRESS_BAR_ID = 51133;
    transient private static final int WIZARD_PROGRESS_BAR_ID = 51134;
    transient private static final int WARRIOR_BUTTON_ID = -14450;
    transient private static final int RANGER_BUTTON_ID = -14449;
    transient private static final int WIZARD_BUTTON_ID = -14448;
    transient private static final int CONFIG_ID = 351;

    transient private final Player p;

    public void openInterface() {

        Item[][] items = ZombiesClassType.getInventoryAndEquipment(selectedClass.classType);
        if(items == null) return;

        p.getPacketSender().sendConfig(CONFIG_ID, configState)
                .sendItemContainer(items[0], INVENTORY_ITEM_CONTAINER_ID)
                        .sendInterface(INTERFACE_ID);
    }

}
