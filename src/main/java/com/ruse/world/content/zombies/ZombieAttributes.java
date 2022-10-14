package com.ruse.world.content.zombies;

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

    public ZombiesManager.ZombiesClass getWarrior() {
        return this.warrior;
    }

    public ZombiesManager.ZombiesClass getRanger() {
        return this.ranger;
    }

    public ZombiesManager.ZombiesClass getWizard() {
        return this.wizard;
    }

    public ZombiesManager.ZombiesClass getSelectedClass() {
        return this.selectedClass;
    }

    public int getConfigState() {
        return this.configState;
    }

    public int getBestWave() {
        return this.bestWave;
    }

    public int getBestKc() {
        return this.bestKc;
    }

    public long getBestTime() {
        return this.bestTime;
    }

    public int getTotalKills() {
        return this.totalKills;
    }

    public void setSelectedClass(ZombiesManager.ZombiesClass selectedClass) {
        this.selectedClass = selectedClass;
    }

    public void setConfigState(int configState) {
        this.configState = configState;
    }

    public void setBestWave(int bestWave) {
        this.bestWave = bestWave;
    }

    public void setBestKc(int bestKc) {
        this.bestKc = bestKc;
    }

    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }
}
