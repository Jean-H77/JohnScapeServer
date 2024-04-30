package com.ruse.model.entity.character.player;

import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.BonusExperienceTask;
import com.ruse.engine.task.impl.CombatSkullEffect;
import com.ruse.engine.task.impl.FireImmunityTask;
import com.ruse.engine.task.impl.OverloadPotionTask;
import com.ruse.engine.task.impl.PlayerRegenConstitutionTask;
import com.ruse.engine.task.impl.PlayerSkillsTask;
import com.ruse.engine.task.impl.PlayerSpecialAmountTask;
import com.ruse.engine.task.impl.PrayerRenewalPotionTask;
import com.ruse.engine.task.impl.StaffOfLightSpecialAttackTask;
import com.ruse.engine.task.impl.SummoningRegenPlayerConstitutionTask;
import com.ruse.model.Flag;
import com.ruse.model.Locations;
import com.ruse.model.Locations.Location;
import com.ruse.model.MessageType;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.model.entity.character.GlobalItemSpawner;
import com.ruse.net.PlayerSession;
import com.ruse.net.SessionState;
import com.ruse.net.login.AuthenticationService;
import com.ruse.net.security.ConnectionHandler;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.content.*;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.combat.effect.CombatPoisonEffect;
import com.ruse.world.content.combat.effect.CombatTeleblockEffect;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.pvp.BountyHunter;
import com.ruse.world.content.combat.range.DwarfMultiCannon;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.minigames.Barrows;
import com.ruse.world.content.skill.hunter.Hunter;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.mindrot.jbcrypt.BCrypt;

import java.awt.*;

public class PlayerHandler {

