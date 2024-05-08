package com.ruse.model.entity.character.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ruse.engine.task.impl.FamiliarSpawnTask;
import com.ruse.model.*;
import com.ruse.model.container.impl.Bank;
import com.ruse.net.login.LoginResponses;
import com.ruse.util.json.ItemTypeAdapter;
import com.ruse.world.content.DropLog;
import com.ruse.world.content.KillsTracker;
import com.ruse.world.content.attendance.AttendanceProgress;
import com.ruse.world.content.attendance.AttendanceTab;
import com.ruse.world.content.combat.magic.CombatSpells;
import com.ruse.world.content.combat.weapon.FightType;
import com.ruse.world.content.skill.SkillManager;
import com.ruse.world.content.strangertasks.StrangerTask;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;

public class PlayerLoading {

	public static int getResult(Player player) {
		// Create the path and file objects.
		Path path = Paths.get("./data/saves/characters/", player.getUsername() + ".json");
		File file = path.toFile();

		// If the file doesn't exist, we're logging in for the first
		// time and can skip all of this.
		if (!file.exists()) {
			return LoginResponses.NEW_ACCOUNT;
		}

		// Now read the properties from the json parser.
		try (FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			Gson builder = new GsonBuilder()
					.registerTypeAdapter(Item.class, new ItemTypeAdapter())
					.create();
			JsonObject reader = (JsonObject) fileParser.parse(fileReader);


			if (reader.has("total-play-time-ms")) {
				player.setTotalPlayTime(reader.get("total-play-time-ms").getAsLong());
			}

			if (reader.has("username")) {
				player.setUsername(reader.get("username").getAsString());
			}

			if (reader.has("password")) {
				String password = reader.get("password").getAsString();
				if (!player.getPassword().equals(password)) {
					return LoginResponses.LOGIN_INVALID_CREDENTIALS;
				}
				player.setPassword(password);
			} else if (reader.has("hash")) {
				String hash = reader.get("hash").getAsString();
				player.setSalt(hash.substring(0, 29));
				if (BCrypt.checkpw(player.getPassword(), hash)) {
				} else {
					return LoginResponses.LOGIN_INVALID_CREDENTIALS;
				}
			}

			if (reader.has("email")) {
				player.setEmailAddress(reader.get("email").getAsString());
			}

			if (reader.has("staff-rights")) {
				String rights = reader.get("staff-rights").getAsString();

				/** retard keen using shit ranks **/

				if(rights.equals("DONATOR")) {
					rights = "BRONZE_MEMBER";
				} else if(rights.equals("SUPER_DONATOR")) {
					rights = "SILVER_MEMBER";
				} else if(rights.equals("EXTREME_DONATOR")) {
					rights = "GOLD_MEMBER";
				} else if(rights.equals("DIAMOND_DONATOR")) {
					rights = "PLATINUM_MEMBER";
				} else if(rights.equals("ONYX_DONATOR")) {
					rights = "DIAMOND_MEMBER";
				}

				player.setRights(PlayerRights.valueOf(rights));
			}

			if (reader.has("game-mode")) {
				if (reader.get("game-mode").getAsString().equalsIgnoreCase("HARDCORE_IRONMAN")) {
					player.setGameMode(GameMode.ULTIMATE_IRONMAN);
				} else {
					player.setGameMode(GameMode.valueOf(reader.get("game-mode").getAsString()));
				}
			}

			if (reader.has("position")) {
				player.getPosition().setAs(builder.fromJson(reader.get("position"), Position.class));
			}

			if(reader.has("online-status")) {
				player.getRelations().setStatus(PlayerRelations.PrivateChatStatus.valueOf(reader.get("online-status").getAsString()), false);
			}

			if (reader.has("money-pouch")) {
				player.setMoneyInPouch(reader.get("money-pouch").getAsLong());
			}

			if (reader.has("given-starter")) {
				player.setReceivedStarter(reader.get("given-starter").getAsBoolean());
			}

			if (reader.has("donated")) {
				player.incrementAmountDonated(reader.get("donated").getAsInt());
			}


			if(reader.has("minutes-bonus-exp")) {
				player.setMinutesBonusExp(reader.get("minutes-bonus-exp").getAsInt(), false);
			}

			if (reader.has("total-gained-exp")) {
				player.getSkillManager().setTotalGainedExp(reader.get("total-gained-exp").getAsInt());
			}

			if(reader.has("voting-points")) {
				player.getPointsHandler().setVotingPoints(reader.get("voting-points").getAsInt(), false);
			}

			if(reader.has("slayer-points")) {
				player.getPointsHandler().setSlayerPoints(reader.get("slayer-points").getAsInt(), false);
			}

			if(reader.has("bh-rank")) {
				player.getAppearance().setBountyHunterSkull(reader.get("bh-rank").getAsInt());
			}

			if (reader.has("gender")) {
				player.getAppearance().setGender(Gender.valueOf(reader.get("gender").getAsString()));
			}

			if (reader.has("spell-book")) {
				player.setSpellbook(MagicSpellbook.valueOf(reader.get("spell-book").getAsString()));
			}

			if (reader.has("prayer-book")) {
				player.setPrayerbook(Prayerbook.valueOf(reader.get("prayer-book").getAsString()));
			}
			if (reader.has("running")) {
				player.setRunning(reader.get("running").getAsBoolean());
			}
			if (reader.has("run-energy")) {
				player.setRunEnergy(reader.get("run-energy").getAsInt());
			}
			if (reader.has("music")) {
				player.setMusicActive(reader.get("music").getAsBoolean());
			}
			if (reader.has("sounds")) {
				player.setSoundsActive(reader.get("sounds").getAsBoolean());
			}
			if (reader.has("auto-retaliate")) {
				player.setAutoRetaliate(reader.get("auto-retaliate").getAsBoolean());
			}
			if (reader.has("xp-locked")) {
				player.setExperienceLocked(reader.get("xp-locked").getAsBoolean());
			}
			if (reader.has("fight-type")) {
				player.setFightType(FightType.valueOf(reader.get("fight-type").getAsString()));
			}
			if (reader.has("skull-timer")) {
				player.setSkullTimer(reader.get("skull-timer").getAsInt());
			}
			if (reader.has("accept-aid")) {
				player.setAcceptAid(reader.get("accept-aid").getAsBoolean());
			}
			if (reader.has("poison-damage")) {
				player.setPoisonDamage(reader.get("poison-damage").getAsInt());
			}
			if (reader.has("poison-immunity")) {
				player.setPoisonImmunity(reader.get("poison-immunity").getAsInt());
			}
			if (reader.has("overload-timer")) {
				player.setOverloadPotionTimer(reader.get("overload-timer").getAsInt());
			}
			if (reader.has("fire-immunity")) {
				player.setFireImmunity(reader.get("fire-immunity").getAsInt());
			}
			if (reader.has("fire-damage-mod")) {
				player.setFireDamageModifier(reader.get("fire-damage-mod").getAsInt());
			}
			if (reader.has("overload-timer")) {
				player.setOverloadPotionTimer(reader.get("overload-timer").getAsInt());
			}
			if (reader.has("prayer-renewal-timer")) {
				player.setPrayerRenewalPotionTimer(reader.get("prayer-renewal-timer").getAsInt());
			}

			if (reader.has("special-amount")) {
				player.setSpecialPercentage(reader.get("special-amount").getAsInt());
			}

			if(reader.has("entered-gwd-room")) {
				player.getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(reader.get("entered-gwd-room").getAsBoolean());
			}

			if(reader.has("gwd-altar-delay")) {
				player.getMinigameAttributes().getGodwarsDungeonAttributes().setAltarDelay(reader.get("gwd-altar-delay").getAsLong());
			}

			if(reader.has("gwd-killcount")) {
				player.getMinigameAttributes().getGodwarsDungeonAttributes().setKillcount(builder.fromJson(reader.get("gwd-killcount"), int[].class));
			}

			if(reader.has("effigy")) {
				player.setEffigy(reader.get("effigy").getAsInt());
			}

			if (reader.has("summon-npc")) {
				int npc = reader.get("summon-npc").getAsInt();
				if(npc > 0)
					player.getSummoning().setFamiliarSpawnTask(new FamiliarSpawnTask(player)).setFamiliarId(npc);
			}
			if (reader.has("summon-death")) {
				int death = reader.get("summon-death").getAsInt();
				if(death > 0 && player.getSummoning().getSpawnTask() != null)
					player.getSummoning().getSpawnTask().setDeathTimer(death);
			}
			if (reader.has("process-farming")) {
				player.setProcessFarming(reader.get("process-farming").getAsBoolean());
			}

			if (reader.has("clanchat")) {
				String clan = reader.get("clanchat").getAsString();
				if(!clan.equals("null"))
					player.setClanChatName(clan);
			}
			if (reader.has("autocast")) {
				player.setAutocast(reader.get("autocast").getAsBoolean());
			}
			if (reader.has("autocast-spell")) {
				int spell = reader.get("autocast-spell").getAsInt();
				if(spell != -1)
					player.setAutocastSpell(CombatSpells.getSpell(spell));
			}

			if (reader.has("dfs-charges")) {
				player.incrementDfsCharges(reader.get("dfs-charges").getAsInt());
			}
			if (reader.has("kills")) {
				KillsTracker.submit(player, builder.fromJson(reader.get("kills").getAsJsonArray(), KillsTracker.KillsEntry[].class));
			}

			if (reader.has("drops")) {
				DropLog.submit(player, builder.fromJson(reader.get("drops").getAsJsonArray(), DropLog.DropLogEntry[].class));
			}

			if (reader.has("bank-pin")) {
				player.getBankPinAttributes().setBankPin(builder.fromJson(reader.get("bank-pin").getAsJsonArray(), int[].class));
			}

			if (reader.has("has-bank-pin")) {
				player.getBankPinAttributes().setHasBankPin(reader.get("has-bank-pin").getAsBoolean());
			}
			if (reader.has("last-pin-attempt")) {
				player.getBankPinAttributes().setLastAttempt(reader.get("last-pin-attempt").getAsLong());
			}
			if (reader.has("invalid-pin-attempts")) {
				player.getBankPinAttributes().setInvalidAttempts(reader.get("invalid-pin-attempts").getAsInt());
			}

			if (reader.has("bank-pin")) {
				player.getBankPinAttributes().setBankPin(builder.fromJson(reader.get("bank-pin").getAsJsonArray(), int[].class));
			}

			if (reader.has("appearance")) {
				player.getAppearance().set(builder.fromJson(
						reader.get("appearance").getAsJsonArray(), int[].class));
			}

			if (reader.has("agility-obj")) {
				player.setCrossedObstacles(builder.fromJson(
						reader.get("agility-obj").getAsJsonArray(), boolean[].class));
			}

			if (reader.has("skills")) {
				player.getSkillManager().setSkills(builder.fromJson(
						reader.get("skills"), SkillManager.Skills.class));
			}
			if (reader.has("inventory")) {
				player.getInventory().setItems(builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class));
			}
			if (reader.has("equipment")) {
				player.getEquipment().setItems(builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class));
			}

