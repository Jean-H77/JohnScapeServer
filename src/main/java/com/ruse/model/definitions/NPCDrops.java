package com.ruse.model.definitions;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruse.GameSettings;
import com.ruse.model.Item;
import com.ruse.model.entity.character.npc.NpcItemDropping;
import com.ruse.util.json.JsonLoader;
import lombok.extern.java.Log;

/**
 * Controls the npc drops
 * 
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>, Gabbe &
 *         Samy
 * 
 */
@Log
public class NPCDrops {

	/**
	 * The map containing all the npc drops.
	 */
	private static Map<Integer, NPCDrops> dropControllers = new HashMap<Integer, NPCDrops>();

	/**
	 * Gets the NPC drop controller by an id.
	 *
	 * @return The NPC drops associated with this id.
	 */
	public static NPCDrops forId(int id) {
		return dropControllers.get(id);
	}

	public static Map<Integer, NPCDrops> getDrops() {
		return dropControllers;
	}

	public static void load() {
		dropControllers.clear();
		NpcItemDropping.ItemDropAnnouncer.init();
		File directory = new File(GameSettings.DEFINITION_DIRECTORY + "npc_drops/");
		if (directory.exists()) {
			Arrays.stream(Objects.requireNonNull(directory.listFiles())).forEach(f -> {
				getLoader(f.getPath()).loadObject();
			});

			System.out.println("Loaded " + dropControllers.size() + " drop tables.");

//			dropControllers.entrySet().stream().forEach(entry -> {
//				for (NpcDropItem item : entry.getValue().drops) {
//					item.chance = item.getChance().random;
//					item.name = ItemDefinition.forId(item.id).getName();
//				}
//
//				File file = NpcDropSplitter.getNpcDropFile(entry.getKey(), "npc_drops2");
//				try (FileWriter writer = new FileWriter(file)) {
//					Gson builder = new GsonBuilder().setPrettyPrinting().create();
//					writer.write(builder.toJson(entry.getValue()));
//					System.out.println("Wrote: " + file);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			});
		} else {
			throw new IllegalStateException("No drop directory.");
		}
	}

	private static JsonLoader getLoader(String path) {
		return new JsonLoader() {

			@Override
			public void load(JsonObject reader, Gson builder) {
				int[] npcIds = builder.fromJson(reader.get("npcIds"), int[].class);
				NpcDropItem[] drops = builder.fromJson(reader.get("drops"), NpcDropItem[].class);
				NPCDrops d = new NPCDrops();
				Arrays.stream(npcIds).forEach(id -> d.npcIds.add(id));
				d.drops = drops;
				for (int id : npcIds) {
					dropControllers.put(id, d);
				}

				for (NpcDropItem drop : d.drops) {
					if (drop.getId() == 0) {
						String name = drop.getName();
						Preconditions.checkState(drop.getName() != null, "No name: " + drop.toString() + ", " + d.toString());
						boolean osrs = name.contains("(osrs)");
						boolean noted = name.contains("(noted)");
						if (noted)
							name = name.replace("(noted)", "").trim();
						if (osrs)
							name = name.replace("(osrs)", "").trim();
						ItemDefinition definition = ItemDefinition.getDefinitionForName(name, osrs ? 30_000 : 0);
						Preconditions.checkState(definition != null, "No item definition: " + name + ", " + d.toString());
						Preconditions.checkState(!osrs && definition.getId() < 30_000 || osrs && definition.getId() > 30_000, "Invalid item state: " + osrs
								+ ", " + definition.getId() + ", " + name + ", " + d.toString());
						int itemId = definition.getId();
						if (noted) {
							int itemId2 = definition.getId();
							itemId = Item.getNoted(itemId);
							Preconditions.checkState(itemId2 != itemId, "Cannot note: " + name + ", " + d.toString());
						}

						drop.setId(itemId);
					}
				}
				d.drops = Stream.of(drops)
						.sorted(Comparator.comparing(NpcDropItem::getChance).reversed())
						.toArray(NpcDropItem[]::new);
			}

			@Override
			public String filePath() {
				return path;
			}
		};
	}

	private NPCDrops() {
	}

	public NPCDrops(String name, List<Integer> npcIds, NpcDropItem[] drops) {
		this.name = name;
		this.npcIds = npcIds;
		this.drops = drops;
	}


	@Override
	public String toString() {
		return "NPCDrops{" +
				"name='" + name + '\'' +
				", npcIds=" + npcIds +
				'}';
	}

	/**
	 * The npc name (if available).
	 */
	private String name;

	/**
	 * The id's of the NPC's that "owns" this class.
	 */
	private List<Integer> npcIds = Lists.newArrayList();

	/**
	 * All the drops that belongs to this class.
	 */
	private NpcDropItem[] drops;


	/**
	 * Gets the drop list
	 *
	 * @return the list
	 */
	public NpcDropItem[] getDropList() {
		return drops;
	}

	/**
	 * Gets the first valid npc id.
	 *
	 * @return an optional containing the first valid npc id
	 */
	public Optional<Integer> getFirstValidNpcId() {
		return npcIds.stream().filter(i -> i != -1).findFirst();
	}

	/**
	 * Gets the npcIds
	 *
	 * @return the npcIds
	 */
	public List<Integer> getNpcIds() {
		return npcIds;
	}

}