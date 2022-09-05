package com.ruse.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

/**
 * A variation of the {@link Attributes} meant for items.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class ItemAttributes {

    private final Item item;
    private final Map<String, Integer> ints = new HashMap<>();

    public ItemAttributes(Item item) {
        this.item = item;
    }

    public void copy(ItemAttributes itemAttributes) {
        Preconditions.checkState(ints.isEmpty());
        for (Map.Entry<String, Integer> entry : itemAttributes.ints.entrySet())
            ints.put(entry.getKey(), entry.getValue());
    }

    public boolean hasAttributes() {
        return !ints.isEmpty();
    }

    public Map<String, Integer> getInts() {
        return ints;
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public int getInt(String key, int fail) {
        return ints.containsKey(key) ? ints.get(key) : fail;
    }

    public void setInt(String key, int value) {
        ints.put(key, value);
    }



}
