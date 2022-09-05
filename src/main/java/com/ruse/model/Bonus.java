package com.ruse.model;

import com.ruse.world.content.BonusManager;

/**
 * Item bonuses.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public enum Bonus {
    STAB_ATTACK(BonusType.ATTACK, BonusManager.ATTACK_STAB),
    SLASH_ATTACK(BonusType.ATTACK, BonusManager.ATTACK_SLASH),
    CRUSH_ATTACK(BonusType.ATTACK, BonusManager.ATTACK_CRUSH),
    MAGIC_ATTACK(BonusType.ATTACK, BonusManager.ATTACK_MAGIC),
    RANGE_ATTACK(BonusType.ATTACK, BonusManager.ATTACK_RANGE),

    STAB_DEFENCE(BonusType.DEFENCE, BonusManager.DEFENCE_STAB),
    SLASH_DEFENCE(BonusType.DEFENCE, BonusManager.DEFENCE_SLASH),
    CRUSH_DEFENCE(BonusType.DEFENCE, BonusManager.DEFENCE_CRUSH),
    MAGIC_DEFENCE(BonusType.DEFENCE, BonusManager.DEFENCE_MAGIC),
    RANGE_DEFENCE(BonusType.DEFENCE, BonusManager.DEFENCE_RANGE),
    SUMMONING_DEFENCE(BonusType.DEFENCE, BonusManager.DEFENCE_SUMMONING),
    MELEE_ABSORB(BonusType.DEFENCE, BonusManager.ABSORB_MELEE),
    MAGIC_ABSORB(BonusType.DEFENCE, BonusManager.ABSORB_MAGIC),
    RANGED_ABSORB(BonusType.DEFENCE, BonusManager.ABSORB_RANGED),

    STRENGTH_MELEE(BonusType.OTHER, BonusManager.BONUS_STRENGTH),
    STRENGTH_RANGED(BonusType.OTHER, BonusManager.RANGED_STRENGTH),
    PRAYER(BonusType.OTHER, BonusManager.BONUS_PRAYER),
    MAGIC_DAMAGE_BOOST(BonusType.OTHER, BonusManager.MAGIC_DAMAGE),
    ;

    /**
     * The index this bonus is stored inside the bonus collection.
     */
    private final int bonusIndex;

    /**
     * The bonus type.
     */
    private final BonusType bonusType;

    Bonus(BonusType bonusCollection, int bonusIndex) {
        this.bonusIndex = bonusIndex;
        this.bonusType = bonusCollection;
    }


}
