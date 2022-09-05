package com.ruse.world.content.combat.strategy.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.ruse.model.Animation;
import com.ruse.model.Hit;
import com.ruse.model.Projectile;
import com.ruse.model.entity.character.CharacterEntity;
import com.ruse.model.entity.character.npc.NPC;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;

public class DemonicGorilla implements CombatStrategy {

    public static final int ID = 37144;
    private static final int MELEE_PROTECT_ID = ID;
    private static final int RANGE_PROTECT_ID = 37145;
    private static final int MAGIC_PROTECT_ID = 37146;
    private static final int[] STATES = {MELEE_PROTECT_ID, RANGE_PROTECT_ID, MAGIC_PROTECT_ID};

    public static final String COMBAT_STYLE_ATTRIBUTE_KEY = "demonic_gorilla_combat_style";
    public static final String DAMAGE_ATTRIBUTE_KEY = "demonic_gorilla_damaged";
    public static final String MISSED_ATTACK_ATTRIBUTE_KEY = "demonic_gorilla_missed_attacks";

    @Override
    public boolean canAttack(CharacterEntity entity, CharacterEntity victim) {
        return true;
    }

    @Override
    public CombatContainer attack(CharacterEntity entity, CharacterEntity victim) {
        if (!entity.getAttributes().contains(COMBAT_STYLE_ATTRIBUTE_KEY)) {
            switchStyle(entity);
        }

        switch ((CombatType) entity.getAttributes().get(COMBAT_STYLE_ATTRIBUTE_KEY)) {
            case MELEE:
                entity.performAnimation(new Animation(37226));
                return new CombatContainer(entity, victim, 1, 1, CombatType.MELEE, true, 300);
            case MAGIC:
                entity.performAnimation(new Animation(37238));
                Projectile.getStandard(entity, victim, 31305).sendProjectile();
                return new CombatContainer(entity, victim, 1, 3, CombatType.MAGIC, true, 300);
            case RANGED:
                entity.performAnimation(new Animation(37227));
                Projectile.getStandard(entity, victim, 31303).sendProjectile();
                return new CombatContainer(entity, victim, 1, 3, CombatType.RANGED, true, 300);
            default:
                throw new IllegalStateException();
        }
    }

    private void switchStyle(CharacterEntity entity, CombatType...filter) {
        if (filter.length > 0) {
            entity.getAttributes().set(COMBAT_STYLE_ATTRIBUTE_KEY, Misc.randomElement(
                    CombatType.COMBAT_TRIANGLE.stream().filter(type -> Arrays.stream(filter).anyMatch(type2 -> type2 != type)).collect(Collectors.toList())
            ));
        } else {
            entity.getAttributes().set(COMBAT_STYLE_ATTRIBUTE_KEY, Misc.randomElement(CombatType.COMBAT_TRIANGLE));
        }
    }

    @Override
    public void attacked(CharacterEntity entity, CharacterEntity victim, Hit hit) {
        if (hit.isBlocked() || hit.getDamage() == 0) {
            int missed = entity.getAttributes().setInt(MISSED_ATTACK_ATTRIBUTE_KEY, 1 + entity.getAttributes().getInt(MISSED_ATTACK_ATTRIBUTE_KEY, 0));
            if (missed >= 3) {
                switchStyle(entity, (CombatType) entity.getAttributes().get(COMBAT_STYLE_ATTRIBUTE_KEY));
                entity.getAttributes().removeInt(MISSED_ATTACK_ATTRIBUTE_KEY);
            }
        }
    }

    @Override
    public void damaged(CharacterEntity entity, Hit hit) {
        int damage = entity.getAttributes().setInt(DAMAGE_ATTRIBUTE_KEY,hit.getDamage() + entity.getAttributes().getInt(DAMAGE_ATTRIBUTE_KEY, 0));
        if (damage >= 500) {
            entity.getAttributes().removeInt(DAMAGE_ATTRIBUTE_KEY);
            NPC npc = entity.toNpc();
            switch (hit.getCombatIcon().getCombatType()) {
                case MELEE:
                    npc.transform(MELEE_PROTECT_ID);
                    break;
                case MAGIC:
                    npc.transform(MAGIC_PROTECT_ID);
                    break;
                case RANGED:
                    npc.transform(RANGE_PROTECT_ID);
                    break;
            }
        }
    }

    @Override
    public void modifyHit(CharacterEntity entity, Hit hit) {
        NPC npc = entity.toNpc();
        switch (npc.getCurrentNpcId()) {
            case MELEE_PROTECT_ID:
                if (hit.getCombatIcon().getCombatType() == CombatType.MELEE) {
                    hit.setDamage(0);
                }
                break;
            case RANGE_PROTECT_ID:
                if (hit.getCombatIcon().getCombatType() == CombatType.RANGED) {
                    hit.setDamage(0);
                }
                break;
            case MAGIC_PROTECT_ID:
                if (hit.getCombatIcon().getCombatType() == CombatType.MAGIC) {
                    hit.setDamage(0);
                }
                break;
        }
    }

    @Override
    public void respawned(CharacterEntity entity) {
        entity.toNpc().transform(Misc.randomElement(STATES));
    }

    @Override
    public boolean customContainerAttack(CharacterEntity entity, CharacterEntity victim) {
        return true;
    }

    @Override
    public int attackDelay(CharacterEntity entity) {
        return 5;
    }

    @Override
    public int attackDistance(CharacterEntity entity) {
        if (entity.getAttributes().contains(COMBAT_STYLE_ATTRIBUTE_KEY)) {
            return ((CombatType) entity.getAttributes().get(COMBAT_STYLE_ATTRIBUTE_KEY)).getAttackDistance();
        }
        return 8;
    }

    @Override
    public CombatType getCombatType(CharacterEntity entity) {
        if (entity.getAttributes().contains(COMBAT_STYLE_ATTRIBUTE_KEY)) {
            return (CombatType) entity.getAttributes().get(COMBAT_STYLE_ATTRIBUTE_KEY);
        }
        return CombatType.MIXED;
    }
}
