package com.ruse.model.entity.character.npc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruse.GameSettings;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.NPCDeathTask;
import com.ruse.model.DamageDealer;
import com.ruse.model.Direction;
import com.ruse.model.Flag;
import com.ruse.model.Hit;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.json.JsonLoader;
import com.ruse.world.World;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.effect.CombatPoisonEffect.PoisonType;
import com.ruse.world.content.combat.strategy.CombatStrategies;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.content.combat.strategy.impl.bosses.KalphiteQueen;
import com.ruse.world.content.combat.strategy.impl.bosses.Nex;
import com.ruse.world.content.skill.hunter.Hunter;
import com.ruse.world.content.skill.hunter.PuroPuro;
import com.ruse.world.content.skill.runecrafting.DesoSpan;
import com.ruse.model.entity.character.CharacterEntity;
import com.ruse.model.entity.character.npc.NPCMovementCoordinator.Coordinator;
import com.ruse.model.entity.character.player.Player;

/**
 * Represents a non-playable character, which players can interact with.
 * @author Gabriel Hannason
 */

public class NPC extends CharacterEntity {

	/**
	 * Prepares the dynamic json loader for loading world npcs.
	 *
	 * @return the dynamic json loader.
	 * @throws Exception
	 *             if any errors occur while preparing for load.
	 */
	public static void init() {
		File directory = new File(GameSettings.DEFINITION_DIRECTORY + "npc_spawns/");
		if (directory.exists()) {
			File[] list = directory.listFiles();
			if (list != null) {
				Arrays.stream(list).forEach(file -> getLoader(file.getPath()).load());
			}
		}

		getLoader(GameSettings.DEFINITION_DIRECTORY + "world_npcs.json").load();
		Nex.spawn();
		PuroPuro.spawn();
		DesoSpan.spawn();
		KalphiteQueen.spawn(1158, new Position(3485, 9509));
	}

	private static JsonLoader getLoader(String path) {
		return new JsonLoader() {
			@Override
			public void load(JsonObject reader, Gson builder) {
				int id = reader.get("npc-id").getAsInt();

				Coordinator coordinator = builder.fromJson(reader.get("walking-policy").getAsJsonObject(), Coordinator.class);
				Direction direction = null;
				if (reader.get("face") != null) {
					direction = Direction.valueOf(reader.get("face").getAsString());
				}
				if(id==7891) {
					direction = Direction.EAST;
				}
				if(id > 30_000) {
					return;
				}
				ArrayList<NPC> spawned = Lists.newArrayList();

				if (reader.has("spawns")) {
					Position[] positions = builder.fromJson(reader.get("spawns").getAsJsonArray(), Position[].class);
					Arrays.stream(positions).forEach(position -> spawned.add(new NPC(id, position)));
				} else {
					Position position = builder.fromJson(reader.get("position").getAsJsonObject(), Position.class);
					spawned.add(new NPC(id, position));
				}

				for (NPC npc : spawned) {
					npc.movementCoordinator.setCoordinator(coordinator);
					npc.setDirection(direction);
					World.register(npc);
					if (id > 5070 && id < 5081) {
						Hunter.HUNTER_NPC_LIST.add(npc);
					}

					CombatStrategy strategy = npc.determineStrategy();
					if (strategy != null) {
						strategy.respawned(npc);
					}
				}
			}

			@Override
			public String filePath() {
				return path;
			}
		};
	}


	private final Position defaultPosition;
	private NPCMovementCoordinator movementCoordinator = new NPCMovementCoordinator(this);
	private Player spawnedFor;
	private NpcDefinition definition;
	private List<DamageDealer> damageDealerMap = new ArrayList<DamageDealer>();
	private final int id;
	private int constitution = 100;
	private int defaultConstitution;
	private int transformationId = -1;
	private boolean[] attackWeakened = new boolean[3], strengthWeakened = new boolean[3];
	private boolean summoningNpc, summoningCombat;
	private boolean isDying;
	private boolean visible = true;
	private boolean healed, chargingAttack;
	private boolean findNewTarget;
	private boolean fetchNewDamageMap;

