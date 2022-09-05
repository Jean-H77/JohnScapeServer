package com.ruse.world.content.skill.slayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.ruse.model.Npcs;
import com.ruse.model.Position;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;

/**
 * @author Gabriel Hannason 
 */

public enum SlayerTasks {

	NO_TASK(null, null, -1, null, -1),

	/**
	 * Easy tasks
	 																			SlayerMaster taskMaster, int npcId, String npcLocation, int XP, Position taskPosition
	 */
	GOBLIN(SlayerMaster.VANNAKA, "Goblins are found in the Training Teleports.", 19, new Position(3246, 3245, 0), 4275),
	ROCK_CRAB(SlayerMaster.VANNAKA, "Rock Crabs can be found in the Training Teleport.", 21, new Position(2709, 3715, 0), 1265),
	EXPERIMENT(SlayerMaster.VANNAKA, "Experiments can be found in the Training Teleport.", 22, new Position(3564, 9954, 0), 1677),
	GIANT_BAT(SlayerMaster.VANNAKA, "Giant Bats can be found in Taverly Dungeon.", 20, new Position(2907, 9833, 0), 78),
	CHAOS_DRUID(SlayerMaster.VANNAKA, "Chaos Druids can be found in Edgeville Dungeon.", 22, new Position(3109, 9931, 0), 181),
	ZOMBIE(SlayerMaster.VANNAKA, "Zombies can be found in Edgeville Dungeon.", 20, new Position(3144, 9903, 0), 76),
	HOBGOBLIN(SlayerMaster.VANNAKA, "Hobgoblins can be found in Edgeville Dungeon.", 45, new Position(3123, 9876, 0), 2686),
	HILL_GIANT(SlayerMaster.VANNAKA, "Hill Giants can be found in Edgeville Dungeon.", 47, new Position(3120, 9844, 0), 117),
	DEADLY_RED_SPIDER(SlayerMaster.VANNAKA, "Deadly Red Spiders can be found in Edgeville Dungeon.", 45, new Position(3083, 9940, 0), 63),
	BABY_BLUE_DRAGON(SlayerMaster.VANNAKA, "Baby Blue Dragons can be found in Taverly Dungeon.", 50, new Position(2891, 9772, 0), 52),
	SKELETON(SlayerMaster.VANNAKA, "Skeletons can be found in Edgeville Dungeon.", 22, new Position(3094, 9896, 0), 90),
	EARTH_WARRIOR(SlayerMaster.VANNAKA, "Earth Warriors can be found in Edgeville Dungeon.", 54, new Position(3124, 9986, 0), 124),
	YAK(SlayerMaster.VANNAKA, "Yaks can be found in the Training Teleport.", 25, new Position(3203, 3267, 0), 5529),
	GHOUL(SlayerMaster.VANNAKA, "Ghouls can be found in the Training Teleport.", 48, new Position(3418, 3508, 0), 1218),
	MONK_OF_ZAMORAK(SlayerMaster.VANNAKA, "Monks of Zamorak are North-West in the Chaos Tunnels.", 45, new Position(3151, 5489, 0), 190),
	BANSHEE(SlayerMaster.VANNAKA, "Banshee can be found on the first floor of the Slayer Tower.", 45, new Position(3441, 3545, 0), 1612),
	CRAWLING_HAND(SlayerMaster.VANNAKA, "Crawling hands are found at the Entrance of Slayer Tower.", 45, new Position(3418, 3544, 0), 1652),
	/**
	 * Medium tasks
	 */
	BANDIT(SlayerMaster.DURADEL, "Bandits can be found in the Training teleport.", 65, new Position(3172, 2976, 0), 1880),
	WILD_DOG(SlayerMaster.DURADEL, "Wild Dogs can be found in Brimhaven Dungeon.", 67, new Position(2680, 9557, 0), 1593),
	MOSS_GIANT(SlayerMaster.DURADEL, "Moss Giants can be found in Brimhaven Dungeon.", 66, new Position(2676, 9549, 0), 112),
	FIRE_GIANT(SlayerMaster.DURADEL, "Fire Giants can be found in Brimhaven Dungeon.", 69, new Position(2664, 9480, 0), 110),
	GREEN_DRAGON(SlayerMaster.DURADEL, "Green Dragons can be found in western Wilderness.", 75, new Position(2977, 3615, 0), 941),
	BLUE_DRAGON(SlayerMaster.DURADEL, "Blue Dragons can be found in Taverly Dungeon.", 80, new Position(2892, 9799, 0), 55),
	HELLHOUND(SlayerMaster.DURADEL, "Hellhounds can be found in Taverly Dungeon.", 80, new Position(2870, 9848, 0), 49),
	//BLACK_DEMON(SlayerMaster.DURADEL, "Black Demons can be found in Edgeville Dungeon.", 83, new Position(3089, 9967, 0), 84),
	BLOODVELD(SlayerMaster.DURADEL, "Bloodvelds can be found in Slayer Tower.", 72, new Position(3418, 3570, 1), 1618),
	INFERNAL_MAGE(SlayerMaster.DURADEL, "Infernal Mages can be found in Slayer Tower.", 70, new Position(3445, 3579, 1), 1643),
	ABERRANT_SPECTRE(SlayerMaster.DURADEL, "Aberrant Spectres can be found in Slayer Tower.", 73, new Position(3432, 3553, 1), 1604),
	NECHRYAEL(SlayerMaster.DURADEL, "Nechryaels can be found in Slayer Tower.", 78, new Position(3448, 3564, 2), 1613),
	GARGOYLE(SlayerMaster.DURADEL, "Gargoyles can be found in Slayer Tower.", 81, new Position(3438, 3534, 2), 1610),
	TZHAAR_XIL(SlayerMaster.DURADEL, "TzHaar-Xils can be found in Tzhaar City.", 90, new Position(2445, 5147, 0), 2605),
	TZHAAR_HUR(SlayerMaster.DURADEL, "TzHaar-Hurs can be found in Tzhaar City.", 79, new Position(2456, 5135, 0), 2600),
	ORK(SlayerMaster.DURADEL, "Orks can be found in the Godwars Dungeon.", 76, new Position(2867, 5322, 2), 6273),
	ARMOURED_ZOMBIE(SlayerMaster.DURADEL, "Armoured Zombies can be found in the Training Teleport.", 80, new Position(3085, 9672, 0), 8162),
	DUST_DEVIL(SlayerMaster.DURADEL, "Dust Devils can be found in the Training Teleport.", 95, new Position(3279, 2964, 0), 1624),
	JUNGLE_STRYKEWYRM(SlayerMaster.DURADEL, "Strykewyrms can be found in the Strykewyrm Cavern.", 109, new Position(2731, 5095, 0), 9467),
	DESERT_STRYKEWYRM(SlayerMaster.DURADEL, "Strykewyrms can be found in the Strykewyrm Cavern.", 112, new Position(2731, 5095, 0), 9465),
	
