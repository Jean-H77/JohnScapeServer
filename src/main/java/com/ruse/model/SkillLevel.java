package com.ruse.model;

/**
 * A skill level.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class SkillLevel {

    private final Skill skill;
    private final int level;

    public SkillLevel(Skill skill, int level) {
        this.skill = skill;
        this.level = level;
    }

    @Override
    public String toString() {
        return "SkillLevel{" +
                "skill=" + skill +
                ", level=" + level +
                '}';
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }
}
