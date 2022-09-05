package com.ruse.model;

import com.ruse.model.entity.character.CharacterEntity;

/**
 * A class that represents a hit inflicted on an entity.
 * 
 * @author lare96
 */
public class Hit {

    private final CharacterEntity attacker;

    /** The amount of damage inflicted in this hit. */
    private int damage;
    
    /** The amount of damage absorbed in this hit */
    private int absorb;

    private Hitmask hitmask;

    private CombatIcon combatIcon;

    private final boolean blocked;

    /**
     * Create a new {@link Hit}.
     * 
     * @param damage
     *            the amount of damage in this hit.
     * @param type
     *            the type of hit this is.
     */
    public Hit(CharacterEntity attacker, int damage, Hitmask hitmask, CombatIcon combatIcon) {
        this(attacker, damage, hitmask, combatIcon, false);
    }

    public Hit(CharacterEntity attacker, int damage, Hitmask hitmask, CombatIcon combatIcon, boolean blocked) {
        this.attacker = attacker;
        this.damage = damage;
        this.hitmask = hitmask;
        this.combatIcon = combatIcon;
        this.absorb = 0;
        this.blocked = blocked;
        this.modify();
    }

    /**
     * Create a new {@link Hit} with a default {@link Hitmask} of
     * <code>NORMAL</code>.
     * 
     * @param damage
     *            the amount of damage in this hit.
     */
    public Hit(int damage) {
        this(null, damage, Hitmask.RED, CombatIcon.MELEE);
    }

    @Override
    public Hit clone() {
        return new Hit(attacker, damage, hitmask, combatIcon);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Hit)) {
            return false;
        }

        Hit hit = (Hit) o;
        return (hit.damage == damage && hit.hitmask == hitmask && hit.combatIcon == combatIcon);
    }

    private void modify() {
        if (this.damage == 0 && this.combatIcon != CombatIcon.BLOCK) {
            this.combatIcon = CombatIcon.BLOCK;
        } else if (this.damage < 0) {
            this.damage = 0;
            this.combatIcon = CombatIcon.BLOCK;
        }
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
        this.modify();
    }
    
    public void incrementAbsorbedDamage(int absorb) {
    	this.damage -= absorb;
    	this.absorb += absorb;
    	this.modify();
    }

    public Hitmask getHitmask() {
        return hitmask;
    }

    public void setHitmask(Hitmask hitmask) {
        this.hitmask = hitmask;
    }
    
    public CombatIcon getCombatIcon() {
    	return combatIcon;
    }

    public void setCombatIcon(CombatIcon combatIcon) {
        this.combatIcon = combatIcon;
    }
    
    public int getAbsorb() {
    	return absorb;
    }
    
    public void setAbsorb(int absorb) {
    	this.absorb = absorb;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public CharacterEntity getAttacker() {
        return attacker;
    }
}
