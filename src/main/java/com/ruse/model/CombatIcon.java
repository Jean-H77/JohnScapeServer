package com.ruse.model;

import com.ruse.world.content.combat.CombatType;

/**
 * Represents a damage's combat icon.
 * 
 * @author relex lawl
 */

public enum CombatIcon {
	
	/*
	 * No combat icon will be drawn.
	 */
	BLOCK,
	
	/*
	 * A sword icon will be drawn next to the hit.
	 */
	MELEE(CombatType.MELEE),
	
	/*
	 * A bow icon will be drawn next to the hit.
	 */
	RANGED(CombatType.RANGED),
	
	/*
	 * A magic hat will be drawn next to the hit.
	 */
	MAGIC(CombatType.MAGIC),
	
	/*
	 * An arrow-like object will be drawn next to the hit.
	 */
	DEFLECT,
	
	/*
	 * A cannon ball will be drawn next to the hit.
	 */
	CANNON(CombatType.RANGED),
	
	/*
	 * Blue shield combat icon
	 */
	BLUE_SHIELD,

	/*
	 * No combat icon
	 */
	NONE;


	/**
	 * Gets the CombatIcon object for said id, being compared
	 * to it's ordinal (so ORDER IS CRUCIAL).
	 * @param id	The ordinal index of the combat icon.
	 * @return		The CombatIcon who's ordinal equals id.
	 */
	public static CombatIcon forId(int id) {
		for (CombatIcon icon : CombatIcon.values()) {
			if (icon.getId() == id)
				return icon;
		}
		return CombatIcon.BLOCK;
	}

	/**
	 * The combat type associated with this combat icon.
	 */
	public final CombatType combatType;

	CombatIcon() {
		this.combatType = CombatType.NONE;
	}

	CombatIcon(CombatType combatType) {
		this.combatType = combatType;
	}

	/**
	 * Gets the id that will be sent to client for said CombatIcon.
	 * @return	The index that will be sent to client.
	 */
	public int getId() {
		return ordinal() - 1;
	}

	public CombatType getCombatType() {
		return combatType;
	}
}