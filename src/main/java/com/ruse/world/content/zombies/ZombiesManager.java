package com.ruse.world.content.zombies;

import com.ruse.model.Item;
import com.ruse.model.entity.character.player.Player;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ZombiesManager {
    private static final int INTERFACE_ID = 51065;
    private static final int INVENTORY_ITEM_CONTAINER_ID = 51152;
    private static final int WARRIOR_PROGRESS_BAR_ID = 51132;
    private static final int RANGER_PROGRESS_BAR_ID = 51133;
    private static final int WIZARD_PROGRESS_BAR_ID = 51134;
    private static final int WARRIOR_BUTTON_ID = -14450;
    private static final int RANGER_BUTTON_ID = -14449;
    private static final int WIZARD_BUTTON_ID = -14448;
    private static final int WARRIOR_EXP_STRING_ID = 51149;
    private static final int RANGER_EXP_STRING_ID = 51150;
    private static final int WIZARD_EXP_STRING_ID = 51151;
    private static final int WARRIOR_LEVEL_STRING_ID = 51143;
    private static final int RANGER_LEVEL_STRING_ID = 51145;
    private static final int WIZARD_LEVEL_STRING_ID = 51147;
    private static final int WARRIOR_NEXT_LEVEL_STRING_ID = 51144;
    private static final int RANGER_NEXT_LEVEL_STRING_ID = 51146;
    private static final int WIZARD_NEXT_LEVEL_STRING_ID = 51148;
    private static final int CONFIG_ID = 351;

    private static final int[] expRequiredToLevel = {
        250, //level 1
        300,
        350,
        400,
        500,
        600,
        700,
        800,
        1000,
        1250,
        1500,
        2000,
        2500,
        3000,
        4000,
        5000,
        7500,
        10000,
        15000,
        30000, //level 20
    };

    private final Player p;

    private final ZombieAttributes zombieAttributes = new ZombieAttributes();

    public void openInterface() {
        if(zombieAttributes.getSelectedClass() == null) {
            zombieAttributes.setSelectedClass(zombieAttributes.getWarrior());
        }
        Item[][] items = ZombiesClassType.getInventoryAndEquipment(zombieAttributes.getSelectedClass().classType);
        if(items == null) return;

        p.getPacketSender().sendConfig(CONFIG_ID, zombieAttributes.getConfigState())
                .sendItemContainer(items[0], INVENTORY_ITEM_CONTAINER_ID)
                .sendProgressBar(zombieAttributes.getWarrior().exp, expRequiredToLevel[zombieAttributes.getWarrior().level], WARRIOR_PROGRESS_BAR_ID)
                .sendString(WARRIOR_EXP_STRING_ID, zombieAttributes.getWarrior().exp + "/" + expRequiredToLevel[zombieAttributes.getWarrior().level])
                .sendProgressBar(zombieAttributes.getWarrior().exp, expRequiredToLevel[zombieAttributes.getWarrior().level], RANGER_PROGRESS_BAR_ID)
                .sendString(RANGER_EXP_STRING_ID, zombieAttributes.getRanger().exp + "/" + expRequiredToLevel[zombieAttributes.getRanger().level])
                .sendProgressBar(zombieAttributes.getWizard().exp, expRequiredToLevel[zombieAttributes.getWizard().level], WIZARD_PROGRESS_BAR_ID)
                .sendString(WIZARD_EXP_STRING_ID, zombieAttributes.getWizard().exp + "/" + expRequiredToLevel[zombieAttributes.getWizard().level])
                .sendString(WARRIOR_LEVEL_STRING_ID, "Lv. " + (zombieAttributes.getWarrior().level+1))
                .sendString(WARRIOR_NEXT_LEVEL_STRING_ID, "Lv. " + (zombieAttributes.getWarrior().level+2))
                .sendString(RANGER_LEVEL_STRING_ID, "Lv. " + (zombieAttributes.getRanger().level+1))
                .sendString(RANGER_NEXT_LEVEL_STRING_ID, "Lv. " + (zombieAttributes.getRanger().level+2))
                .sendString(WIZARD_LEVEL_STRING_ID, "Lv. " + (zombieAttributes.getWizard().level+1))
                .sendString(WIZARD_NEXT_LEVEL_STRING_ID, "Lv. " + (zombieAttributes.getWizard().level+2))
                        .sendInterface(INTERFACE_ID);
    }


    @RequiredArgsConstructor
    static class ZombiesClass {
        private final ZombiesClassType classType;
        private int exp;
        private int level;

        public void incrementExpBy(int amount) {
            exp += amount;
        }

        public void checkForLevelUp() {
            if(level == 20) return;
            levelUp();
        }

        public void levelUp() {

        }

    }
}
