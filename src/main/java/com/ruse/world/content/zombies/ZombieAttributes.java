package com.ruse.world.content.zombies;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZombieAttributes {
    private final ZombiesManager.ZombiesClass warrior = new ZombiesManager.ZombiesClass(ZombiesClassType.WARRIOR);
    private final ZombiesManager.ZombiesClass ranger = new ZombiesManager.ZombiesClass(ZombiesClassType.RANGER);
    private final ZombiesManager.ZombiesClass wizard = new ZombiesManager.ZombiesClass(ZombiesClassType.WIZARD);
    private ZombiesManager.ZombiesClass selectedClass;
    private int configState;
    private int bestWave;
    private int bestKc;
    private long bestTime;
    private int totalKills;
}
