package com.ruse.util.definition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ruse.GameSettings;
import com.ruse.model.Item;
import com.ruse.model.container.impl.Shop;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDefinition;

public class ShopSplitter {

    public static void main(String...args) {
        NpcDefinition.parseNpcs().load();
        Shop.ShopManager.load();
        ItemDefinition.init();

        Shop.ShopManager.getShops().forEach((key, value) -> {
            File file = new File(GameSettings.DEFINITION_DIRECTORY + "shops/"
                    + value.getId() + "_"
                    +  value.getName().replaceAll(" ", "_").toLowerCase() + ".json");

            if (file.exists()) {
                throw new IllegalStateException(file.getPath());
            }

            // You have to set Item#slot to transient or it will write the slot also
            try (FileWriter writer = new FileWriter(file)) {
                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                JsonObject object = new JsonObject();
                object.addProperty("id", value.getId());
                object.addProperty("name", value.getName());
                object.addProperty("currency", value.getCurrency().getId());
                List<Item> items = Arrays.stream(value.getItems()).filter(i -> i.getId() != -1).collect(Collectors.toList());
                object.add("items", builder.toJsonTree(items));
                writer.write(builder.toJson(object));
                System.out.println("Wrote: " + file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