	public NPC(int id, Position position) {
		super(position);
		NpcDefinition definition = NpcDefinition.forId(id);
		if(definition == null)
			throw new NullPointerException("NPC "+id+" is not defined!");
		this.defaultPosition = position;
		this.id = id;
		this.definition = definition;
		this.defaultConstitution = definition.getHitpoints() < 100 ? 100 : definition.getHitpoints();
		this.constitution = defaultConstitution;
		setLocation(Location.getLocation(this));

		if(id == 7891) {
			this.getMovementQueue().setLockMovement(true);
		}
	}

	public void sequence() {
		getCombatBuilder().process();

		// Hp restore
		if(constitution < defaultConstitution) {
			if(!isDying) {
				if(getLastCombat().elapsed((id == 2042 || id == 2043 || id == 2044 || id == 13447 || id == 3200 ? 50000 : 5000))
						&& !getCombatBuilder().isAttacking()
						&& getLocation() != Location.PEST_CONTROL_GAME
						&& getLocation() != Location.DUNGEONEERING
						&& getLocation() != Location.ZULRAH) {
					setConstitution(constitution + (int) (defaultConstitution * 0.1));
					if(constitution > defaultConstitution) {
						setConstitution(defaultConstitution);
					}
				}
			}
		}
	}

	@Override
	public void appendDeath() {
		if(!isDying && !summoningNpc && id != 7891) {
			TaskManager.submit(new NPCDeathTask(this));
			isDying = true;
		}
	}

	@Override
	public int getConstitution() {
		return constitution;
	}

	@Override
	public NPC setConstitution(int constitution) {
		if(this.getId() == 7891) {
			return this;
		}
		this.constitution = constitution;
		if(this.constitution <= 0)
			appendDeath();
		return this;
	}

	@Override
	public void heal(int heal) {
		if ((this.constitution + heal) > getDefaultConstitution()) {
			setConstitution(getDefaultConstitution());
			return;
		}
		setConstitution(this.constitution + heal);
	}


	@Override
	public int getBaseAttack(CombatType type) {
		return getDefinition().getAttackBonus();
	}

	@Override
	public int getAttackSpeed() {
		return this.getDefinition().getAttackSpeed();
	}

	@Override
	public void damaged(Hit hit) {
		CombatStrategy strategy = determineStrategy();
		if (strategy != null) {
			strategy.damaged(this, hit);
		}
	}

	@Override
	public void attacked(CharacterEntity victim, Hit hit) {
		CombatStrategy strategy = determineStrategy();
		if (strategy != null) {
			strategy.attacked(this, victim, hit);
		}
	}

	@Override
	public void modifyHit(Hit hit) {
		CombatStrategy strategy = determineStrategy();
		if (strategy != null) {
			strategy.modifyHit(this, hit);
		}
	}

	@Override
	public void respawned() {
		CombatStrategy strategy = determineStrategy();
		if (strategy != null) {
			strategy.respawned(this);
		}
	}

	@Override
	public int getBaseDefence(CombatType type) {
		if (type == CombatType.MAGIC)
			return getDefinition().getDefenceMage();
		else if (type == CombatType.RANGED)
			return getDefinition().getDefenceRange();

		return getDefinition().getDefenceMelee();
	}

	@Override
	public boolean isNpc() {
		return true;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof NPC && ((NPC)other).getIndex() == getIndex();
	}

	@Override
	public int getSize() {
		return getDefinition().getSize();
	}

	@Override
	public void poisonVictim(CharacterEntity victim, CombatType type) {
		if (getDefinition().isPoisonous()) {
			CombatFactory.poisonEntity(
					victim,
					type == CombatType.RANGED || type == CombatType.MAGIC ? PoisonType.MILD
							: PoisonType.EXTRA);
		}

	}

