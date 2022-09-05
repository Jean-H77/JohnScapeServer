package com.ruse.util.definition;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;

public class DumpItemValues {

    public static void main(String...args) {
        ItemDefinition.init();
        List<ItemDefinition> list = ItemDefinition.getEntries().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        list.sort((item1, item2) -> item2.getValue() - item1.getValue());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./temp/item_values.txt"))) {
            list.forEach(item -> {
                try {
                    writer.write(item.getName() + ", " + item.getId() + ", " + Misc.insertCommasToNumber(item.getValue()));
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception cause) {
            cause.printStackTrace();
        }
    }

}
