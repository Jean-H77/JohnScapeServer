package com.ruse.world.content.upgrader;

import com.google.common.collect.ImmutableList;
import com.ruse.model.Item;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;


public enum Upgradeable {

    TWISTED_BOW(50997, new Item[]{ new Item(1420, 2), new Item(995, 175_000_000) }, 100, Category.WEAPONS),
    S_1(52325, new Item[]{ new Item(1420, 2), new Item(995, 175_000_000) }, 100, Category.WEAPONS),
    S_2(55736, new Item[]{ new Item(1420, 2), new Item(995, 175_000_000) }, 100, Category.WEAPONS),
    S_3(55741, new Item[]{ new Item(1420, 2), new Item(995, 175_000_000) }, 100, Category.WEAPONS),
    S_4(905, new Item[]{ new Item(1420, 2), new Item(995, 175_000_000) }, 100, Category.WEAPONS),
    S_5(919, new Item[]{ new Item(1420, 2), new Item(995, 175_000_000), new Item(4151, 2), new Item(6585, 3) }, 75, Category.WEAPONS),
    JUSTICIAR_HEAD_GUARD(52326, new Item[]{ new Item(1422, 1), new Item(995, 275_000_000) }, 100, Category.ARMOUR),
    JUSTICIAR_CHEST_GUARD(52327, new Item[]{ new Item(1422, 1), new Item(995, 275_000_000) }, 100, Category.ARMOUR),
    JUSTICIAR_LEGS_GUARD(52328, new Item[]{ new Item(1422, 1), new Item(995, 275_000_000) }, 100, Category.ARMOUR),
    A_1(54664, new Item[]{ new Item(1422, 1), new Item(995, 275_000_000) }, 100, Category.ARMOUR),
    A_2(54666, new Item[]{ new Item(1422, 1), new Item(995, 275_000_000) }, 100, Category.ARMOUR),
    A_3(54668, new Item[]{ new Item(1422, 1), new Item(995, 275_000_000) }, 100, Category.ARMOUR)
    ;

    private final int upgradedItemID;
    private final Item[] requiredItems;
    private final int successChance;
    private final Category category;

    private Upgradeable(int upgradedItemID, Item[] requiredItems, int successChance, Category category) {
        this.upgradedItemID = upgradedItemID;
        this.requiredItems = requiredItems;
        this.successChance = successChance;
        this.category = category;
    }

    public int getUpgradedItemID() {
        return this.upgradedItemID;
    }

    public Item[] getRequiredItems() {
        return this.requiredItems;
    }

    public int getSuccessChance() {
        return this.successChance;
    }

    public Category getCategory() {
        return this.category;
    }

    enum Category {
        ARMOUR(0),
        JEWELRY(1),
        WEAPONS(2),
        MISCELLANEOUS(3)
        ;

        private final int radioButtonId;

        private Category(int radioButtonId) {
            this.radioButtonId = radioButtonId;
        }

        public int getRadioButtonId() {
            return this.radioButtonId;
        }
    }

    public static final ImmutableList<Upgradeable> ARMOUR = ImmutableList.copyOf(Arrays.stream(values()).filter(upgradeable -> upgradeable.category == Category.ARMOUR).collect(Collectors.toList()));
    public static final ImmutableList<Upgradeable> JEWELRY = ImmutableList.copyOf(Arrays.stream(values()).filter(upgradeable -> upgradeable.category == Category.JEWELRY).collect(Collectors.toList()));
    public static final ImmutableList<Upgradeable> WEAPONS = ImmutableList.copyOf(Arrays.stream(values()).filter(upgradeable -> upgradeable.category == Category.WEAPONS).collect(Collectors.toList()));
    public static final ImmutableList<Upgradeable> MISCELLANEOUS = ImmutableList.copyOf(Arrays.stream(values()).filter(upgradeable -> upgradeable.category == Category.MISCELLANEOUS).collect(Collectors.toList()));

    public static final int[] ARMOUR_UPGRADEABLE_ITEM_IDS = new int[ARMOUR.size()];
    public static final int[] JEWELRY_UPGRADEABLE_ITEM_IDS = new int[JEWELRY.size()];
    public static final int[] WEAPONS_UPGRADEABLE_ITEM_IDS = new int[WEAPONS.size()];
    public static final int[] MISCELLANEOUS_UPGRADEABLE_ITEM_IDS = new int[MISCELLANEOUS.size()];

    public static void initItemIdsArray(ImmutableList<Upgradeable> arr, int[] arr1) {
        for(int i = 0; i < arr.size(); i++) {
            arr1[i] = arr.get(i).upgradedItemID;
        }
    }

    public static ImmutableList<Upgradeable> getUpgradeableListByCategory(Category category) {
        switch (category) {
            case ARMOUR:
                return ARMOUR;
            case JEWELRY:
                return JEWELRY;
            case WEAPONS:
                return WEAPONS;
            case MISCELLANEOUS:
                return MISCELLANEOUS;
            default:
                return null;
        }
    }

    public static int[] getUpgradeItemIdsByCategory(Category category) {
        switch (category) {
            case ARMOUR:
                return ARMOUR_UPGRADEABLE_ITEM_IDS;
            case JEWELRY:
                return JEWELRY_UPGRADEABLE_ITEM_IDS;
            case WEAPONS:
                return WEAPONS_UPGRADEABLE_ITEM_IDS;
            case MISCELLANEOUS:
                return MISCELLANEOUS_UPGRADEABLE_ITEM_IDS;
            default:
                return null;
        }
    }

    static  {
        initItemIdsArray(ARMOUR,ARMOUR_UPGRADEABLE_ITEM_IDS);
        initItemIdsArray(JEWELRY,JEWELRY_UPGRADEABLE_ITEM_IDS);
        initItemIdsArray(WEAPONS,WEAPONS_UPGRADEABLE_ITEM_IDS);
        initItemIdsArray(MISCELLANEOUS,MISCELLANEOUS_UPGRADEABLE_ITEM_IDS);
    }
}
