package com.ruse.model.entity.character.player;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ruse.GameServer;
import com.ruse.model.Item;
import com.ruse.util.Misc;
import com.ruse.util.json.ItemTypeAdapter;
import org.apache.commons.lang3.text.WordUtils;
import org.mindrot.jbcrypt.BCrypt;

public  class PlayerSaving {


	public static void save(Player player) {
		if(player.newPlayer())
			return;
		// Create the path and file objects.
		Path path = Paths.get("./data/saves/characters/", player.getUsername() + ".json");
		File file = path.toFile();
		file.getParentFile().setWritable(true);

		// Attempt to make the player save directory if it doesn't
		// exist.
		if (!file.getParentFile().exists()) {
			try {
				file.getParentFile().mkdirs();
			} catch (SecurityException e) {
				System.out.println("Unable to create directory for player data!");
			}
		}

		try (FileWriter writer = new FileWriter(file)) {
			JsonObject object = new JsonObject();
			Gson builder = new GsonBuilder()
					.registerTypeAdapter(Item.class, new ItemTypeAdapter())
					.setPrettyPrinting()
					.create();


			object.addProperty("total-play-time-ms", player.getTotalPlayTime());
			object.addProperty("username", player.getUsername().trim());

			if (GameServer.getConfiguration().isEncryptPasswords()) {
				object.addProperty("hash", BCrypt.hashpw(player.getPassword(), player.getSalt()));
			} else {
				object.addProperty("password", player.getPassword().trim());
			}

			object.addProperty("email", player.getEmailAddress() == null ? "null" : player.getEmailAddress().trim());
			object.addProperty("staff-rights", player.getRights().name());
			object.addProperty("game-mode", player.getGameMode().name());
			object.addProperty("loyalty-title", player.getLoyaltyTitle().name());
			/** HEX YELL COLORS **/
			object.addProperty("yellhexcolor", player.getYellHex() == null ? "ffffff" : player.getYellHex());
			object.add("position", builder.toJsonTree(player.getPosition()));
			object.addProperty("online-status", player.getRelations().getStatus().name());
			object.addProperty("given-starter", (player.didReceiveStarter()));
			object.addProperty("money-pouch", (player.getMoneyInPouch()));
			object.addProperty("donated", (player.getAmountDonated()));
			object.addProperty("minutes-bonus-exp", (player.getMinutesBonusExp()));
			object.addProperty("total-gained-exp", (player.getSkillManager().getTotalGainedExp()));
			object.addProperty("barrows-points", (player.getPointsHandler().getBarrowsPoints()));
			object.addProperty("member-points", (player.getPointsHandler().getMemberPoints()));
			object.addProperty("Skilling-points", (player.getPointsHandler().getSkillingPoints()));
			object.addProperty("prestige-points", (player.getPointsHandler().getPrestigePoints()));
			object.addProperty("achievement-points", (player.getPointsHandler().getAchievementPoints()));
			object.addProperty("dung-tokens", (player.getPointsHandler().getDungeoneeringTokens()));
			object.addProperty("commendations", (player.getPointsHandler().getCommendations()));
			object.addProperty("loyalty-points", (player.getPointsHandler().getLoyaltyPoints()));
			object.addProperty("voting-points", (player.getPointsHandler().getVotingPoints()));
			object.addProperty("slayer-points", (player.getPointsHandler().getSlayerPoints()));
			object.addProperty("pk-points", (player.getPointsHandler().getPkPoints()));
			object.addProperty("player-kills", (player.getPlayerKillingAttributes().getPlayerKills()));
			object.addProperty("player-killstreak", (player.getPlayerKillingAttributes().getPlayerKillStreak()));
			object.addProperty("player-deaths", (player.getPlayerKillingAttributes().getPlayerDeaths()));
			object.addProperty("target-percentage", (player.getPlayerKillingAttributes().getTargetPercentage()));
			object.addProperty("bh-rank", (player.getAppearance().getBountyHunterSkull()));
			object.addProperty("gender", player.getAppearance().getGender().name());
			object.addProperty("spell-book", player.getSpellbook().name());
			object.addProperty("prayer-book", player.getPrayerbook().name());
			object.addProperty("running", (player.isRunning()));
			object.addProperty("run-energy", (player.getRunEnergy()));
			object.addProperty("music", (player.musicActive()));
			object.addProperty("sounds", (player.soundsActive()));
			object.addProperty("auto-retaliate", (player.isAutoRetaliate()));
			object.addProperty("xp-locked", (player.experienceLocked()));
			object.addProperty("veng-cast", (player.hasVengeance()));
			object.addProperty("last-veng", (player.getLastVengeance().elapsed()));
			object.addProperty("fight-type", player.getFightType().name());
			object.addProperty("sol-effect", (player.getStaffOfLightEffect()));
			object.addProperty("skull-timer", (player.getSkullTimer()));
			object.addProperty("accept-aid", (player.isAcceptAid()));
			object.addProperty("poison-damage", (player.getPoisonDamage()));
			object.addProperty("poison-immunity", (player.getPoisonImmunity()));
			object.addProperty("overload-timer", (player.getOverloadPotionTimer()));
			object.addProperty("fire-immunity", (player.getFireImmunity()));
			object.addProperty("fire-damage-mod", (player.getFireDamageModifier()));
			object.addProperty("prayer-renewal-timer", (player.getPrayerRenewalPotionTimer()));
			object.addProperty("teleblock-timer", (player.getTeleblockTimer()));
			object.addProperty("special-amount", (player.getSpecialPercentage()));
			object.addProperty("entered-gwd-room", (player.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()));
			object.addProperty("gwd-altar-delay", (player.getMinigameAttributes().getGodwarsDungeonAttributes().getAltarDelay()));
			object.add("gwd-killcount", builder.toJsonTree(player.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()));
			object.addProperty("effigy", (player.getEffigy()));
			object.addProperty("summon-npc", (player.getSummoning().getFamiliar() != null ? player.getSummoning().getFamiliar().getSummonNpc().getId() : -1));
			object.addProperty("summon-death", (player.getSummoning().getFamiliar() != null ? player.getSummoning().getFamiliar().getDeathTimer() : -1));
			object.addProperty("process-farming", (player.shouldProcessFarming()));
			object.addProperty("clanchat", player.getClanChatName() == null ? "null" : player.getClanChatName().trim());
			object.addProperty("autocast", (player.isAutocast()));
			object.addProperty("autocast-spell", player.getAutocastSpell() != null ? player.getAutocastSpell().spellId() : -1);
			object.addProperty("dfs-charges", player.getDfsCharges());
			object.addProperty("recoil-deg", (player.getRecoilCharges()));
			object.addProperty("blowpipe-deg", (player.getBlowpipeCharges()));
			object.add("brawlers-deg", builder.toJsonTree(player.getBrawlerChargers()));
			object.add("ancient-deg", builder.toJsonTree(player.getAncientArmourCharges()));
			object.add("killed-players", builder.toJsonTree(player.getPlayerKillingAttributes().getKilledPlayers()));
			object.add("barrows-brother", builder.toJsonTree(player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()));
			object.addProperty("random-coffin", (player.getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()));
			object.addProperty("barrows-killcount", (player.getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount()));
			object.add("nomad", builder.toJsonTree(player.getMinigameAttributes().getNomadAttributes().getQuestParts()));
			object.add("recipe-for-disaster", builder.toJsonTree(player.getMinigameAttributes().getRecipeForDisasterAttributes().getQuestParts()));
			object.addProperty("recipe-for-disaster-wave", (player.getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted()));
			object.addProperty("clue-progress", (player.getClueProgress()));
			object.add("dung-items-bound", builder.toJsonTree(player.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems()));
			object.addProperty("rune-ess", (player.getStoredRuneEssence()));
			object.addProperty("pure-ess", (player.getStoredPureEssence()));
			object.addProperty("has-bank-pin", (player.getBankPinAttributes().hasBankPin()));
			object.addProperty("last-pin-attempt", (player.getBankPinAttributes().getLastAttempt()));
			object.addProperty("invalid-pin-attempts", (player.getBankPinAttributes().getInvalidAttempts()));
			object.add("bank-pin", builder.toJsonTree(player.getBankPinAttributes().getBankPin()));
			object.add("appearance", builder.toJsonTree(player.getAppearance().getLook()));
			object.add("agility-obj", builder.toJsonTree(player.getCrossedObstacles()));
			object.add("skills", builder.toJsonTree(player.getSkillManager().getSkills()));
			object.add("inventory", builder.toJsonTree(player.getInventory().getItems()));
			object.add("equipment", builder.toJsonTree(player.getEquipment().getItems()));
			object.add("preset-equipment", builder.toJsonTree(player.getPreSetEquipment().getItems()));
			object.add("bank-0", builder.toJsonTree(player.getBank(0).getValidItems()));
			object.add("bank-1", builder.toJsonTree(player.getBank(1).getValidItems()));
			object.add("bank-2", builder.toJsonTree(player.getBank(2).getValidItems()));
			object.add("bank-3", builder.toJsonTree(player.getBank(3).getValidItems()));
			object.add("bank-4", builder.toJsonTree(player.getBank(4).getValidItems()));
			object.add("bank-5", builder.toJsonTree(player.getBank(5).getValidItems()));
			object.add("bank-6", builder.toJsonTree(player.getBank(6).getValidItems()));
			object.add("bank-7", builder.toJsonTree(player.getBank(7).getValidItems()));
			object.add("bank-8", builder.toJsonTree(player.getBank(8).getValidItems()));

			object.add("ge-slots", builder.toJsonTree(player.getGrandExchangeSlots()));

			/** STORE SUMMON **/
			if(player.getSummoning().getBeastOfBurden() != null) {
				object.add("store", builder.toJsonTree(player.getSummoning().getBeastOfBurden().getValidItems()));
			}
			object.add("charm-imp", builder.toJsonTree(player.getSummoning().getCharmImpConfigs()));

			object.add("friends", builder.toJsonTree(player.getRelations().getFriendList().toArray()));
			object.add("ignores", builder.toJsonTree(player.getRelations().getIgnoreList().toArray()));
			object.add("loyalty-titles", builder.toJsonTree(player.getUnlockedLoyaltyTitles()));
			object.add("kills", builder.toJsonTree(player.getKillsTracker().toArray()));
			object.add("drops", builder.toJsonTree(player.getDropLog().toArray()));
			object.addProperty("fri13may16", (player.didFriday13May2016()));
			object.addProperty("spiritdebug", (player.isSpiritDebug()));
			object.addProperty("reffered", (player.gotReffered()));
			object.addProperty("indung", (player.isInDung()));
			object.addProperty("toggledglobalmessages", (player.toggledGlobalMessages()));
			object.addProperty("flying", (player.isFlying()));
			object.addProperty("canfly", (player.canFly()));
			object.addProperty("ghostwalking", (player.isGhostWalking()));
			object.addProperty("canghostwalk", (player.canGhostWalk()));
			object.addProperty("barrowschests", (player.getPointsHandler().getBarrowsChests()));
			object.addProperty("cluesteps", (player.getPointsHandler().getClueSteps()));
			object.add("hween2016", builder.toJsonTree(player.getHween2016All()));
			object.addProperty("donehween2016", (player.doneHween2016()));
			object.add("bosspets", builder.toJsonTree(player.getBossPetsAll()));
			object.addProperty("christmas2016",  (player.getChristmas2016()));
			object.addProperty("newYear2017",  (player.getNewYear2017()));
			object.addProperty("easter2017",  (player.getEaster2017()));
			object.add("hcimdunginventory", builder.toJsonTree(player.getDungeoneeringIronInventory().getItems()));
			object.add("hcimdungequipment", builder.toJsonTree(player.getDungeoneeringIronEquipment().getItems()));
			object.addProperty("bonecrusheffect", (player.getBonecrushEffect()));
			object.add("p-tps", builder.toJsonTree(player.getPreviousTeleports()));
			object.addProperty("yell-tit", player.getYellTitle() == null ? "null" : player.getYellTitle());
			//doneHween2016
			//object.add("uimDungItems", builder.toJsonTre1e(player.getBank(0).getValidItems()));

			writer.write(builder.toJson(object));
			writer.close();

			/*
			 * Housing
			 */
         /*   FileOutputStream fileOut = new FileOutputStream("./data/saves/housing/rooms/" + player.getUsername() + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(player.getHouseRooms());
            out.close();
            fileOut.close();

            fileOut = new FileOutputStream("./data/saves/housing/furniture/" + player.getUsername() + ".ser");
            out = new ObjectOutputStream(fileOut);
            out.writeObject(player.getHouseFurniture());
            out.close();
            fileOut.close();

            fileOut = new FileOutputStream("./data/saves/housing/portals/" + player.getUsername() + ".ser");
            out = new ObjectOutputStream(fileOut);
            out.writeObject(player.getHousePortals());
            out.close();
            fileOut.close();
            */
		} catch (Exception e) {
			// An error happened while saving.
			GameServer.getLogger().log(Level.WARNING,
					"An error has occured while saving a character file!", e);
		}
	}

	public static boolean playerExists(String p) {
		p = Misc.formatPlayerName(p.toLowerCase());
		p = WordUtils.capitalizeFully(p);
		p.replaceAll(" ", "\\ ");
		//System.out.println("./data/saves/characters/"+p+".json ....... "+ new File("./data/saves/characters/"+p+".json").exists());
		return new File("./data/saves/characters/"+p+".json").exists();
	}

}
