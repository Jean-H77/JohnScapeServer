package com.ruse.model.definitions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ruse.GameSettings;
import com.ruse.model.BonusValue;
import com.ruse.model.SkillLevel;
import com.ruse.model.container.impl.Equipment;
import com.ruse.world.content.collectionlog.CollectionLogTab;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 * This file manages every item definition, which includes
 * their name, description, value, skill requirements, etc.
 * 
 * @author relex lawl
 */
@Log
public class ItemDefinition {
	
	/**
	 * The file where item definitions are stored.
	 */
	private static final String ITEM_DEFINITION_FILE = GameSettings.DEFINITION_DIRECTORY + "item_definitions.json";

	/**
	 * The file where item actions are stored.
	 */
	private static final String ITEM_ACTIONS_FILE = GameSettings.DEFINITION_DIRECTORY + "item_actions.json";
	private static final Logger log = Logger.getLogger(ItemDefinition.class.getName());

	/**
	 * ItemDefinition array containing all items' definition values.
	 */
	private static Map<Integer, ItemDefinition> definitions = new HashMap<>();

	/**
	 * Empty bonuses.
	 */
	private static final List<BonusValue> EMPTY_BONUSES = Collections.unmodifiableList(Lists.newArrayList());

	/**
	 * Empty requirements.
	 */
	private static final List<SkillLevel> EMPTY_REQUIREMENTS = Collections.unmodifiableList(Lists.newArrayList());
	
