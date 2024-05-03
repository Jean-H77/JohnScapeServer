package com.ruse.world.content.skill.slayer;

public class SlayerTask {

    private final int npcId;
    private final SlayerTaskDifficulty difficulty;
    private final SlayerMaster slayerMaster;
    private int amount;

    public SlayerTask(int npcId, int amount, SlayerTaskDifficulty difficulty, SlayerMaster slayerMaster) {
        this.npcId = npcId;
        this.amount = amount;
        this.difficulty = difficulty;
        this.slayerMaster = slayerMaster;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getAmount() {
        return amount;
    }

    public int decrementAndGet(int decrementAmount) {
        amount -= decrementAmount;
        return amount;
    }

    public SlayerTaskDifficulty getDifficulty() {
        return difficulty;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public SlayerMaster getSlayerMaster() {
        return slayerMaster;
    }
}