			for(int i = 0; i < 9; i++) {
				if(reader.has("bank-"+i+""))
					player.setBank(i, new Bank(player)).getBank(i).addItems(builder.fromJson(reader.get("bank-"+i+"").getAsJsonArray(), Item[].class), false);
			}

			if(reader.has("bank-0")) {
				player.setBank(0, new Bank(player)).getBank(0).addItems(builder.fromJson(reader.get("bank-0").getAsJsonArray(), Item[].class), false);
			}

			if(reader.has("bank-1")) {
				player.setBank(1, new Bank(player)).getBank(1).addItems(builder.fromJson(reader.get("bank-1").getAsJsonArray(), Item[].class), false);
			}

			if(reader.has("bank-2")) {
				player.setBank(2, new Bank(player)).getBank(2).addItems(builder.fromJson(reader.get("bank-2").getAsJsonArray(), Item[].class), false);
			}

			if(reader.has("bank-3")) {
				player.setBank(3, new Bank(player)).getBank(3).addItems(builder.fromJson(reader.get("bank-3").getAsJsonArray(), Item[].class), false);
			}

			if(reader.has("bank-4")) {
				player.setBank(4, new Bank(player)).getBank(4).addItems(builder.fromJson(reader.get("bank-4").getAsJsonArray(), Item[].class), false);
			}

