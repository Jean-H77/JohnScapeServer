package com.ruse.util.definition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ruse.GameSettings;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NpcDefinition;
import org.apache.commons.lang3.tuple.Pair;

public class AddMissingDefinitions {

    private static final String NEW_ITEM_DEFS = GameSettings.DEFINITION_DIRECTORY + "item_definitions2.json";
    private static final String NEW_NPC_DEFS = GameSettings.DEFINITION_DIRECTORY + "npc_definitions2.json";

    public static void main(String...args) {
        try {
            NpcDefinition.parseNpcs().load();
            ItemDefinition.init();

            BufferedReader itemReader = new BufferedReader(new FileReader("./temp/item_ids.txt"));
            BufferedReader npcReader = new BufferedReader(new FileReader("./temp/npc_ids.txt"));

            itemReader.lines().forEach(line -> {
                Pair<Integer, String> pair = getPair(line);
                Preconditions.checkState(pair != null, "null: " + line);
                if (!ItemDefinition.isPresent(pair.getLeft())) {
                    ItemDefinition.add(new ItemDefinition(pair.getLeft(), pair.getRight()));
                }
            });

            npcReader.lines().forEach(line -> {
                Pair<Integer, String> pair = getPair(line);
                Preconditions.checkState(pair != null, "null: " + line);
                if (!NpcDefinition.isPresent(pair.getLeft())) {
                    NpcDefinition.add(new NpcDefinition(pair.getLeft(), pair.getRight()));
                }
            });


            try (FileWriter writer = new FileWriter(NEW_ITEM_DEFS)) {
                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                List<ItemDefinition> list = ItemDefinition.getEntries().stream().map(Map.Entry::getValue).collect(Collectors.toList());
                list.sort(Comparator.comparingInt(ItemDefinition::getId));
                writer.write(builder.toJson(list));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (FileWriter writer = new FileWriter(NEW_NPC_DEFS)) {
                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                List<NpcDefinition> list = NpcDefinition.getEntries().stream().map(Map.Entry::getValue).collect(Collectors.toList());
                list.sort(Comparator.comparingInt(NpcDefinition::getId));
                writer.write(builder.toJson(list));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Pair<Integer, String> getPair(String line) {
        String[] split = line.split(" ", 2);
        if (split.length < 2) {
            return null;
        } else {
            return Pair.of(Integer.parseInt(split[0]), split[1]);
        }
    }

}
