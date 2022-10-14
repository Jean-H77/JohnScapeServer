package com.ruse.model.definitions;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruse.GameSettings;
import com.ruse.util.json.JsonLoader;
import com.ruse.world.content.collectionlog.CollectionLogTab;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A single npc definition.
 * 
 * @author lare96
 */
public class NpcDefinition {

	/** An array containing all of the npc definitions. */
	private static Map<Integer, NpcDefinition> definitions = new HashMap<>();

	/**
	 * Gets an NpcDefinition
	 */
	public static NpcDefinition forId(int id) {
		return definitions.containsKey(id) ? definitions.get(id) : new NpcDefinition(id, "null");
	}

	/**
	 * Get the definition entries.
	 */
	public static Set<Map.Entry<Integer, NpcDefinition>> getEntries() {
		return Collections.unmodifiableSet(definitions.entrySet());
	}

	/**
	 * Generates a list of all NpcDefinitions.
	 * Do not use this in runtime it's only for utility.
	 */
	public static List<NpcDefinition> all() {
		return getEntries().stream().map(Map.Entry::getValue).collect(Collectors.toList());
	}

	/**
	 * Check if an NpcDefinition is present
	 */
	public static boolean isPresent(int id) {
		return definitions.containsKey(id);
	}

	/**
	 * Add an NpcDefinition
	 * @throws IllegalStateException if npc definition is already present
	 */
	public static void add(NpcDefinition npcDefinition) {
		Preconditions.checkState(definitions.get(npcDefinition.getId()) == null, "Item definition already present.");
		definitions.put(npcDefinition.getId(), npcDefinition);
	}

	/**
	 * Prepares the dynamic json loader for loading npc definitions.
	 *
	 * @return the dynamic json loader.
	 * @throws Exception
	 *             if any errors occur while preparing for load.
	 */
	public static JsonLoader parseNpcs() {
		return new JsonLoader() {
			@Override
			public void load(JsonObject reader, Gson builder) {
				int index = reader.get("id").getAsInt();
				NpcDefinition definition = new NpcDefinition();
				definition.setId(index);
				definition.setName(reader.get("name").getAsString());
				if (reader.has("examine")) {
					definition.setExamine(reader.get("examine").getAsString());
				} else {
					definition.setExamine("");
				}
				definition.setCombatLevel(reader.get("combat")
						.getAsInt());
				definition.setNpcSize(reader.get("size").getAsInt());
				definition.setAttackable(reader.get("attackable")
						.getAsBoolean());
				definition.setAggressive(reader.get("aggressive")
						.getAsBoolean());
				definition.setRetreats(reader.get("retreats")
						.getAsBoolean());
				definition.setPoisonous(reader.get("poisonous")
						.getAsBoolean());
				definition.setRespawnTime(reader.get("respawn")
						.getAsInt());
				definition.setMaxHit(reader.get("maxHit").getAsInt());
				definition.setHitpoints(reader.get("hitpoints")
						.getAsInt());
				definition.setAttackSpeed(reader.get("attackSpeed")
						.getAsInt());
				definition.setAttackAnimation(reader.get("attackAnim")
						.getAsInt());
				definition.setDefenceAnimation(reader
						.get("defenceAnim").getAsInt());
				definition.setDeathAnimation(reader.get("deathAnim")
						.getAsInt());
				definition.setAttackBonus(reader.get("attackBonus")
						.getAsInt());
				definition.setDefenceMelee(reader.get("defenceMelee")
						.getAsInt());
				definition.setDefenceRange(reader.get("defenceRange")
						.getAsInt());
				definition.setDefenceMage(reader.get("defenceMage")
						.getAsInt());
				if (reader.has("slayerLevel")) {
					definition.setSlayerLevel(reader.get("slayerLevel")
							.getAsInt());
				}
				definitions.put(definition.id, definition);


				if(Arrays.stream(CollectionLogTab.BOSSES.getLogs()).anyMatch(log -> log.getName().equalsIgnoreCase(definition.name))) {
					definition.isCollectionLogNpc = true;
				}


			}

			@Override
			public String filePath() {
				return GameSettings.DEFINITION_DIRECTORY + "npc_definitions.json";
			}
		};
	}

	/** The id of the npc. */
	private int id;

	/** The name of the npc. */
	private String name;

	/** The examine of the npc. */
	private String examine;

	/** The combat level of the npc. */
	private int combat;