			if(reader.has("bank-5")) {
				player.setBank(5, new Bank(player)).getBank(5).addItems(builder.fromJson(reader.get("bank-5").getAsJsonArray(), Item[].class), false);
			}

			if(reader.has("bank-6")) {
				player.setBank(6, new Bank(player)).getBank(6).addItems(builder.fromJson(reader.get("bank-6").getAsJsonArray(), Item[].class), false);
			}

			if(reader.has("bank-7")) {
				player.setBank(7, new Bank(player)).getBank(7).addItems(builder.fromJson(reader.get("bank-7").getAsJsonArray(), Item[].class), false);
			}

			if(reader.has("bank-8")) {
				player.setBank(8, new Bank(player)).getBank(8).addItems(builder.fromJson(reader.get("bank-8").getAsJsonArray(), Item[].class), false);
			}

			if (reader.has("friends")) {
				long[] friends = builder.fromJson(
						reader.get("friends").getAsJsonArray(), long[].class);

				for (long l : friends) {
					player.getRelations().getFriendList().add(l);
				}
			}
			if (reader.has("ignores")) {
				long[] ignores = builder.fromJson(
						reader.get("ignores").getAsJsonArray(), long[].class);

				for (long l : ignores) {
					player.getRelations().getIgnoreList().add(l);
				}
			}