	/**
	 * Hard tasks
	 */
	DARK_BEAST(SlayerMaster.KURADEL, "Dark Beasts can be found in the Chaos Tunnels, via Dungeon teleport.", 160, new Position(3168, 5463, 0), 2783),
	MONKEY_GUARD(SlayerMaster.KURADEL, "Monkey Guards can be found in the Training Teleport.", 140, new Position(2795, 2775, 0), 1459),
	WATERFIEND(SlayerMaster.KURADEL, "Waterfiends can be found in the Ancient Cavern.", 134, new Position(1737, 5353, 0), 5361),
	ICE_STRYKEWYRM(SlayerMaster.KURADEL, "Strykewyrms can be found in the Strykewyrm Cavern.", 138, new Position(2731, 5095, 0), 9463),
	STEEL_DRAGON(SlayerMaster.KURADEL, "Steel dragons can be found in Brimhaven Dungeon.", 156, new Position(2710, 9441, 0), 1592),
	MITHRIL_DRAGON(SlayerMaster.KURADEL, "Mithril Dragons can be found in the Ancient Cavern.", 160, new Position(1761, 5329, 1), 5363),
	GREEN_BRUTAL_DRAGON(SlayerMaster.KURADEL, "Green Brutal Dragons can be found in the Ancient Cavern.", 155, new Position(1767, 5340, 0), 5362),
	SKELETON_WARLORD(SlayerMaster.KURADEL, "Skeleton Warlords can be found in the Ancient Cavern.", 144, new Position(1763, 5358, 0), 6105),
	SKELETON_BRUTE(SlayerMaster.KURADEL, "Skeleton Brutes can be found in the Ancient Cavern.", 144, new Position(1788, 5335, 0), 6104),
	AVIANSIE(SlayerMaster.KURADEL, "Aviansies can be found in the Godwars Dungeon.", 146, new Position(2868, 5268, 2), 6246),
	FROST_DRAGON(SlayerMaster.KURADEL, "Frost Dragons can be found in the deepest of Wilderness.", 225, new Position(2968, 3902, 0), 51),
	ANGRY_BARBARIAN_SPIRIT(SlayerMaster.KURADEL, "Angry Barbarian Spirits are found in the Ancient Cavern.", 155, new Position(1749, 5337, 0), 749),
	BRUTAL_GREEN_DRAGON(SlayerMaster.KURADEL, "Brutal Green Drags are found in the Ancient Cavern.", 210, new Position(1762, 5323, 0), 5362),
	LOST_BARBARIAN(SlayerMaster.KURADEL, "Angry Barbarians are found in the Ancient Cavern.", 137, new Position(1765, 5344, 0), 6102),
	ABYSSAL_DEMON(SlayerMaster.KURADEL, "Abyssal Demons are on the top floor of the Slayer Tower.", 137, new Position(3420, 3567, 2), 1615),
	BLACK_DEMON(SlayerMaster.DURADEL, "Black Demons can be found in Edgeville Dungeon.", 83, new Position(3089, 9967, 0), 84, Npcs.DEMONIC_GORILLA),