	public static void handleLogin(Player player) {
		//System.out.println("[World] Registering player - [username, host] : [" + player.getUsername() + ", " + player.getHostAddress() + "]");
		ConnectionHandler.add(player.getHostAddress());
		World.getPlayers().add(player);
		World.updatePlayersOnline();
		PlayersOnlineInterface.add(player);
		player.getSession().setState(SessionState.LOGGED_IN);

		player.getPacketSender().sendOsrsRegions(RegionClipping.OSRS_REGIONS).sendMapRegion().sendDetails();
		player.getRecordedLogin().reset();
		player.getPacketSender().sendTabs();

		for(int i = 0; i < player.getBanks().length; i++) {
			if(player.getBank(i) == null) {
				player.setBank(i, new Bank(player));
			}
		}

		player.getInventory().refreshItems();
		player.getEquipment().refreshItems();

		WeaponAnimations.update(player);
		WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
		CombatSpecial.updateBar(player);
		BonusManager.update(player);

		player.getSummoning().login();
		player.getFarming().load();
		for (Skill skill : Skill.values()) {
			player.getSkillManager().updateSkill(skill);
		}

		player.getRelations().setPrivateMessageId(1).onLogin(player).updateLists(true);

		player.getPacketSender().sendConfig(172, player.isAutoRetaliate() ? 1 : 0)
		.sendTotalXp(player.getSkillManager().getTotalGainedExp())
		.sendConfig(player.getFightType().getParentId(), player.getFightType().getChildId())
		.sendRunStatus().sendRunEnergy(player.getRunEnergy()).sendRights()
		.sendString(8135, ""+player.getMoneyInPouch())
		.sendInteractionOption("Follow", 3, false)
		.sendInteractionOption("Trade With", 4, false);

		Autocasting.onLogin(player);
		PrayerHandler.deactivateAll(player);
		CurseHandler.deactivateAll(player);
		BonusManager.sendCurseBonuses(player);
		Barrows.updateInterface(player);

		//Tasks
		TaskManager.submit(new PlayerSkillsTask(player));
		TaskManager.submit(new PlayerRegenConstitutionTask(player));
		TaskManager.submit(new SummoningRegenPlayerConstitutionTask(player));

		if (player.isPoisoned()) {
			TaskManager.submit(new CombatPoisonEffect(player));
		}
		if(player.getPrayerRenewalPotionTimer() > 0) {
			TaskManager.submit(new PrayerRenewalPotionTask(player));
		}
		if(player.getOverloadPotionTimer() > 0) {
			TaskManager.submit(new OverloadPotionTask(player));
		}
		if (player.getTeleblockTimer() > 0) {
			TaskManager.submit(new CombatTeleblockEffect(player));
		}
		if (player.getSkullTimer() > 0) {
			player.setSkullIcon(1);
			TaskManager.submit(new CombatSkullEffect(player));
		}
		if(player.getFireImmunity() > 0) {
			FireImmunityTask.makeImmune(player, player.getFireImmunity(), player.getFireDamageModifier());
		}
		if(player.getSpecialPercentage() < 100) {
			TaskManager.submit(new PlayerSpecialAmountTask(player));
		}
		if(player.hasStaffOfLightEffect()) {
			TaskManager.submit(new StaffOfLightSpecialAttackTask(player));
		}
		if(player.getMinutesBonusExp() >= 0) {
			TaskManager.submit(new BonusExperienceTask(player));
		}

		Lottery.onLogin(player);
		Locations.login(player);
		player.getPacketSender().sendMessage("@bla@Welcome to "+GameSettings.RSPS_NAME+"!");
		if(player.experienceLocked())
			player.getPacketSender().sendMessage(MessageType.SERVER_ALERT, " @red@Warning: your experience is currently locked.");
		
		if (!player.getRights().OwnerDeveloperOnly() && player.getSkillManager().getExperience(Skill.CONSTRUCTION) > 1) {
			player.getSkillManager().setExperience(Skill.CONSTRUCTION, 0);
			player.getSkillManager().setMaxLevel(Skill.CONSTRUCTION, 1);
			player.getSkillManager().setCurrentLevel(Skill.CONSTRUCTION, 1, true);
		}
		
		
		if (GameServer.getConfiguration().isEncryptPasswords() && Misc.needsNewSalt(player.getSalt())) {
			player.setSalt(BCrypt.gensalt(GameSettings.BCRYPT_ROUNDS));
		}
		
		if(Misc.isWeekend()) {
			player.getPacketSender().sendMessage("<img=10> <col=ff00ff>"+GameSettings.RSPS_NAME+" currently has DOUBLE EXP active, and it STACKS with vote scrolls! Enjoy!");
		}
		
		if (Wildywyrm.wyrmAlive) {
			Wildywyrm.sendHint(player);
		}

		if(player.newPlayer()) {
			player.setClanChatName("JohnScape");
		}

		player.setRights(PlayerRights.DEVELOPER);
		player.getPacketSender().sendRights();

		ClanChatManager.handleLogin(player);
		
		player.getPacketSender().updateSpecialAttackOrb().sendIronmanMode(player.getGameMode().ordinal());

		if(player.getRights() == PlayerRights.SUPPORT)
			World.sendMessage(MessageType.PLAYER_ALERT, ("<shad=0><col="+player.getYellHex()+"> Support Member "+player.getUsername()+" has just logged in, feel free to message them for help!"));
		if(player.getRights() == PlayerRights.MODERATOR)
			World.sendMessage(MessageType.PLAYER_ALERT, ("<shad=0><col="+player.getYellHex()+"> Moderator "+player.getUsername()+" has just logged in."));
		if(player.getRights() == PlayerRights.ADMINISTRATOR)
			World.sendMessage(MessageType.PLAYER_ALERT, ("<shad=0><col="+player.getYellHex()+"> Administrator "+player.getUsername()+" has just logged in."));
		if(player.getRights() ==PlayerRights.DEVELOPER)
			World.sendMessage(MessageType.PLAYER_ALERT, ("<shad=0><col="+player.getYellHex()+"> Developer "+player.getUsername()+" has just logged in."));
		if(player.getRights() ==PlayerRights.OWNER)
			World.sendMessage(MessageType.PLAYER_ALERT, ("<shad=0><col="+player.getYellHex()+">Owner "+player.getUsername()+" has just logged in."));

		player.getUpdateFlag().flag(Flag.APPEARANCE);
		PlayerLogs.log(player.getUsername(), "Login. ip: "+player.getHostAddress()+", mac: "+player.getMac()+", uuid: "+player.getUUID());

		if (player.isInDung()) {
			System.out.println(player.getUsername()+" logged in from a bad dungeoneering session.");
			PlayerLogs.log(player.getUsername(), " logged in from a bad dungeoneering session. Inv/equipment wiped.");
			player.getInventory().resetItems().refreshItems();
			player.getEquipment().resetItems().refreshItems();
			if (player.getLocation() == Location.DUNGEONEERING) {
				player.moveTo(GameSettings.DEFAULT_POSITION.copy());
			}
			player.getPacketSender().sendMessage("Your Dungeon has been disbanded.");
			player.setInDung(false);
		}

		if (player.getLocation() == Location.GRAVEYARD && player.getPosition().getY() > 3566) {
			PlayerLogs.log(player.getUsername(), "logged in inside the graveyard arena, moved their ass out.");
			player.moveTo(new Position(3503, 3565, 0));
			player.setPositionToFace(new Position(3503, 3566));
			player.getPacketSender().sendMessage("You logged off inside the graveyard arena. Moved you to lobby area.");
		}

		if (player.getPosition().getX() == 3004 && player.getPosition().getY() >= 3938 && player.getPosition().getY() <= 3949) {
			PlayerLogs.log(player.getUsername(), player.getUsername()+" was stuck in the obstacle pipe in the Wild.");
			player.moveTo(new Position(3006, player.getPosition().getY(), player.getPosition().getZ()));
			player.getPacketSender().sendMessage("You logged off inside the obstacle pipe, moved out.");
		}

		GlobalItemSpawner.spawnGlobalGroundItems(player);
		player.getPacketSender().sendString(57003, "Players:  @gre@"+(int)(World.getPlayers().size()));

		if(player.getAttendanceManager().isDifferentDay()) {
			player.getAttendanceManager().newDay();
		}

		player.getAchievementManger().sendAchievementData();
	}