			if (reader.has("loyalty-titles")) {
				player.setUnlockedLoyaltyTitles(builder.fromJson(reader.get("loyalty-titles").getAsJsonArray(), boolean[].class));
			}

			if (reader.has("reffered")) {
				player.setReffered(reader.get("reffered").getAsBoolean());
			}

			if (reader.has("toggledglobalmessages")) {
				player.setToggledGlobalMessages(reader.get("toggledglobalmessages").getAsBoolean());
			}

			if (reader.has("p-tps")) {
				player.setPreviousTeleports(builder.fromJson(reader.get("p-tps").getAsJsonArray(), int[].class));
			}

			if(reader.has("lastloggedinday")) {
				player.getAttendanceManager().setLastLoggedInDate(LocalDate.parse(reader.get("lastloggedinday").getAsString()));
			}

			if (reader.has("attendance-popup")) {
				player.getAttendanceUI().setPopUp(reader.get("attendance-popup").getAsBoolean());
			}

			if(reader.has("attendanceprogress")) {
				HashMap<AttendanceTab, AttendanceProgress> temp = builder.fromJson(reader.get("attendanceprogress"),
						new TypeToken<HashMap<AttendanceTab, AttendanceProgress>>() {
						}.getType());
				player.getAttendanceManager().getPlayerAttendanceProgress().putAll(temp);
			}

			if(reader.has("stranger-tasks")) {
				HashMap<StrangerTask.Difficulty, StrangerTask> temp = builder.fromJson(reader.get("stranger-tasks"),
						new TypeToken<HashMap<StrangerTask.Difficulty, StrangerTask>>() {
						}.getType());
				player.getStrangerTasks().putAll(temp);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return LoginResponses.LOGIN_SUCCESSFUL;
		}
		return LoginResponses.LOGIN_SUCCESSFUL;
	}
}