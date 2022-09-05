package com.ruse.model;

import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AttributesSerializableTest {

    ItemContainer getInventory() {
        return new ItemContainer(null) {
            @Override
            public int capacity() {
                return 28;
            }

            @Override
            public StackType stackType() {
                return StackType.DEFAULT;
            }

            @Override
            public ItemContainer refreshItems() {
                return this;
            }

            @Override
            public ItemContainer full() {
                return this;
            }
        };
    }

    ItemContainer getBank() {
        return new ItemContainer(null) {
            @Override
            public int capacity() {
                return 350;
            }

            @Override
            public StackType stackType() {
                return StackType.STACKS;
            }

            @Override
            public ItemContainer refreshItems() {
                return this;
            }

            @Override
            public ItemContainer full() {
                return this;
            }
        };
    }

    @Test
    void item_attributes() {
        Item item = new Item(4151, 1);
        item.getAttributes().setInt("testing_int", 5);
        ItemContainer inventory = getInventory();
        ItemContainer bank = getBank();

        inventory.add(item);
        assertEquals(5, inventory.get(0).getAttributes().getInt("testing_int"));

        inventory.switchItem(bank, inventory.get(0), 0, true, false);
        assertEquals(5, bank.get(0).getAttributes().getInt("testing_int"));
    }

}