	public static boolean handleLogout(Player player, Boolean forced) {
		try {
			PlayerSession session = player.getSession();
			
			if(session.getChannel().isOpen()) {
				session.getChannel().close();
			}

			if(!player.isRegistered()) {
				return true;
			}

			boolean exception = forced || GameServer.isUpdating() || AuthenticationService.queue.stream().anyMatch(it -> it instanceof Player p && p.equals(player)) && player.getLogoutTimer().elapsed(90000);
			if(player.logout() || exception) {
			//	System.out.println("[World] Deregistering player - [username, host] : [" + player.getUsername() + ", " + player.getHostAddress() + "]");
				player.getSession().setState(SessionState.LOGGING_OUT);
				ConnectionHandler.remove(player.getHostAddress());
				player.setTotalPlayTime(player.getTotalPlayTime() + player.getRecordedLogin().elapsed());
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getCannon() != null) {
					DwarfMultiCannon.pickupCannon(player, player.getCannon(), true);
				}
				if(exception && player.getResetPosition() != null) {
					player.moveTo(player.getResetPosition());
					player.setResetPosition(null);
				}
				if(player.getRegionInstance() != null) {
					player.getRegionInstance().destruct();
				}
				if(player.isShopping() && player.getShop() != null) {
					player.getShop().getPlayersCurrentlyViewing().remove(player);
				}
				player.getDungeonManager().leaveDungeon(true);
				Hunter.handleLogout(player);
				Locations.logout(player);
				player.getSummoning().unsummon(false, false);
				ClanChatManager.leave(player, false);
				player.getRelations().updateLists(false);
				PlayersOnlineInterface.remove(player);
				TaskManager.cancelTasks(player.getCombatBuilder());
				TaskManager.cancelTasks(player);
				player.save();
				World.getPlayers().remove(player);
				session.setState(SessionState.LOGGED_OUT);
				World.updatePlayersOnline();
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
