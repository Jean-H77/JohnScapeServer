package com.ruse.world.content.collection_log;

import java.util.ArrayList;
import java.util.List;

public class PlayerLog {

    private boolean hasCompleted;
    private List<LogEntry> entries;
    private boolean canClaim;
    private boolean hasClaimed;

    PlayerLog() {
        this.hasCompleted = false;
        this.canClaim = false;
        this.hasClaimed = false;
        this.entries = new ArrayList<>();
    }

    public boolean isHasCompleted() {
        return hasCompleted;
    }

    public void setHasCompleted(boolean hasCompleted) {
        this.hasCompleted = hasCompleted;
    }


    public boolean isCanClaim() {
        return canClaim;
    }

    public void setCanClaim(boolean canClaim) {
        this.canClaim = canClaim;
    }

    public boolean isHasClaimed() {
        return hasClaimed;
    }

    public void setHasClaimed(boolean hasClaimed) {
        this.hasClaimed = hasClaimed;
    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LogEntry> entries) {
        this.entries = entries;
    }

    static class LogEntry {
        private final int itemId;
        private int itemAmount;

        public LogEntry(int itemId) {
            this.itemId = itemId;
            this.itemAmount = 0;
        }

        public int getItemId() {
            return itemId;
        }

        public int getItemAmount() {
            return itemAmount;
        }

        public int incrementAndGet(int amount) {
            return itemAmount += amount;
        }
    }

}