	/**
	 * Elite
	 */
	NEX(SlayerMaster.SUMONA, "Nex can be found in the Godwars Dungeon.", 1000, new Position(2903, 5203), 13447),
	GENERAL_GRAARDOR(SlayerMaster.SUMONA, "General Graardor can be found in the Godwars Dungeon.", 680, new Position(2863, 5354, 2), 6260),
	TORMENTED_DEMON(SlayerMaster.SUMONA, "Tormented Demons can be found using the Boss teleport.", 400, new Position(2717, 9805, 0), 8349),
	KING_BLACK_DRAGON(SlayerMaster.SUMONA, "The King Black Dragon can be found using the Boss teleport.", 260, new Position(2273, 4681, 0), 50),
	DAGANNOTH_SUPREME(SlayerMaster.SUMONA, "The Dagannoth Kings can be found using the Boss teleport.", 260, new Position(1908, 4367, 0), 2881),
	DAGANNOTH_REX(SlayerMaster.SUMONA, "The Dagannoth Kings can be found using the Boss teleport.", 260, new Position(1908, 4367, 0), 2883),
	DAGANNOTH_PRIME(SlayerMaster.SUMONA, "The Dagannoth Kings can be found using the Boss teleport.", 260, new Position(1908, 4367, 0), 2882),
	CHAOS_ELEMENTAL(SlayerMaster.SUMONA, "The Chaos Elemental can be found using the Boss teleport.", 580, new Position(3285, 3921, 0), 3200),
	SLASH_BASH(SlayerMaster.SUMONA, "Slash Bash can be found using the Boss teleport.", 280, new Position(2547, 9448, 0), 2060),
	KALPHITE_QUEEN(SlayerMaster.SUMONA, "The Kalphite Queen can be found using the Boss teleport.", 310, new Position(3476, 9502, 0), 1160),
	PHOENIX(SlayerMaster.SUMONA, "The Phoenix can be found using the Boss teleport.", 210, new Position(2839, 9557, 0), 8549),
	CORPOREAL_BEAST(SlayerMaster.SUMONA, "The Corporeal Beast can be found using the Boss teleport.", 800, new Position(2885, 4375, 0), 8133),
	//BANDOS_AVATAR(SlayerMaster.SUMONA, 4540, "The Bandos Avatar can be found using the Boss teleport.", 34000, new Position(2891, 4767)),
	CALLISTO(SlayerMaster.SUMONA, "Callisto can be found using the Boss teleport.", 575, new Position(3163, 3796, 0), 2009),
	VETION(SlayerMaster.SUMONA, "Vet'ion can be found using the Boss teleport.", 615, new Position(3009, 3767, 0), 2006),
	VENENATIS(SlayerMaster.SUMONA, "Venenatis can be found using the Boss teleport.", 592, new Position(3005, 3732, 0), 2000),
	SCORPIA(SlayerMaster.SUMONA, "Scorpia can be found using the Boss teleport.", 605, new Position(2849, 9640, 0), 2001),
	ZULRAH(SlayerMaster.SUMONA, "Zulrah can be found in Oldschool Boss teleports.", 605, new Position(3406, 2794, 0), 2042) //hax for 2042, 2044, 2043 ||
	;

