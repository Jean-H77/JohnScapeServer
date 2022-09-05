package com.ruse.world.content.combat.strategy;

import com.ruse.model.Hit;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.model.entity.Entity;
import com.ruse.model.entity.character.CharacterEntity;
import com.ruse.model.entity.character.player.Player;

/**
 * A container used to determine how an entity will act during a combat session.
 * New combat strategies do not need to be made for {@link Player}s because
 * everything is handled for them in the three default factory strategy classes.
 * 
 * @author lare96
 */
public interface CombatStrategy {

    /**
     * Determines if the attacking {@link Entity} is able to make an attack.
     * Used for miscellaneous checks such as runes, arrows, target, etc.
     * 
     * @param entity
     *            the entity to check.
     * @return <code>true</code> if the attack can be successfully made,
     *         <code>false</code> if it cannot.
     */
    boolean canAttack(CharacterEntity entity, CharacterEntity victim);

    /**
     * Fired when the attacking {@link Entity} has passed the
     * <code>canAttack(Entity e1, Entity e2)</code> check and is ready to
     * attack.
     * 
     * @param entity
     *            the attacking entity in this combat hook.
     * @param victim
     *            the defending entity in this combat hook.
     * @return the combat container that will be used to deal damage to the
     *         victim during this hook.
     */
    CombatContainer attack(CharacterEntity entity, CharacterEntity victim);

    boolean customContainerAttack(CharacterEntity entity, CharacterEntity victim);
    
    /**
     * How long the attacking {@link Entity} must wait in intervals to attack.
     * 
     * @param entity
     *            the attacking entity in this combat hook.
     * @return the amount of time that the attacker must wait.
     */
    int attackDelay(CharacterEntity entity);

    /**
     * How close the attacking {@link Entity} must be to attack the victim.
     * 
     * @param entity
     *            the attacking entity in this combat hook.
     * @return the radius that the attacker has to be within in order to attack.
     */
    int attackDistance(CharacterEntity entity);

    CombatType getCombatType(CharacterEntity entity);

    /**
     * Entity was damaged.
     * @param hit the damage
     */
    default void damaged(CharacterEntity entity, Hit hit) {}

    /**
     * This entity attacked another entity
     */
    default void attacked(CharacterEntity entity, CharacterEntity victim, Hit hit) {}

    /**
     *  Modify a hit.
     * @param entity the entity that has the CombatStrategy
     */
    default void modifyHit(CharacterEntity entity, Hit hit) {}

    /**
     * Entity respawned
     * @param entity the entity
     */
    default void respawned(CharacterEntity entity) {

    }
}
