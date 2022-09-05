package com.ruse.model.definitions;

import java.util.Arrays;

import com.ruse.model.Item;
import com.ruse.util.Misc;

/**
 * Represents a npc drop item
 */
public class NpcDropItem {

    /**
     * The item name.
     */
    private String name;

    /**
     * The id.
     */
    private int id;

    /**
     * Array holding all the amounts of this item.
     */
    private final int[] count;

    /**
     * The chance of getting this item.
     */
    private int chance;

    /**
     * New npc drop item
     *
     * @param id
     *            the item
     * @param count
     *            the count
     * @param chance
     *            the chance
     */
    public NpcDropItem(int id, int[] count, int chance) {
       this(null, id, count, chance);
    }

    /**
     * New npc drop item
     *
     * @param name
     *            the name
     * @param id
     *            the item
     * @param count
     *            the count
     * @param chance
     *            the chance
     */
    public NpcDropItem(String name, int id, int[] count, int chance) {
        this.name = name;
        this.id = id;
        this.count = count;
        this.chance = chance;
    }

    @Override
    public String toString() {
        return "NpcDropItem{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", count=" + Arrays.toString(count) +
                ", chance=" + chance +
                '}';
    }

    /**
     * Set the item id
     * @param id the item id
     */
    protected void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the item id.
     *
     * @return The item id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the chance.
     *
     * @return The chance.
     */
    public int[] getCount() {
        return count;
    }

    /**
     * Gets the chance.
     *
     * @return The chance.
     */
    public int getChance() {
        return chance;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the item
     *
     * @return the item
     */
    public Item getItem() {
        int amount = 0;
        for (int i = 0; i < count.length; i++)
            amount += count[i];
        if (amount > count[0])
            amount = count[0] + Misc.getRandom(count[1]);
        return new Item(id, amount);
    }
}
