package com.ruse.world.content;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ruse.util.Misc;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportLocations;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.model.entity.character.player.Player;

public class TeleportInterface {

	public static int[] TELEPORT_STRING_IDS = {44202, 44402, 44602, 44802, 45002, 45202};
	public static int[] TELEPORT_BUTTON_IDS = {-21434, -21234, -21034, -20834, -20634, -20434};
	
	public static int INTERFACE_ID = 44000, 
			DESCRIPTION_TITLE = 44086,
			TITLE = 44005,
			CATEGORY_ID = 44072,
			NEW_CATEGORY_ID = 44048,
			TELEPORTBUTTON = 44096,
			PREV_BUTTON = 44089;

	public static int DESCRIPTION[] = { 44087, 44088 };
	public static int PREVIOUS[] = { 44090, 44091, 44092, 44098 };
	
	public static boolean handleButton(int id) {
		if (id >= -21534 && id <= -20428) {
			return true;
		}
		return false;
	}

	public static void open(Player player) {
		resetInterface(player);
		player.getPacketSender().sendInterface(INTERFACE_ID);
	}
	public static void resetInterface(Player player) {
		if (player.getTeleportInterfaceData() != null) {
			player.setTeleportInterfaceData(null);
		}
		player.getPacketSender().sendString(TITLE, "JohnScape Teleports");
		player.getPacketSender().sendString(TELEPORTBUTTON, "Teleport");
		for (int i = 0; i < Category.values().length; i++) {
			player.getPacketSender().sendString(NEW_CATEGORY_ID+i, Misc.formatPlayerName(Category.values()[i].toString()));
		}
		player.getPacketSender().sendString(DESCRIPTION_TITLE, "Select a teleport!");
		player.getPacketSender().sendString(DESCRIPTION[0], "Choose a category from the left.");
		player.getPacketSender().sendString(DESCRIPTION[1], "Then an option from the right.");
		player.getPacketSender().sendString(PREV_BUTTON, "History");
		populatePrevious(player);
		
		for (int i = 0; i < TeleportInterfaceData.values().length; i++) {
			player.getPacketSender().sendString(TeleportInterfaceData.values()[i].getStringId(), TeleportInterfaceData.values()[i].getDescriptionTitle());
		}
	}
	
