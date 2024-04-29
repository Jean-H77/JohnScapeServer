package com.ruse.world;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ruse.GameSettings;
import com.ruse.model.MessageType;
import com.ruse.model.PlayerRights;
import com.ruse.model.entity.Entity;
import com.ruse.model.entity.EntityHandler;
import com.ruse.model.entity.character.CharacterList;
import com.ruse.model.entity.character.GlobalItemSpawner;
import com.ruse.model.entity.character.npc.NPC;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.entity.character.updating.NpcUpdateSequence;
import com.ruse.model.entity.character.updating.PlayerUpdateSequence;
import com.ruse.model.entity.character.updating.UpdateSequence;
import com.ruse.util.Misc;
import com.ruse.webhooks.discord.DiscordMessager;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class World {

	/** All of the registered players. */
	private static CharacterList<Player> players = new CharacterList<>(GameSettings.playerCharacterListCapacity);

	/** All of the registered NPCs. */
	private static CharacterList<NPC> npcs = new CharacterList<>(GameSettings.npcCharacterListCapacity);

	/** Used to block the game thread until updating has completed. */
	private static Phaser synchronizer = new Phaser(1);

	/** A thread pool that will update players in parallel. */
	private static ExecutorService updateExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("UpdateThread").setPriority(Thread.MAX_PRIORITY).build());

    public static void register(Entity entity) {
		EntityHandler.register(entity);
	}

	public static void deregister(Entity entity) {
		EntityHandler.deregister(entity);
	}

	public static Player getPlayerByName(String username) {
		Optional<Player> op = players.search(p -> p != null && p.getUsername().equals(Misc.formatText(username)));
		return op.orElse(null);
	}

	public static Player getPlayerByLong(long encodedName) {
		Optional<Player> op = players.search(p -> p != null && p.getLongUsername().equals(encodedName));
		return op.orElse(null);
	}

	public static void sendMessage(String message) {
		if (message.contains("[Yell]")) {
			DiscordMessager.sendYellMessage(message);
		} else if (message.contains("10 more players have just voted")){
			DiscordMessager.sendInGameMessage("10 more players have just voted.");
		} else {
			DiscordMessager.sendInGameMessage(message);
		}
		players.forEach(p -> p.getPacketSender().sendMessage(message));
	}

	public static void sendMessage(MessageType type, String message) {
		players.forEach(p -> p.getPacketSender().sendMessage(type, message));
		if (message.contains("[Yell]")) {
			DiscordMessager.sendYellMessage(message);
		} else if (message.contains("logged in for the first time")) {
			DiscordMessager.sendStaffMessage(message);
		} else {
			DiscordMessager.sendInGameMessage(message);
		}
	}

	public static void sendFilteredMessage(String message) {
		players.stream().filter(p -> p != null && (!p.toggledGlobalMessages())).forEach(p -> p.getPacketSender().sendMessage(message));
	}

	public static void sendStaffMessage(String message) {
		players.stream().filter(p -> p != null && (p.getRights().isStaff())).forEach(p -> p.getPacketSender().sendMessage(message)); // == PlayerRights.OWNER || p.getRights() == PlayerRights.DEVELOPER || p.getRights() == PlayerRights.ADMINISTRATOR || p.getRights() == PlayerRights.MODERATOR || p.getRights() == PlayerRights.SUPPORT)).forEach(p -> p.getPacketSender().sendMessage(message));
		DiscordMessager.sendStaffMessage(message);
	}
	
	public static void sendOwnerDevMessage(String message) {
		players.stream().filter(p -> p != null && (p.getRights() == PlayerRights.OWNER || p.getRights() == PlayerRights.DEVELOPER)).forEach(p -> p.getPacketSender().sendMessage(message));
		DiscordMessager.sendDebugMessage("[Owner/Developer]\n"+message);
	}
	
	public static void sendGlobalGroundItems() {
		players.stream().filter(Objects::nonNull).forEach(GlobalItemSpawner::spawnGlobalGroundItems);
	} 

	public static void updatePlayersOnline() {
		players.forEach(p -> p.getPacketSender().sendString(39160, "@or2@Players online:   @or2@[ @yel@"+ players.size() +"@or2@ ]"));
		players.forEach(p -> p.getPacketSender().sendString(57003, "Players:  @gre@"+ World.getPlayers().size()));
	}

	public static void savePlayers() {
		Thread.startVirtualThread(() -> players.forEach(Player::save));
	}

	public static CharacterList<Player> getPlayers() {
		return players;
	}

	public static CharacterList<NPC> getNpcs() {
		return npcs;
	}
	
	public static void sequence() {
		UpdateSequence<Player> playerUpdate = new PlayerUpdateSequence(synchronizer, updateExecutor);
		UpdateSequence<NPC> npcUpdate = new NpcUpdateSequence();
		players.forEach(playerUpdate::executePreUpdate);
		npcs.forEach(npcUpdate::executePreUpdate);
		synchronizer.bulkRegister(players.size());
		players.forEach(playerUpdate::executeUpdate);
		synchronizer.arriveAndAwaitAdvance();
		players.forEach(playerUpdate::executePostUpdate);
		npcs.forEach(npcUpdate::executePostUpdate);
	}
}
