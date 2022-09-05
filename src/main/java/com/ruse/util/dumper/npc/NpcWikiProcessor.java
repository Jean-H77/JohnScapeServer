package com.ruse.util.dumper.npc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.definitions.NpcDropItem;
import com.ruse.util.dumper.HtmlGrab;
import com.ruse.util.dumper.HtmlGrabber;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NpcWikiProcessor {

    public static void main(String...args) {
        try {
            NpcDefinition.parseNpcs().load();
            ItemDefinition.init();
            HtmlGrabber grabber = new HtmlGrabber(false);
            HashMap<String, NPCDrops> tables = new HashMap<>();

            for (NpcDefinition definition : NpcDefinition.all()) {
                if (definition.getName() != null) {
                    String name = definition.getName().toLowerCase();
                    if (tables.containsKey(name)) {
                        tables.get(name).getNpcIds().add(definition.getId());
                    } else {
                        grabber.submit(new HtmlGrab(name, "npcs", html -> {
                            Document doc = Jsoup.parse(html);
                            WikiDropDefinition wikiDropDefinition = new WikiDropDefinition(doc);
                            List<NpcDropItem> drops = wikiDropDefinition.getItemDrops();
                            if (!drops.isEmpty()) {
                                tables.put(name, new NPCDrops(definition.getName().replaceAll(" ", "_"),
                                        Arrays.asList(definition.getId()), drops.toArray(new NpcDropItem[0])));
                            }
                        }));
                    }
                }
            }

            grabber.finish();


            File file = new File("./temp/osrs_drops.json");
            try (FileWriter writer = new FileWriter(file)) {
                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                writer.write(builder.toJson(tables.values()));
                System.out.println("Wrote: " + file);
            } catch (IOException e) {
                e.printStackTrace();
            }


            System.out.println("Finished.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
