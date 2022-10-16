package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.DamageDealer;
import com.ruse.model.Locations.Location;
import com.ruse.model.entity.character.npc.NpcItemDropping;
import com.ruse.world.World;
import com.ruse.world.content.KillsTracker;
import com.ruse.world.content.KillsTracker.KillsEntry;
import com.ruse.world.content.Wildywyrm;
import com.ruse.world.content.combat.dungeon.DungeonNPC;
import com.ruse.world.content.combat.strategy.impl.bosses.KalphiteQueen;
import com.ruse.world.content.combat.strategy.impl.bosses.Nex;
import com.ruse.model.entity.character.npc.NPC;
import com.ruse.model.entity.character.player.Player;

import java.util.Arrays;

/**
 * Represents an npc's death task, which handles everything
 * an npc does before and after their death animation (including it), 
 * such as dropping their drop table items.
 * 
 * @author relex lawl
 */

public class NPCDeathTask extends Task {

	/**
	 * The NPCDeathTask constructor.
	 * @param npc	The npc being killed.
	 */
	public NPCDeathTask(NPC npc) {
		super(2);
		this.npc = npc;
		this.ticks = 2;
	}

	/**
	 * The npc setting off the death task.
	 */
	private final NPC npc;

	/**
	 * The amount of ticks on the task.
	 */
	private int ticks = 2;

	/**
	 * The player who killed the NPC
	 */
	private Player killer = null;

	public static int[] GROUP_BOSSES = new int[] {39472};

	@SuppressWarnings("incomplete-switch")
	@Override
	public void execute() {
		try {
			npc.setEntityInteraction(null);
			switch (ticks) {
			case 2:
				npc.getMovementQueue().setLockMovement(true).reset();

				if(Arrays.stream(GROUP_BOSSES).anyMatch(id -> id == npc.getId())) {
					int i = 0;
					for(DamageDealer killer_ : npc.getCombatBuilder().getTopKillers(npc)) {
						if(i > 0) { // skip top dealer since top dealer gets loot later
							NpcItemDropping.dropItems(killer_.getPlayer(), npc);
						}
						i++;
					}
				}

				DamageDealer damageDealer = npc.getCombatBuilder().getTopDamageDealer(true, null);
				killer = damageDealer == null ? null : damageDealer.getPlayer();
				
				if(!(npc.getId() >= 6142 && npc.getId() <= 6145) && !(npc.getId() > 5070 && npc.getId() < 5081))
					npc.performAnimation(new Animation(npc.getDefinition().getDeathAnimation()));

				/** CUSTOM NPC DEATHS **/
				if(npc.getId() == 13447) {
					Nex.handleDeath();
				}

				break;
			case 0:
				if(killer != null) {

					boolean boss = (npc.getDefaultConstitution() > 2000);
					if(!Nex.nexMinion(npc.getId()) && npc.getId() != 1158 && !(npc.getId() >= 3493 && npc.getId() <= 3497)) {
						KillsTracker.submit(killer, new KillsEntry(npc.getDefinition().getName(), 1, boss));
						if(boss) {
							//Achievements.doProgress(killer, AchievementData.DEFEAT_500_BOSSES);
						}
					}
					/** LOCATION KILLS **/
					if(npc.getLocation().handleKilledNPC(killer, npc)) {
						stop();
						return;
					}
					/** PARSE DROPS **/
					NpcItemDropping.dropItems(killer, npc);
				}
				stop();
				break;
			}
			ticks--;
		} catch(Exception e) {
			e.printStackTrace();
			stop();
		}
	}

	@Override
	public void stop() {
		setEventRunning(false);

		npc.setDying(false);

		//respawn
		if(npc.getDefinition().getRespawnTime() > 0 && npc.getLocation() != Location.GRAVEYARD && npc.getLocation() != Location.DUNGEONEERING && !(npc instanceof DungeonNPC)) {
			TaskManager.submit(new NPCRespawnTask(npc, npc.getDefinition().getRespawnTime()));
		}

		World.deregister(npc);

		if(npc.getId() == 1158 || npc.getId() == 1160) {
			KalphiteQueen.death(npc.getId(), npc.getPosition());
		}
		if(Nex.nexMob(npc.getId())) {
			Nex.death(npc.getId());
		}
	}
}
