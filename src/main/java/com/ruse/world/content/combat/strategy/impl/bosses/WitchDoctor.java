package com.ruse.world.content.combat.strategy.impl.bosses;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.model.entity.character.CharacterEntity;
import com.ruse.model.entity.character.npc.NPC;
import com.ruse.model.entity.character.player.Player;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.HitQueue;
import com.ruse.world.content.combat.strategy.CombatStrategy;

public class WitchDoctor implements CombatStrategy {

    private static final Animation throwAnim = new Animation(2614);
    @Override
    public boolean canAttack(CharacterEntity entity, CharacterEntity victim) {
        return true;
    }

    @Override
    public CombatContainer attack(CharacterEntity entity, CharacterEntity victim) {
        return null;
    }

    @Override
    public boolean customContainerAttack(CharacterEntity entity, CharacterEntity victim) {
        NPC npc = (NPC)entity;
        if(npc.isChargingAttack() || npc.getConstitution() <= 0) {
            return true;
        }
        Player target = (Player)victim;
        npc.performAnimation(throwAnim);

        for (Player t : Misc.getCombinedPlayerList(target)) {
            if(t == null)
                continue;
            if (Locations.goodDistance(t.getPosition(), npc.getPosition(), 6)) {
                new Projectile(npc, t, 192, 44, 6, 43, 43, 0).sendProjectile();
                new HitQueue.CombatHit(npc.getCombatBuilder(), new CombatContainer(npc, t, 1, 2, CombatType.MAGIC, true)).handleAttack();
                TaskManager.submit(new Task(1) {
                    @Override
                    protected void execute() {
                        t.performGraphic(new Graphic(346));
                        stop();
                    }
                });
            }
        }
        return true;
    }

    @Override
    public int attackDelay(CharacterEntity entity) {
        return 5;
    }

    @Override
    public int attackDistance(CharacterEntity entity) {
        return 6;
    }

    @Override
    public CombatType getCombatType(CharacterEntity entity) {
        return CombatType.MAGIC;
    }
}
