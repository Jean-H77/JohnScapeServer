package com.ruse.world.content.zombies;

import com.ruse.model.entity.character.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ZombiesParty {
    private final List<Player> player = new ArrayList<>();
    private int wave;
    private long elapsedTime;
    private int totalKills;

    public ZombiesParty() {
    }

    public List<Player> getPlayer() {
        return this.player;
    }

    public int getWave() {
        return this.wave;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public int getTotalKills() {
        return this.totalKills;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }
}