	/**
	 * Loading all item definitions
	 */
	public static void init() {
		definitions.clear();
		Preconditions.checkState(new File(ITEM_DEFINITION_FILE).exists(), "No item def file.");
		try (BufferedReader reader = new BufferedReader(new FileReader(ITEM_DEFINITION_FILE))) {
			ItemDefinition[] read = new Gson().fromJson(reader.lines().collect(Collectors.joining()), ItemDefinition[].class);
			Arrays.stream(read).forEach(definition -> {
				for (BonusValue bonus : definition.getBonuses()) {
					Preconditions.checkState(bonus.getBonus() != null, definition.getBonuses().toString());
				}
				for (SkillLevel level : definition.getRequirements()) {
					Preconditions.checkState(level.getSkill() != null, definition.getRequirements().toString());
				}

				if(Arrays.stream(CollectionLogTab.values()).anyMatch(collectionLogTab -> Arrays.stream(collectionLogTab.getLogs()).anyMatch(log1 -> Arrays.stream(log1.getRequiredItemIds()).anyMatch(it -> it == definition.id)))) {
					definition.isCollectionLogItem = true;
				}

				definitions.put(definition.id, definition);
			});

			System.out.println("Loaded " + definitions.size() + " item definitions.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(ITEM_ACTIONS_FILE))) {
			JsonObject[] itemActions = new Gson().fromJson(reader.lines().collect(Collectors.joining()), JsonObject[].class);
			for (JsonObject itemAction : itemActions) {
				int id = itemAction.get("id").getAsInt();
				ItemDefinition definition = definitions.get(id);
				if (definition != null) {
					JsonArray jsonActionArray = itemAction.get("actions").getAsJsonArray();
					for (int index = 0; index < jsonActionArray.size(); index++) {
						if (!jsonActionArray.get(index).isJsonNull()) {
							definition.actions[index] = jsonActionArray.get(index).getAsString();
						}
					}
				}
			}
			System.out.println("Loaded " + itemActions.length + " item action definitions.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets an ItemDefinition for the name
	 */
	public static ItemDefinition getDefinitionForName(String itemName) {
		return getDefinitionForName(itemName, 0);
	}

	/**
	 * Gets an ItemDefinition for the name
	 */
	public static ItemDefinition getDefinitionForName(String itemName, int minimumId) {
		int id = getItemId(itemName, minimumId);
		if (id == -1) {
			System.out.println("No item definition for name: " + itemName);
			return null;
		} else {
			return ItemDefinition.forId(id);
		}
	}

	public static int getItemId(String itemName) {
		return getItemId(itemName, 0);
	}

	/**
	 * Get item id for name.
	 */
	public static int getItemId(String itemName, int minimumId) {
		for (Map.Entry<Integer, ItemDefinition> definitions : getEntries()) {
			if (definitions.getKey() < minimumId)
				continue;
			if (definitions.getValue().getName().equalsIgnoreCase(itemName)) {
				return definitions.getValue().getId();
			}
		}
		return -1;
	}

	/**
	 * Gets the item definition correspondent to the id.
	 *
	 * @param id	The id of the item to fetch definition for.
	 * @return		definitions[id].
	 */
	public static ItemDefinition forId(int id) {
		if (!definitions.containsKey(id)) {
			return new ItemDefinition(id);
		} else {
			return definitions.get(id);
		}
	}

	/**
	 * Add an item definition.
	 * @throws IllegalStateException if definition already present
	 */
	public static void add(ItemDefinition itemDefinition) {
		Preconditions.checkState(definitions.get(itemDefinition.getId()) == null, "Item definition already present.");
		definitions.put(itemDefinition.getId(), itemDefinition);
	}

	/**
	 * Gets the definition set.
	 */
	public static Set<Map.Entry<Integer, ItemDefinition>> getEntries() {
		return Collections.unmodifiableSet(definitions.entrySet());
	}

	/**
	 * Check if an item is present.
	 */
	public static boolean isPresent(int id) {
		return definitions.containsKey(id);
	}

	/**
	 * The id of the item.
	 */
	private int id = 0;

	/**
	 * If the template id is set, this definition will defer to another definition for
	 * every value except the name and id.
	 */
	private int templateId;

	/**
	 * The name of the item.
	 */
	private String name = "None";

	/**
	 * The item's description.
	 */
	private String description = "Null";

	/**
	 * The item's actions.
	 */
	private transient String[] actions = new String[5];

	/**
	 * The item's shop value.
	 */
	private int value;

	/**
	 * Flag to check if item is stackable.
	 */
	private boolean stackable;

	/**
	 * Flag that checks if item is noted.
	 */
	private boolean noted;

	/**
	 * The equipment type.
	 */
	private EquipmentType equipmentType = EquipmentType.NONE;

	/**
	 * The bonuses.
	 */
	private List<BonusValue> bonuses = null;

	/**
	 * The requirements.
	 */
	private List<SkillLevel> requirements = null;

	@Getter
	@Setter
	private boolean isCollectionLogItem;

	/**
	 * Create new ItemDefinition.
	 */
	public ItemDefinition() {
	}

	/**
	 * Create new ItemDefinition.
	 */
	public ItemDefinition(int id) {
		this.id = id;
	}

	/**
	 * Create new ItemDefinition.
	 */
	public ItemDefinition(int id, String name) {
		this.id = id;
		this.name = name;
	}

	private ItemDefinition getTemplateOrThis() {
		if (templateId > 0) {
			Objects.requireNonNull(forId(templateId));
			return forId(templateId);
		} else {
			return this;
		}
	}

	@Override
	public String toString() {
		return "[ItemDefinition(" + id + ")] - Name: " + name + "; equipment slot: " + getEquipmentSlot() + "; value: "
				+ value + "; stackable ? " + Boolean.toString(stackable) + "; noted ? " + Boolean.toString(noted) + ";";
	}

	/**
	 * Gets the item's id.
	 * 
	 * @return id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the item's name.
	 * 
	 * @return name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the item's description.
	 * 
	 * @return	description.
	 */
	public String getDescription() {
		return getTemplateOrThis().description;
	}

	/**
	 * Checks if the item is stackable.
	 * 
	 * @return	stackable.
	 */
	public boolean isStackable() {
		if(noted)
			return true;
		return getTemplateOrThis().stackable;
	}

	/**
	 * Gets the item's shop value.
	 * 
	 * @return	value.
	 */
	public int getValue() {
		return isNoted() ? ItemDefinition.forId(getId() - 1).getValue() : getTemplateOrThis().value;
	}
	
	/**
	 * Gets the item's equipment slot index.
	 * 
	 * @return	equipmentSlot.
	 */
	public int getEquipmentSlot() {
		return getTemplateOrThis().equipmentType.slot;
	}

	public String getAction(int index) {
		if (index >= actions.length || actions[index] == null) {
			return "none";
		} else {
			return actions[index];
		}
	}

	/**
	 * Checks if item is noted.
	 * 
	 * @return noted.
	 */
	public boolean isNoted() {
		return getTemplateOrThis().noted;
	}

	/**
	 * Checks if item is full body.
	 */
	public boolean isFullBody() {
		return getTemplateOrThis().equipmentType.equals(EquipmentType.PLATEBODY);
	}
	
	/**
	 * Checks if item is full helm.
	 */
	public boolean isFullHelm() {
		return getTemplateOrThis().equipmentType.equals(EquipmentType.FULL_HELMET);
	}

	public boolean isTwoHanded() {
		return getTemplateOrThis().equipmentType == EquipmentType.TWO_HANDED_WEAPON;
	}

	/**
	 * Gets the bonuses.
	 */
	public List<BonusValue> getBonuses() {
		if (getTemplateOrThis().bonuses == null) {
			return EMPTY_BONUSES;
		} else {
			return getTemplateOrThis().bonuses;
		}
	}

	/**
	 * Gets the requirements.
	 */
	public List<SkillLevel> getRequirements() {
		if (getTemplateOrThis().requirements == null) {
			return EMPTY_REQUIREMENTS;
		} else {
			return getTemplateOrThis().requirements;
		}
	}

	public boolean isCollectionLogItem() {
		return this.isCollectionLogItem;
	}

	public void setCollectionLogItem(boolean isCollectionLogItem) {
		this.isCollectionLogItem = isCollectionLogItem;
	}

	/**
	 * The equipment type.
	 */
	public enum EquipmentType {
		HAT(Equipment.HEAD_SLOT),
		CAPE(Equipment.CAPE_SLOT),
		SHIELD(Equipment.SHIELD_SLOT),
		GLOVES(Equipment.HANDS_SLOT),
		BOOTS(Equipment.FEET_SLOT),
		AMULET(Equipment.AMULET_SLOT),
		RING(Equipment.RING_SLOT),
		ARROWS(Equipment.AMMUNITION_SLOT),
		FULL_MASK(Equipment.HEAD_SLOT),
		FULL_HELMET(Equipment.HEAD_SLOT),
		BODY(Equipment.BODY_SLOT),
		PLATEBODY(Equipment.BODY_SLOT),
		LEGS(Equipment.LEG_SLOT),
		WEAPON(Equipment.WEAPON_SLOT),
		TWO_HANDED_WEAPON(Equipment.WEAPON_SLOT),
		NONE(-1)
		;
		
		EquipmentType(int slot) {
			this.slot = slot;
		}
		
		private int slot;
	}

//	public static void main(String...args2) {
//		ItemDefinition definition = null;
//		try {
//			File file = new File("./data/def/items.txt");
//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			List<String> lines = reader.lines().collect(Collectors.toList());
//			boolean twoHanded = false;
//			for (String line: lines) {
//				if (line.contains("inish")) {
//					definitions.put(definition.id, definition);
//					definition = new ItemDefinition();
//					twoHanded = false;
//					continue;
//				}
//				String[] args = line.split(": ");
//				if (args.length <= 1)
//					continue;
//				String token = args[0], value = args[1];
//				if (line.contains("Bonus[")) {
//					String[] other = line.split("]");
//					int index = Integer.valueOf(line.substring(6, other[0].length()));
//					double bonus = Double.valueOf(value);
//					//definition.bonus[index] = bonus;
//					if (definition.bonuses == null)
//						definition.bonuses = Lists.newArrayList();
//					definition.bonuses.add(new BonusValue(Bonus.values()[index], bonus));
//					continue;
//				}
//				if (line.contains("Requirement[")) {
//					String[] other = line.split("]");
//					int index = Integer.valueOf(line.substring(12, other[0].length()));
//					int requirement = Integer.valueOf(value);
//					if (definition.requirements == null)
//						definition.requirements = Lists.newArrayList();
//					definition.requirements.add(new SkillLevel(Skill.values()[index], requirement));
//					continue;
//				}
//				switch (token.toLowerCase()) {
//					case "item id":
//						int id = Integer.valueOf(value);
//						definition = new ItemDefinition();
//						definition.id = id;
//						twoHanded = false;
//						break;
//					case "name":
//						if(value == null)
//							continue;
//						definition.name = value;
//						break;
//					case "examine":
//						definition.description = value;
//						break;
//					case "value":
//						int price = Integer.valueOf(value);
//						definition.value = price;
//						break;
//					case "stackable":
//						definition.stackable = Boolean.valueOf(value);
//						break;
//					case "noted":
//						definition.noted = Boolean.valueOf(value);
//						break;
//					case "double-handed":
//						//definition.isTwoHanded = Boolean.valueOf(value);
//						twoHanded = Boolean.valueOf(value);
//						break;
//					case "equipment type":
//						definition.equipmentType = EquipmentType.valueOf(value);
//						if (definition.equipmentType == EquipmentType.WEAPON && twoHanded) {
//							definition.equipmentType = EquipmentType.TWO_HANDED_WEAPON;
//						}
//						break;
//					case "is weapon":
//						//definition.weapon = Boolean.valueOf(value);
//						if (!Boolean.valueOf(value) && (definition.equipmentType == EquipmentType.WEAPON || definition.equipmentType == EquipmentType.TWO_HANDED_WEAPON)) {
//							definition.equipmentType = EquipmentType.NONE;
//						}
//						break;
//				}
//			}
//			reader.close();
//
//			try (FileWriter writer = new FileWriter("./temp/item_definitions.json")) {
//				Gson builder = new GsonBuilder().setPrettyPrinting().create();
//				List<ItemDefinition> defs = Lists.newArrayList(definitions.values());
//				defs.sort(Comparator.comparingInt(ItemDefinition::getId));
//				writer.write(builder.toJson(defs));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
}
