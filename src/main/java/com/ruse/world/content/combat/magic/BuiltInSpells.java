package com.ruse.world.content.combat.magic;

import java.util.EnumSet;
import java.util.Optional;

public enum BuiltInSpells {
    BEGINNER_WAND(6908, CombatSpells.FIRE_BOLT),
    PIRATES_STAFF(21580, CombatSpells.CRUMBLE_UNDEAD),
    DRAGON_STAFF(19323, CombatSpells.FIRE_WAVE),
    MYSTIC_STAFF(18341, CombatSpells.BLOOD_BURST),
    STARTER_STAFF(896, CombatSpells.BABY_SCORPION),
    STAFF_OF_THE_ANCIENTS(18342, CombatSpells.SHADOW_BARRAGE),
    FOREST_STAFF(899, CombatSpells.EARTH_WAVE);

    BuiltInSpells(int staff_id, CombatSpells spell) {
        this.staffId = staff_id;
        this.spell = spell;
    }

    public int getStaffId() {
        return staffId;
    }

    private final int staffId;

    public CombatSpells getSpell() {
        return spell;
    }

    private final CombatSpells spell;

    private static final EnumSet<BuiltInSpells> set = EnumSet.allOf(BuiltInSpells.class);;

    public static CombatSpells getSpell(int id) {
        Optional<BuiltInSpells> spell;
        spell = set.stream().filter(staff -> staff.staffId == id).findFirst();

        return spell.map(BuiltInSpells::getSpell).orElse(null);
    }

    public static boolean hasStaff(int id) {
        return set.stream().anyMatch(wep -> wep.staffId == id);
    }
}
