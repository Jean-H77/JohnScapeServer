package com.ruse.util.dumper.npc;


import java.util.Map;

import com.ruse.util.dumper.HtmlGrab;
import com.ruse.util.dumper.HtmlGrabber;
import com.ruse.model.definitions.NpcDefinition;

public class NpcWikiGrabber {

    public static void main(String...args) {
        try {
            NpcDefinition.parseNpcs().load();
            HtmlGrabber grabber = new HtmlGrabber(true);
            for (Map.Entry<Integer, NpcDefinition> def : NpcDefinition.getEntries()) {
                if (def.getValue().getName() != null) {
                    grabber.submit(new HtmlGrab(def.getValue().getName(), "npcs", null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
