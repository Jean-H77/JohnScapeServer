package com.ruse.world.content;

import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.definitions.NpcDropItem;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.impl.DropViewerSearch;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DropViewer {

    private static final int INTERFACE_ID = 41050;
    public static final int SEARCH_BUTTON_ID = -24479;
    public static final int ITEM_CONTAINER_ID = 41475;

    private final Player p;

    private List<NPCDrops> dropList;

    private NpcDropItem[] currentlyViewing;

    public DropViewer(Player p) {
        this.p = p;
    }

    public void displayInterface() {
        p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void displayNewSearch(List<NPCDrops> _dropList) {
        int startingNpcNameId = 41061;
        clearNames();
        if (_dropList.isEmpty()) {
            p.getPacketSender().sendString(startingNpcNameId, "No drops to view");
            return;
        }
        dropList = _dropList;
        for (int i = 0; i < 30; i++) {
            if (i == dropList.size()) break;
            Optional<Integer> npcId = dropList.get(i).getFirstValidNpcId();
            p.getPacketSender().sendString(startingNpcNameId + i, npcId.isPresent() ? NpcDefinition.forId(npcId.get()).getName() : "");
        }
        p.getPacketSender().sendScrollMax(41060, Math.max(206, Math.min(600, dropList.size() * 20)));
    }

    public boolean handleButtonClick(int btnId) {
        if (dropList == null) return false;
        int index = 24475 + btnId;

        if (index >= dropList.size()) return false;
        NPCDrops drops = dropList.get(index);
        if (drops != null) {
            displayItems(drops);
            return true;
        }
        return false;
    }

    public void showNpcDropListById(int npcId) {
        NPCDrops drops = NPCDrops.forId(npcId);
        if (drops != null) {
            clearNames();
            p.getPacketSender().sendString(41061, NpcDefinition.forId(npcId).getName());
            displayItems(drops);
            p.getPacketSender().sendInterface(INTERFACE_ID);
        } else {
            p.getPacketSender().sendMessage("@red@This npc has no drops.");
        }
    }

    public void displayItems(NPCDrops drops) {
        NpcDropItem[] dropItems = drops.getDropList();
        if(Arrays.equals(currentlyViewing, dropItems)) return;
        clearInterface();
        currentlyViewing = dropItems;
        p.getPacketSender().sendScrollMax(41092, Math.max(266, dropItems.length * 38));

        int startingItemNameId = 41193;
        int startingChanceId = 41295;
        int index = 0;

        p.getPacketSender().sendItemContainer(dropItems, ITEM_CONTAINER_ID);
        PacketBuilder builder = new PacketBuilder(30, Packet.PacketType.SHORT);
        builder.putShort(dropItems.length);
        for (NpcDropItem item : dropItems) {
            int chance = item.getChance();
            String name = item.getName();
            builder.putString(1 + "/" + (chance == 0 ? 1 : chance));
            builder.putInt(startingChanceId+index);
            builder.putString(name);
            builder.putInt(startingItemNameId+index);
            index++;
        }
        p.getSession().queueMessage(builder);
    }

    public void clearNames() {
        int startingNpcNameId = 41061;
        for(int i = 0; i < 30; i++) {
            p.getPacketSender().sendString(startingNpcNameId+i, "");
        }
    }

    private void clearInterface() {
        p.getPacketSender().sendInterfaceItems(ITEM_CONTAINER_ID, new CopyOnWriteArrayList<>());
        int startingItemNameId = 41193;
        int startingChanceId = 41295;
        PacketBuilder builder = new PacketBuilder(30, Packet.PacketType.SHORT);
        builder.putShort(7);
        for(int i = 0; i < 7; i++) {
            builder.putString("");
            builder.putInt(startingChanceId+i);
            builder.putString("");
            builder.putInt(startingItemNameId+i);
        }
        p.getSession().queueMessage(builder);
    }

    public void search() {
        p.getPacketSender().sendEnterInputPrompt("Enter NPC name to search for:");
        p.setInputHandling(new DropViewerSearch());
    }

    public Predicate<Map.Entry<Integer, NPCDrops>> checkName(String name) {
        return s -> NpcDefinition.forId(s.getKey()).getName().toLowerCase().startsWith(name);
    }

    public List<NPCDrops> getFilteredNpcDropList(String name) {
        return NPCDrops.getDrops().entrySet()
                .parallelStream()
                .filter(checkName(name))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public Player getP() {
        return this.p;
    }

    public List<NPCDrops> getDropList() {
        return this.dropList;
    }

    public NpcDropItem[] getCurrentlyViewing() {
        return this.currentlyViewing;
    }

    public void setDropList(List<NPCDrops> dropList) {
        this.dropList = dropList;
    }

    public void setCurrentlyViewing(NpcDropItem[] currentlyViewing) {
        this.currentlyViewing = currentlyViewing;
    }
}
