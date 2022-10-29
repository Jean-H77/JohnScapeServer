package com.ruse.world.content.zombies;

import com.ruse.model.Item;

public enum ZombiesClassType {
    WARRIOR(new Item[]{new Item(4151,1)}, new Item[]{new Item(4151,1)}),
    RANGER(new Item[]{new Item(4151,1)}, new Item[]{new Item(4151,1)}),
    WIZARD(new Item[]{new Item(4151,1)}, new Item[]{new Item(4151,1)})
    ;

    private final Item[] equipmentItems;
    private final Item[] inventoryItems;

    private ZombiesClassType(Item[] equipmentItems, Item[] inventoryItems) {
        this.equipmentItems = equipmentItems;
        this.inventoryItems = inventoryItems;
    }

    public static Item[][] getInventoryAndEquipment(ZombiesClassType class_) {
        if(class_ == WARRIOR) {
            return new Item[][] {WARRIOR.inventoryItems, WARRIOR.equipmentItems};
        } else if(class_ == RANGER) {
            return new Item[][] {RANGER.inventoryItems, RANGER.equipmentItems};
        } else if(class_ == WIZARD) {
            return new Item[][] {WIZARD.inventoryItems, WIZARD.equipmentItems};
        }
        return null;
    }
}