	public static void populatePrevious(Player player) {
		for (int i = 0; i < player.getPreviousTeleports().length; i++) {
			if (player.getPreviousTeleportsIndex(i) < 0 && TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(i)) != null) {
				String title = TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(i)).getDescriptionTitle();
				if (title.length() > 13) {
					title = title.substring(0, 13) + "..";
				}
				player.getPacketSender().sendString(PREVIOUS[i], title);
			} else {
				player.getPacketSender().sendString(PREVIOUS[i], "");
			}
		}
	}
	
	public static void updatePrevious(Player player, int newest) {		
		boolean duplicate = false;
		int[] orig = player.getPreviousTeleports();
		
		for (int i = 0; i < orig.length; i++) {
			if (orig[i] == newest) {
				duplicate = true;
				break;
			}
		}
		
		if (duplicate) {
			ArrayList<Integer> list = new ArrayList<Integer>();
		//	list.add(newest);
		//	list.add(player.getPreviousTeleportsIndex(0));
			//list.add(player.getPreviousTeleportsIndex(1));
			//list.add(player.getPreviousTeleportsIndex(2));
		//	list.add(player.getPreviousTeleportsIndex(3));
			Set<Integer> hs = new LinkedHashSet<>();
			hs.addAll(list);
			list.clear();
			list.addAll(hs);
			int[] arr = list.stream().filter(i -> i != null).mapToInt(i -> i).toArray();
			player.setPreviousTeleports(arr);
			
		} else {
			int[] newstuff = { /*newest, player.getPreviousTeleportsIndex(0), player.getPreviousTeleportsIndex(1),
					player.getPreviousTeleportsIndex(2)*/ };
		//	player.setPreviousTeleports(newstuff);
		}
		
		//populatePrevious(player);
	}
	
	public static void updateDescription(Player player, TeleportInterfaceData data) {
		player.getPacketSender().sendString(DESCRIPTION_TITLE, data.getDescriptionTitle());
		player.getPacketSender().sendString(DESCRIPTION[0], data.getDescriptionText1());
		player.getPacketSender().sendString(DESCRIPTION[1], "");
	}
	
	public static void handleButtonClick(Player player, int button) {
		switch (button) {
		case -21534: //exit button
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case -21443: //teleport button
			if (player.getTeleportInterfaceData() == null) {
				player.getPacketSender().sendMessage("Please select a teleport destination first.");
				return;
			} else {
				TeleportHandler.teleportPlayer(player, player.getTeleportInterfaceData().getDestination().getPos(), TeleportType.NORMAL);
				if (player.getTeleportInterfaceData().getDestination().getHint() != null) {
					String txt = player.getTeleportInterfaceData().getDestination().getHint();
					if (!txt.equalsIgnoreCase("") && !txt.equalsIgnoreCase(" ") && txt.length() > 0) {
						player.getPacketSender().sendMessage(txt);
					}
				}
				//updatePrevious(player, player.getTeleportInterfaceData().getButtonId());
			}
			break;
		case -21446: //previous #1
			if (player.getPreviousTeleportsIndex(0) < 0) {
				player.setTeleportInterfaceData(TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(0)));
				updateDescription(player, TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(0)));
			}
			break;
		case -21445: // previous #2
			if (player.getPreviousTeleportsIndex(1) < 0) {
				player.setTeleportInterfaceData(TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(1)));
				updateDescription(player, TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(1)));
			}
			break;
		case -21444: // previous #3
			if (player.getPreviousTeleportsIndex(2) < 0) {
				player.setTeleportInterfaceData(TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(2)));
				updateDescription(player, TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(2)));
			}
			break;
		case -21438: // previous #4
			if (player.getPreviousTeleportsIndex(3) < 0) {
				player.setTeleportInterfaceData(TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(3)));
				updateDescription(player, TeleportInterfaceData.forButtonId(player.getPreviousTeleportsIndex(3)));
			}
			break;
		}
		for (int i = 0; i < TeleportInterfaceData.values().length; i++) {
			if (TeleportInterfaceData.values()[i].getButtonId() == button) {
				updateDescription(player, TeleportInterfaceData.forButtonId(button));
				player.setTeleportInterfaceData(TeleportInterfaceData.values()[i]);
				break;
			} 
			
		}
	}

	private enum Category {
		CITIES,
		MONSTERS,
		DUNGEONS,
		BOSSES,
		MINIGAMES,
		WILDERNESS
	}

	public enum TeleportInterfaceData {
		/* Cities */
		EDGEVILLE("Edgeville", "A small town at the edge of the Wilderness.\\nA popular spot of PKers to gather.", TeleportLocations.EDGEVILLE, Category.CITIES),
		AL_KHARID("Al Kharid", "A northern city of the Kharidian Desert.\\nRuled by the Emir.", TeleportLocations.AL_KHARID, Category.CITIES),
		ARDOUGNE("Ardougne", "The capital of the Kingdom of JohnScape.\\nThe West is infected with a plague.", TeleportLocations.ARDOUGNE, Category.CITIES),
		CAMELOT("Camelot", "A giant castle, and home of King Arthur.\\nJust a stone's throw east of Seers' Village.", TeleportLocations.CAMELOT, Category.CITIES),
		CANIFIS("Canifis", "A small town in Morytania, east of Varrock.\\nThe town is inhabited by werewolves!", TeleportLocations.CANIFIS, Category.CITIES),
		DRAYNOR("Draynor", "A village between Falador and Lumbridge.\\nPart of the Kingdom of Misthalin.", TeleportLocations.DRAYNOR, Category.CITIES),
		FALADOR("Falador", "The capital city of Asgarnia.\\nHome of the white knights.", TeleportLocations.FALADOR, Category.CITIES),
		VARROCK("Varrock", "The large capital city of Misthalin.\\nHome of many heros.", TeleportLocations.VARROCK, Category.CITIES),
		KARAMJA("Karamja", "The largest island in Gielinor.\\nPopulated with all sorts of creatures.", TeleportLocations.KARAMJA, Category.CITIES),
		LUMBRIDGE("Lumbridge", "Known for a bridge crossing the River Lum.\\nPopular among low levels.", TeleportLocations.LUMBRIDGE, Category.CITIES),
		YANILLE("Yanille", "Lies to the south of Ardougne.\\nHeavily defended by guards.", TeleportLocations.YANILLE, Category.CITIES),
		GRAND_EXCHANGE("Grand Exchange", "A popular trade spot in Varrock.\\nThe central bank for player items.", TeleportLocations.TRADE, Category.CITIES),
		CHILL("Chill", "An icy mountain away from most folk.\\nA hearty place to relax.", TeleportLocations.CHILL, Category.CITIES),

		/* Monsters */
		BANISHED_FOUR("The Banished", "Home to the banished four.", TeleportLocations.BANISHED_FOUR, Category.MONSTERS),
		TORMENTED_DEMONS("Tormented Demons", "Level: 25\\n@gre@Non-wilderness.", TeleportLocations.TORMENTED_DEMONS, Category.MONSTERS),
		YAKS("Yaks", "Level: 22\\n@gre@Non-wilderness.", TeleportLocations.YAKS, Category.MONSTERS),
		BANDITS("Bandits", "Level: 56\\n@gre@Non-wilderness.", TeleportLocations.BANDITS, Category.MONSTERS),
		GHOULS("Ghouls", "Level: 42\\n@gre@Non-wilderness.", TeleportLocations.GHOULS, Category.MONSTERS),
		CHAOS_DRUIDS("Chaos Druids", "Level: 13\\n@gre@Non-wilderness.", TeleportLocations.CHAOS_DRUIDS, Category.MONSTERS),
		GOBLINS("Goblins", "Level: 2\\n@gre@Non-wilderness.", TeleportLocations.GOBLINS, Category.MONSTERS),
		DUST_DEVILS("Dust Devils", "Level: 93\\n@gre@Non-wilderness.", TeleportLocations.DUST_DEVILS, Category.MONSTERS),
		CHICKENS("Chickens", "Level: 1\\n@gre@Non-wilderness.", TeleportLocations.CHICKENS, Category.MONSTERS),
		MONKEY_SKELETONS("Monkey Skeletons", "Level: 142\\n@gre@Non-wilderness.", TeleportLocations.MONKEY_SKELETONS, Category.MONSTERS),
		MONKEY_GUARDS("Monkey Guards", "Level: 167\\n@gre@Non-wilderness.", TeleportLocations.MONKEY_GUARDS, Category.MONSTERS),
		ARMOURED_ZOMBIES("Armoured Zombies", "Level: 85\\n@gre@Non-wilderness.", TeleportLocations.ARMOURED_ZOMBIES, Category.MONSTERS),
		DEMONIC_GORILLAS("Demonic Gorillas", "Level: 275\\n@gre@Non-wilderness.", TeleportLocations.DEMONIC_GORILLAS, Category.MONSTERS),

		/* Dungeons */
		EDGE_DUNG("Edgeville Dungeon", "A low level dungeon.\\nHolds a few slayer assignments.", TeleportLocations.EDGE_DUNGEON, Category.DUNGEONS),
		SLAYER_TOWER("Slayer Tower", "A slayer specific area.\\nFilled with low to high level monsters.", TeleportLocations.SLAYER_TOWER, Category.DUNGEONS),
		BRIMHAVEN_DUNG("Brimhaven Dungeon", "A medium to high level dungeon.\\nHas a lot of monsters and tasks.", TeleportLocations.BRIMHAVEN_DUNGEON, Category.DUNGEONS),
		TAVERLY_DUNG("Taverly Dungeon", "A low to medium level dungeon.\\nHas a few slayer tasks.", TeleportLocations.TAVERLY_DUNGEON, Category.DUNGEONS),
		GODWARS_DUNG("God wars Dungeon", "A high level dungeon.\\nFull of godly incarnations.", TeleportLocations.GODWARS_DUNGEON, Category.DUNGEONS),
		STRYKEWYRM_CAVERN("Strykewyrm Cavern", "A high level dungeon.\\nOnly contains strykewyrms.", TeleportLocations.STRYKEWYRM_CAVERN, Category.DUNGEONS),
		ANCIENT_CAVERN("Ancient Cavern", "A high level dungeon.\\nHolds ancient barbarian tasks.", TeleportLocations.ANCIENT_CAVERN, Category.DUNGEONS),
		CHAOS_TUNNELS("Chaos Tunnels", "A low to high level dungeon.\\n(Pending completion) Has a few tasks.", TeleportLocations.CHAOS_TUNNELS, Category.DUNGEONS),

		/* Bosses */
		SISTER_SENGA("Sister Senga", "Home to the mad apothecary.", TeleportLocations.SISTER_SENGA, Category.BOSSES),
		INFERNAL_PYRELORD("Infernal Pyrelord", "Home to the Infernal Pyrelord", TeleportLocations.INFERNAL_PYRELORD, Category.BOSSES),

		/*GODWARS("God wars", "Home to each god's champion.\\nLots of unique drops available.", TeleportLocations.GWD, Category.BOSSES),
		DAGANNOTH_KINGS("Dagannoth Kings", "The Dagannoth lair, containing the kings.\\nMedium to high level content.", TeleportLocations.DAGKINGS, Category.BOSSES),
		FROST_DRAGONS("Frost Dragons", "Popular for their frostdragon bones.\\n@red@Warning: Level 46 Wilderness!", TeleportLocations.FROSTDRAGONSWILDY, Category.BOSSES),
		TORMENTED_DEMONS("Tormented Demons", "Have a chance to drop Dragon claws.\\nAlso have a chance to kill you.", TeleportLocations.TORMENTEDDEMONS, Category.BOSSES),
		KING_BLACK_DRAGON("King Black Dragon", "The King of black dragons.\\n@gre@Non-wilderness.", TeleportLocations.KBD, Category.BOSSES),
		CHAOS_ELEMENTAL("Chaos Elemental", "An unnerving entity.\\n@red@Warning: Level 50 Wilderness!", TeleportLocations.CHAOSELE, Category.BOSSES),
		SLASHBASH("Slash Bash", "An angry ogre in his cave.\\nHis flesh is rotting off!", TeleportLocations.SLASHBASH, Category.BOSSES),
		KALPHITE_QUEEN("Kalphite Queen", "Queen of the Kalphites.\\nThis boss has multiple forms!", TeleportLocations.KQ, Category.BOSSES),
		PHOENIX("Phoenix", "The flying infero.\\nBeatiful and deadly.", TeleportLocations.PHOENIX, Category.BOSSES),
		BANDOS_AVATAR("Bandos Avatar", "A avatar created by the power of Bandos.\\nIncredibly lethal.", TeleportLocations.BANDOSAVATAR, Category.BOSSES),
		GLACORS("Glacors", "Frozen shells of their former selves.\\nCoveted for their boot drops.", TeleportLocations.GLACORS, Category.BOSSES),
		CORPERAL_BEAST("Corporeal Beast", "Guardian of the spirit shields.\\nVery strong, be careful!", TeleportLocations.CORPBEAST, Category.BOSSES),
		NEX("Nex", "An ancient creature and Zarosian.\\nDrops ancient armor.", TeleportLocations.NEX, Category.BOSSES),
		CALLISTO("Callisto", "A chaotic bear.\\n@red@Warning: Level 35 Wilderness.", TeleportLocations.CALLISTO, Category.BOSSES),
		VET_ION("Vet'ion", "An old, demented soul.\\n@red@Warning: Level 31 Wilderness.", TeleportLocations.VETION, Category.BOSSES),
		VENENATIS("Venenatis", "A terrible spider.\\nWarning: Level 27 Wilderness.", TeleportLocations.VENENATIS, Category.BOSSES),
		ZULRAH("Zulrah", "Our custom take on Zulrah. Be careful!\\nKeep items on death.", TeleportLocations.ZULRAH, Category.BOSSES),
		KRAKEN("Kraken", "A monster of the sea.\\n@gre@Non-wilderness.", TeleportLocations.KRAKEN, Category.BOSSES),
		SCORPIA("Scorpia", "Queen of the scorpions.\\n@gre@Non-wilderness.", TeleportLocations.SCORPIA, Category.BOSSES),*/

		/* Minigames */
		DUEL_ARENA("Duel Arena", "One will leave with supreme wealth.\\nThe other will leave with nothing.", TeleportLocations.DUELARENA, Category.MINIGAMES),
		BARROWS("Barrows", "Home of the ancient barrows brothers.\\n@red@Warning: RoT6 is LOSE ITEMS ON DEATH.", TeleportLocations.BARROWS, Category.MINIGAMES),
		PEST_CONTROL("Pest Control", "A defense outpost of the Void knights.\\nRewards powerful gear.", TeleportLocations.PESTCONTROL, Category.MINIGAMES),
		WARRIOR_GUILD("Warrior's Guild", "Home of great warriors.\\nA place to gather defenders.", TeleportLocations.WARRIORSGUILD, Category.MINIGAMES),
		FIGHT_CAVE("Fight Caves", "Prove yourself against Jad.\\nOnly one (Jad) wave.", TeleportLocations.FIGHTCAVE, Category.MINIGAMES),
		FIGHT_PIT("Fight Pits", "PvP against others, free-style.\\nNo rewards.", TeleportLocations.FIGHTPIT, Category.MINIGAMES),
		GRAVEYARD("Graveyard", "Take on the zombie masses.\\nMid level rewards.", TeleportLocations.GRAVEYARD, Category.MINIGAMES),

		/* Wilderness */
		EDGE_DITCH("Edgeville Ditch @gre@(Safe)", "To the edge of the wilderness.\\n@gre@Safe teleport.", TeleportLocations.EDGEVILLEDITCH, Category.WILDERNESS),
		MAGE_BANK("Mage Bank @gre@(Safe)", "Home to some of the most chaotic mages.\\n@gre@Safe teleport.", TeleportLocations.MAGEBANK_SAFE, Category.WILDERNESS),
		GHOST_TOWN("Ghost Town @gre@(Safe)", "A town of revenants in deep wilderness.\\n@gre@Safe teleport.", TeleportLocations.GHOSTTOWN, Category.WILDERNESS),
		WEST_DRAGONS("West Dragons @red@(Level 10)", "Popular for PvM and PvPers.\\n@red@Warning: Level 10 Wilderness!", TeleportLocations.EDGEWESTDRAGONS, Category.WILDERNESS),
		CHAOS_ALTAR("Chaos Altar @red@(Level 13)", "A chaotic altar surrounded by lava.\\n@red@Warning: Level 13 Wilderness!", TeleportLocations.CHAOSALTAR, Category.WILDERNESS),
		EAST_DRAGONS("East Dragons @red@(Level 23)", "Popular for PvM and PvPers.\\n@red@Warning: Level 23 Wilderness!", TeleportLocations.EDGEEASTDRAGONS, Category.WILDERNESS),
		DEMONIC_RUINS("Demonic Ruins @red@(Level 46)", "Once a temple, mangled by demons.\\n@red@Warning: Level 46 Wilderness!", TeleportLocations.DEMONIC_RUINS, Category.WILDERNESS),

		;
		
		private final String descriptionTitle;
		private final String descriptionText1;
		private final TeleportLocations destination;
		private final Category category;
		private int teleportIndex = -1;
		
		TeleportInterfaceData(String descriptionTitle, String descriptionText, TeleportLocations destination, Category category) {
			this.descriptionTitle = descriptionTitle;
			this.descriptionText1 = descriptionText;
			this.destination = destination;
			this.category = category;
		}

		public String getDescriptionTitle() {
			return this.descriptionTitle;
		}

		public String getDescriptionText1() {
			return this.descriptionText1;
		}
		public TeleportLocations getDestination() {
			return this.destination;
		}

		/**
		 * Gets of the index of the teleport in the context of it's teleport category.
		 */
		public int getTeleportIndex() {
			if (teleportIndex == -1) {
				int newTeleportIndex = 0;
				for (int index = 0; index < values().length; index++) {
					if (values()[index].category == this.category) {
						if (values()[index] == this) {
							break;
						} else {
							newTeleportIndex++;
						}
					}
				}
				teleportIndex = newTeleportIndex;
				return newTeleportIndex;
			} else {
				return teleportIndex;
			}
		}

		public int getButtonId() {
			return TELEPORT_BUTTON_IDS[category.ordinal()] + getTeleportIndex();
		}

		public int getStringId() {
			return TELEPORT_STRING_IDS[category.ordinal()] + getTeleportIndex();
		}

		public static TeleportInterfaceData forTitle(String title) {
			for(TeleportInterfaceData data: TeleportInterfaceData.values()) {
				if(data.getDescriptionTitle().equalsIgnoreCase(title)) {
					return data;
				}
			}
			return null;
		}
		
		public static TeleportInterfaceData forButtonId(int button) {
			for(TeleportInterfaceData data: TeleportInterfaceData.values()) {
				if(data.getButtonId() == button) {
					return data;
				}
			}
			return null;
		}
	}

}