	/** The npc size. */
	private int size;

	/** If the npc is attackable. */
	private boolean attackable;

	/** If the npc is aggressive. */
	private boolean aggressive;

	/** If the npc retreats. */
	private boolean retreats;

	/** If the npc poisons. */
	private boolean poisonous;

	/** Time it takes for this npc to respawn. */
	private int respawn;

	/** The max hit of this npc. */
	private int maxHit;

	/** The amount of hp this npc has. */
	private int hitpoints;

	/** The attack speed of this npc. */
	private int attackSpeed;

	/** The attack animation of this npc. */
	private int attackAnim;

	/** The defence animation of this npc. */
	private int defenceAnim;

	/** The death animation of this npc. */
	private int deathAnim;

	/** This npc's attack bonus. */
	private int attackBonus;

	/** This npc's melee resistance. */
	private int defenceMelee;

	/** This npc's range resistance. */
	private int defenceRange;

	/** This npc's defence resistance. */
	private int defenceMage;

	/** This npc's slayer level required to attack. */
	private int slayerLevel;

	private boolean isCollectionLogNpc;

	public NpcDefinition() {
		name = "null";
		examine = "null";
		id = -1;
	}

	public NpcDefinition(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Gets the id of the npc.
	 * 
	 * @return the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of the npc.
	 * 
	 * @param id
	 *            the id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the name of the npc.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name for this npc.
	 * 
	 * @param name
	 *            the name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the examine info for this npc.
	 * 
	 * @return the examine.
	 */
	public String getExamine() {
		return examine;
	}

	/**
	 * Sets the examine info for this npc.
	 * 
	 * @param examine
	 *            the examine info to set.
	 */
	public void setExamine(String examine) {
		this.examine = examine;
	}

	/**
	 * Gets the combat level of this npc.
	 * 
	 * @return the combat level.
	 */
	public int getCombatLevel() {
		return combat;
	}

	/**
	 * Sets the combat level for this npc.
	 * 
	 * @param combatLevel
	 *            the combat level to set.
	 */
	public void setCombatLevel(int combat) {
		this.combat = combat;
	}

	/**
	 * Gets the size of this npc.
	 * 
	 * @return the npc size.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size of this npc.
	 * 
	 * @param size
	 *            the npc size to set.
	 */
	public void setNpcSize(int size) {
		this.size = size;
	}

	/**
	 * Gets if this npc is attackable.
	 * 
	 * @return true if this npc is attackable.
	 */
	public boolean isAttackable() {
		return attackable;
	}

	/**
	 * Sets if this npc is attackable.
	 * 
	 * @param attackable
	 *            the attackable to set.
	 */
	public void setAttackable(boolean attackable) {
		this.attackable = attackable;
	}

	/**
	 * Gets if this npc is aggressive.
	 * 
	 * @return true if this npc is aggressive.
	 */
	public boolean isAggressive() {
		return aggressive;
	}

	/**
	 * Sets if this npc is aggressive.
	 * 
	 * @param aggressive
	 *            the aggressive to set.
	 */
	public void setAggressive(boolean aggressive) {
		this.aggressive = aggressive;
	}

	/**
	 * Gets if this npc retreats or not when their health is low.
	 * 
	 * @return true if this npc retreats.
	 */
	public boolean isRetreats() {
		return retreats;
	}

	/**
	 * Sets if this npc retreats or not when their health is low.
	 * 
	 * @param retreats
	 *            the retreats to set.
	 */
	public void setRetreats(boolean retreats) {
		this.retreats = retreats;
	}

	/**
	 * Gets if this npc is poisonous.
	 * 
	 * @return the poisonous.
	 */
	public boolean isPoisonous() {
		return poisonous;
	}

	/**
	 * Sets if this npc is poisonous.
	 * 
	 * @param poisonous
	 *            the poisonous to set.
	 */
	public void setPoisonous(boolean poisonous) {
		this.poisonous = poisonous;
	}

	/**
	 * Gets the respawn time for this npc.
	 * 
	 * @return the respawn time.
	 */
	public int getRespawnTime() {
		return respawn;
	}

	/**
	 * Sets the respawn time for this npc.
	 * 
	 * @param respawnTime
	 *            the respawn time to set.
	 */
	public void setRespawnTime(int respawn) {
		this.respawn = respawn;
	}

	/**
	 * Gets the maximum damage this npc is allowed to deal.
	 * 
	 * @return the max hit.
	 */
	public int getMaxHit() {
		return maxHit;
	}

	/**
	 * Sets the maximum damage this npc is allowed to deal.
	 * 
	 * @param maxHit
	 *            the max hit to set.
	 */
	public void setMaxHit(int maxHit) {
		this.maxHit = maxHit;
	}

	/**
	 * Gets the amount of hitpoints this npc has.
	 * 
	 * @return the hitpoints.
	 */
	public int getHitpoints() {
		return hitpoints;
	}

	/**
	 * Sets the amount of hitpoints this npc has.
	 * 
	 * @param hitpoints
	 *            the hitpoints to set.
	 */
	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}

