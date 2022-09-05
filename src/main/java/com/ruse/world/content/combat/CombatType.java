package com.ruse.world.content.combat;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A set of constants representing the three different types of combat that can
 * be used.
 * 
 * @author lare96
 */
public enum CombatType {
    MELEE(1),
    RANGED(8),
    MAGIC(10),
    DRAGON_FIRE,
    MIXED,
    NONE
    ;

    public static final List<CombatType> COMBAT_TRIANGLE = Collections.unmodifiableList(Lists.newArrayList(CombatType.MELEE, CombatType.RANGED, CombatType.MAGIC));

    private final int attackDistance;

    CombatType() {
        this(1);
    }

    CombatType(int attackDistance) {
        this.attackDistance = attackDistance;
    }

    public int getAttackDistance() {
        return attackDistance;
    }
}