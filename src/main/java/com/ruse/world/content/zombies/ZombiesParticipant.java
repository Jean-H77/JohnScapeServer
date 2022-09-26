package com.ruse.world.content.zombies;

import com.ruse.model.entity.character.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ZombiesParticipant {

    private final Player participant;
    private int kills;
    private int damageDone;

    private final ZombiesClass warrior = new ZombiesClass(ZombiesClassType.WARRIOR);
    private final ZombiesClass ranger = new ZombiesClass(ZombiesClassType.RANGER);
    private final ZombiesClass wizard = new ZombiesClass(ZombiesClassType.WIZARD);
    private ZombiesClass selectedClass;
    private int configState;
    private int bestWave;
    private int bestKc;
    private long bestTime;
    private int totalKills;

    public void incrementDamageByAmount(int amount) {
        damageDone+=amount;
    }

    public void incrementKills() {
        kills++;
    }

    @RequiredArgsConstructor
    static class ZombiesClass {
        private final ZombiesClassType classType;
        private int exp;
        private int level;

        public void incrementExpBy(int amount) {
            exp += amount;
        }

        public void checkForLevelUp() {
            if(level == 20) return;
            levelUp();
        }

        public void levelUp() {

        }

    }
}
