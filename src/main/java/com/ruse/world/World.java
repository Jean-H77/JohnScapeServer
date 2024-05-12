package com.ruse.world;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruse.GameSettings;
import com.ruse.eventbus.impl.EndCycleEvent;
import com.ruse.eventbus.impl.player.PlayerRegisterEvent;
import com.ruse.eventbus.impl.player.PlayerRegisterRequest;
import com.ruse.model.Item;
import com.ruse.model.MessageType;
import com.ruse.model.PlayerRights;
import com.ruse.model.entity.Entity;
import com.ruse.model.entity.EntityHandler;
import com.ruse.model.entity.character.CharacterList;
import com.ruse.model.entity.character.GlobalItemSpawner;
import com.ruse.model.entity.character.npc.NPC;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.entity.character.player.PlayerHandler;
import com.ruse.model.entity.character.updating.NpcUpdateSequence;
import com.ruse.model.entity.character.updating.PlayerUpdateSequence;
import com.ruse.model.entity.character.updating.UpdateSequence;
import com.ruse.util.Misc;
import com.ruse.util.json.ItemTypeAdapter;
import com.ruse.world.content.wogw.WellOfGoodwill;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
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

	public static Deque<Player> loginQueue = new ConcurrentLinkedDeque<>();

	public static Deque<Player> logoutQueue = new ConcurrentLinkedDeque<>();

	public static final ExecutorService fileIOExecutor = Executors.newFixedThreadPool(1);

	private static final int LOGIN_LIMITER = 25;

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
		players.forEach(p -> p.getPacketSender().sendMessage(message));
	}

	public static void sendMessage(MessageType type, String message) {
		players.forEach(p -> p.getPacketSender().sendMessage(type, message));
	}

	public static void sendFilteredMessage(String message) {
		players.stream().filter(p -> p != null && (!p.toggledGlobalMessages())).forEach(p -> p.getPacketSender().sendMessage(message));
	}

	public static void sendStaffMessage(String message) {
		players.stream().filter(p -> p != null && (p.getRights().isStaff())).forEach(p -> p.getPacketSender().sendMessage(message)); // == PlayerRights.OWNER || p.getRights() == PlayerRights.DEVELOPER || p.getRights() == PlayerRights.ADMINISTRATOR || p.getRights() == PlayerRights.MODERATOR || p.getRights() == PlayerRights.SUPPORT)).forEach(p -> p.getPacketSender().sendMessage(message));
	}
	
	public static void sendOwnerDevMessage(String message) {
		players.stream().filter(p -> p != null && (p.getRights() == PlayerRights.OWNER || p.getRights() == PlayerRights.DEVELOPER)).forEach(p -> p.getPacketSender().sendMessage(message));
	}
	
	public static void sendGlobalGroundItems() {
		players.stream().filter(Objects::nonNull).forEach(GlobalItemSpawner::spawnGlobalGroundItems);
	} 

	public static void updatePlayersOnline() {
		players.forEach(p -> p.getPacketSender().sendString(39160, "@or2@Players online:   @or2@[ @yel@"+ players.size() +"@or2@ ]"));
		players.forEach(p -> p.getPacketSender().sendString(57003, "Players:  @gre@"+ World.getPlayers().size()));
	}

	public static void savePlayers() {
		players.forEach(Player::save);
	}

	public static CharacterList<Player> getPlayers() {
		return players;
	}

	public static CharacterList<NPC> getNpcs() {
		return npcs;
	}

	public static void save() {
		Thread.startVirtualThread(() -> {
			Path path = Paths.get("./data/saves/world/world.json");
			File file = path.toFile();
			file.getParentFile().setWritable(true);

			if (!file.getParentFile().exists()) {
				try {
					file.getParentFile().mkdirs();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}

			try (FileWriter writer = new FileWriter(file)) {
				JsonObject object = new JsonObject();
				Gson builder = new GsonBuilder()
						.setPrettyPrinting()
						.create();

				object.addProperty("wogw", WellOfGoodwill.contributedAmount);

				writer.write(builder.toJson(object));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void load() {
		Path path = Paths.get("./data/saves/world/world.json");
		File file = path.toFile();

		if(!file.exists()) {
			return;
		}

		try (FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			JsonObject reader = (JsonObject) fileParser.parse(fileReader);

			if (reader.has("wogw")) {
				WellOfGoodwill.contributedAmount = reader.get("wogw").getAsInt();

				if(WellOfGoodwill.contributedAmount > 0) {
					WellOfGoodwill.startDepleteTask();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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

		for(int i = 0; i < LOGIN_LIMITER; i++) {
			if(loginQueue.isEmpty() && logoutQueue.isEmpty()) {
				break;
			}

			if(!loginQueue.isEmpty()) {
				Player player = loginQueue.pop();
				PlayerHandler.handleLogin(player);
			}

			if(!logoutQueue.isEmpty()) {
				Player player = logoutQueue.pop();
				PlayerHandler.handleLogout(player, false);
			}
		}
	}
}