	SlayerTasks(SlayerMaster taskMaster, String npcLocation, int XP, Position taskPosition, int...npcIds) {
		this.taskMaster = taskMaster;
		this.npcLocation = npcLocation;
		this.XP = XP;
		this.taskPosition = taskPosition;
		this.npcIds = Lists.newArrayList();
		Arrays.stream(npcIds).forEach(id -> this.npcIds.add(id));
		this.npcIds = Collections.unmodifiableList(this.npcIds);
	}

	private SlayerMaster taskMaster;
	private List<Integer> npcIds;
	private String npcLocation;
	private int XP;
	private Position taskPosition;

	public SlayerMaster getTaskMaster() {
		return this.taskMaster;
	}

	public List<Integer> getNpcIds() {
		return this.npcIds;
	}

	public String getNpcLocation() {
		return this.npcLocation;
	}

	public int getXP() {
		return this.XP;
	}

	public Position getTaskPosition() {
		return this.taskPosition;
	}

	public int getSlayerLevelForAssignment() {
		int min = 0;
		for (int npc : npcIds) {
			NpcDefinition definition = NpcDefinition.forId(npc);
			if (definition != null && definition.getSlayerLevel() > min) {
				min = definition.getSlayerLevel();
			}
		}
		return min;
	}

	public static SlayerTasks forId(int id) {
		for (SlayerTasks tasks : SlayerTasks.values()) {
			if (tasks.ordinal() == id) {
				return tasks;
			}
		}
		return null;
	}

	public static int[] getNewTaskData(SlayerMaster master) {
		int slayerTaskId = 1, slayerTaskAmount = 20;
		int easyTasks = 0, mediumTasks = 0, hardTasks = 0, eliteTasks = 0;

		/*
		 * Calculating amount of tasks
		 */
		for(SlayerTasks task: SlayerTasks.values()) {
			if(task.getTaskMaster() == SlayerMaster.VANNAKA)
				easyTasks++;
			else if(task.getTaskMaster() == SlayerMaster.DURADEL) 
				mediumTasks++;
			else if(task.getTaskMaster() == SlayerMaster.KURADEL) 
				hardTasks++;
			else if(task.getTaskMaster() == SlayerMaster.SUMONA)
				eliteTasks++;
		}

		/*
		 * Getting a task
		 */
		if(master == SlayerMaster.VANNAKA) {
			slayerTaskId = 1 + Misc.getRandom(easyTasks);
			if(slayerTaskId > easyTasks)
				slayerTaskId = easyTasks;
			slayerTaskAmount = 15 + Misc.getRandom(15);
		} else if(master == SlayerMaster.DURADEL) {
			slayerTaskId = easyTasks - 1 + Misc.getRandom(mediumTasks);
			slayerTaskAmount = 12 + Misc.getRandom(13);
		} else if(master == SlayerMaster.KURADEL) {
			slayerTaskId = 1 + easyTasks + mediumTasks + Misc.getRandom(hardTasks - 1);
			slayerTaskAmount = 10 + Misc.getRandom(15);
		} else if(master == SlayerMaster.SUMONA) {
			slayerTaskId = 1 + easyTasks + mediumTasks + hardTasks + Misc.getRandom(eliteTasks - 1);
			slayerTaskAmount = 2 + Misc.getRandom(7);
		}
		return new int[] {slayerTaskId, slayerTaskAmount};
	}
}