	/**
	 * Gets how fast this npc can attack.
	 * 
	 * @return the attack speed.
	 */
	public int getAttackSpeed() {
		return attackSpeed;
	}

	/**
	 * Sets how fast this npc can attack.
	 * 
	 * @param attackSpeed
	 *            the attack speed to set.
	 */
	public void setAttackSpeed(int attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	/**
	 * Gets the attacking animation for this npc.
	 * 
	 * @return the attack animation.
	 */
	public int getAttackAnimation() {
		return attackAnim;
	}

	/**
	 * Sets the attacking animation for this npc.
	 * 
	 * @param attackAnimation
	 *            the attack animation to set.
	 */
	public void setAttackAnimation(int attackAnim) {
		this.attackAnim = attackAnim;
	}

	/**
	 * Gets the defending animation for this npc.
	 * 
	 * @return the defence animation.
	 */
	public int getDefenceAnimation() {
		return defenceAnim;
	}

	/**
	 * Sets the defending animation for this npc.
	 * 
	 * @param defenceAnimation
	 *            the defence animation to set.
	 */
	public void setDefenceAnimation(int defenceAnim) {
		this.defenceAnim = defenceAnim;
	}

	/**
	 * Gets the death animation for this npc.
	 * 
	 * @return the death animation.
	 */
	public int getDeathAnimation() {
		return deathAnim;
	}

	/**
	 * Sets the death animation for this npc.
	 * 
	 * @param deathAnimation
	 *            the death animation to set.
	 */
	public void setDeathAnimation(int deathAnim) {
		this.deathAnim = deathAnim;
	}

	/**
	 * Gets the attack bonus.
	 * 
	 * @return the attack bonus.
	 */
	public int getAttackBonus() {
		return attackBonus;
	}

	/**
	 * Sets the attack bonus.
	 * 
	 * @param attackBonus
	 *            the attack bonus to set.
	 */
	public void setAttackBonus(int attackBonus) {
		this.attackBonus = attackBonus;
	}

	/**
	 * Gets the melee resistance.
	 * 
	 * @return the defence melee.
	 */
	public int getDefenceMelee() {
		return defenceMelee;
	}

	/**
	 * Sets the melee resistance.
	 * 
	 * @param defenceMelee
	 *            the defence melee to set.
	 */
	public void setDefenceMelee(int defenceMelee) {
		this.defenceMelee = defenceMelee;
	}

	/**
	 * Gets the range resistance.
	 * 
	 * @return the defence range.
	 */
	public int getDefenceRange() {
		return defenceRange;
	}

	/**
	 * Sets the range resistance.
	 * 
	 * @param defenceRange
	 *            the defence range to set.
	 */
	public void setDefenceRange(int defenceRange) {
		this.defenceRange = defenceRange;
	}

	/**
	 * Gets the magic resistance.
	 * 
	 * @return the defence mage.
	 */
	public int getDefenceMage() {
		return defenceMage;
	}

	/**
	 * Sets the magic resistance.
	 * 
	 * @param defenceMage
	 *            the defence mage to set.
	 */
	public void setDefenceMage(int defenceMage) {
		this.defenceMage = defenceMage;
	}

	/**
	 * This npc's slayer level required to attack
	 * 
	 * @return required slayer level to attack npc
	 */
	public int getSlayerLevel() {
		return slayerLevel;
	}

	public void setSlayerLevel(int slayerLevel) {
		this.slayerLevel = slayerLevel;
	}

    public boolean isCollectionLogNpc() {
        return this.isCollectionLogNpc;
    }

    public void setCollectionLogNpc(boolean isCollectionLogNpc) {
        this.isCollectionLogNpc = isCollectionLogNpc;
    }
}
