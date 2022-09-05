package com.ruse.world.content.combat.strategy.impl.bosses.gwd;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Projectile;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.model.entity.character.CharacterEntity;
import com.ruse.model.entity.character.npc.NPC;
import com.ruse.model.entity.character.player.Player;

public class Steelwill implements CombatStrategy {

	@Override
	public boolean canAttack(CharacterEntity entity, CharacterEntity victim) {
		return victim.isPlayer() && ((Player)victim).getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom();
	}

	@Override
	public CombatContainer attack(CharacterEntity entity, CharacterEntity victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(CharacterEntity entity, CharacterEntity victim) {
		NPC steelwill = (NPC)entity;
		
		if(victim.getConstitution() <= 0) {
			return true;
		}
		if(steelwill.isChargingAttack()) {
			return true;
		}
		
		steelwill.performAnimation(new Animation(steelwill.getDefinition().getAttackAnimation()));
		steelwill.performGraphic(new Graphic(1202));
		steelwill.setChargingAttack(true);

		steelwill.getCombatBuilder().setContainer(new CombatContainer(steelwill, victim, 1, 3, CombatType.MAGIC, true));
		
		TaskManager.submit(new Task(1, steelwill, false) {
			int tick = 0;
			@Override
			public void execute() {
				if(tick == 1) {
					new Projectile(steelwill, victim, 1203, 44, 3, 43, 43, 0).sendProjectile();
					steelwill.setChargingAttack(false);
					stop();
				}
				tick++;
			}
		});
		return true;
	}

	@Override
	public int attackDelay(CharacterEntity entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(CharacterEntity entity) {
		return 8;
	}

	@Override
	public CombatType getCombatType(CharacterEntity entity) {
		return CombatType.MAGIC;
	}
}