	@Override
	public CombatStrategy determineStrategy() {
		return CombatStrategies.getStrategy(id);
	}

	public boolean switchesVictim() {
		if(getLocation() == Location.DUNGEONEERING) {
			return true;
		}
		return id == 6263 || id == 6265 || id == 6203 || id == 6208 || id == 6206 || id == 6247 || id == 6250 || id == 3200
				|| id == 4540 || id == 1158 || id == 1160 || id == 8133 || id == 13447 || id == 13451 || id == 13452
				|| id == 13453 || id == 13454 || id == 2896 || id == 2882 || id == 2881 || id == 6260 || id == 109
				|| id == 2001 || id == 2006 || id == 2009 || id == 2000;
	}

	public int getAggressionDistance() {
		int distance = 7;
		
		switch(id) {
		case 2030:
		case 2029:
		case 2028:
		case 2027:
		case 2026:
		case 2025:
			return 15;
		}
		if(Nex.nexMob(id)) {
			distance = 60;
		} else if(id == 2896) {
			distance = 50;
		}
		return distance;
	}

	/**
	 * Gets the current id, if the npc was transformed at some point this will reflect that.
	 * @return the real npc id
	 */
	public int getCurrentNpcId() {
		return transformationId == -1 ? id : transformationId;
	}

	public void transform(int npcId) {
		transformationId = npcId;
		getUpdateFlag().flag(Flag.TRANSFORM);
	}

	public int getId() {
		return id;
	}

	public Position getDefaultPosition() {
		return defaultPosition;
	}

	public int getDefaultConstitution() {
		return defaultConstitution;
	}

	public int getTransformationId() {
		return transformationId;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setDying(boolean isDying) {
		this.isDying = isDying;
	}
	
	public void setDefaultConstitution(int defaultConstitution) {
		this.defaultConstitution = defaultConstitution;
	}

	/**
	 * @return the statsWeakened
	 */
	public boolean[] getDefenceWeakened() {
		return attackWeakened;
	}

	public void setSummoningNpc(boolean summoningNpc) {
		this.summoningNpc = summoningNpc;
	}

	public boolean isSummoningNpc() {
		return summoningNpc;
	}

	public boolean isDying() {
		return isDying;
	}

	/**
	 * @return the statsBadlyWeakened
	 */
	public boolean[] getStrengthWeakened() {
		return strengthWeakened;
	}

	public NPCMovementCoordinator getMovementCoordinator() {
		return movementCoordinator;
	}

	public NpcDefinition getDefinition() {
		return definition;
	}

	public Player getSpawnedFor() {
		return spawnedFor;
	}

	public NPC setSpawnedFor(Player spawnedFor) {
		this.spawnedFor = spawnedFor;
		return this;
	}

	public boolean hasHealed() {
		return healed;
	}

	public void setHealed(boolean healed) {
		this.healed = healed;
	}

	public boolean isChargingAttack() {
		return chargingAttack;
	}

	public NPC setChargingAttack(boolean chargingAttack) {
		this.chargingAttack = chargingAttack;
		return this;
	}
	
	public boolean findNewTarget() {
		return findNewTarget;
	}
	
	public void setFindNewTarget(boolean findNewTarget) {
		this.findNewTarget = findNewTarget;
	}
	
	public boolean summoningCombat() {
		return summoningCombat;
	}
	
	public void setSummoningCombat(boolean summoningCombat) {
		this.summoningCombat = summoningCombat;
	}
	
	public void setFetchNewDamageMap(boolean fetchNewDamageMap) {
		this.fetchNewDamageMap = fetchNewDamageMap;
	}
	
	public boolean fetchNewDamageMap() {
		return fetchNewDamageMap;
	}
	
	public List<DamageDealer> getDamageDealerMap() {
		return damageDealerMap;
	}
	
	public void setDamageDealerMap(List<DamageDealer> damageDealerMap) {
		this.damageDealerMap = damageDealerMap;
	}
}
