package com.ruse.util.definition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.OptionalInt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ruse.GameSettings;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDefinition;

public class NpcDropSplitter {
/*
    public static void main(String...args) {
        NPCDrops.load();
        NpcDefinition.parseNpcs().load();
        HashSet<int[]> parsed = new HashSet<>();

        NPCDrops.getDrops().forEach((key, value) -> {
            if (!parsed.contains(value.getNpcIds())) {
                OptionalInt id = value.getFirstValidNpcId();
                if (id.isPresent()) {
                    File file = getNpcDropFile(id.getAsInt(), "npc_drops");
                    try (FileWriter writer = new FileWriter(file)) {
                        Gson builder = new GsonBuilder().setPrettyPrinting().create();
                        writer.write(builder.toJson(value));
                        System.out.println("Wrote: " + file);
                        parsed.add(value.getNpcIds());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
*/
    public static File getNpcDropFile(int npcId, String directory) {
        String dir = GameSettings.DEFINITION_DIRECTORY + directory + "/";
        NpcDefinition def = NpcDefinition.forId(npcId);
        String name;

        if (def != null && def.getName() != null) {
            name = NpcDefinition.forId(npcId).getName().replaceAll(" ", "_").toLowerCase();
        } else {
            name = npcId + "";
        }

        if (!new File(dir).exists()) {
            new File(dir).mkdirs();
        }

        File file;
        int i = 0;
        do {
            file = new File(dir + name
                    + (i++ == 0 ? "" : "_" + i)
                    + ".json");
        } while (file.exists());

        return file;
    }

}
