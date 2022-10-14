package com.ruse.world.content.combat.dungeon;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.GameObject;
import com.ruse.model.Position;
import com.ruse.world.World;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.clan.ClanChat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Dungeon {

    public static final List<Dungeon> ACTIVE_DUNGEONS = new ArrayList<>();

    private final List<DungeonNPC> npcs = new ArrayList<>();

    private final List<GameObject> objects = new ArrayList<>();

    private final Position startPosition;

    private final Position exitPosition;

    private ClanChat clanChat;

    private LocalDateTime endTime;

    private final int lengthInMinutes;

    private final String dungeonName;

    private int height;


    public Dungeon(Position startPosition, Position exitPosition, int lengthInMinutes, String dungeonName) {
        this.startPosition = startPosition;
        this.exitPosition = exitPosition;
        this.lengthInMinutes = lengthInMinutes;
        this.dungeonName = dungeonName;
    }

    public void start() {
        endTime = LocalDateTime.now().plus(lengthInMinutes, ChronoUnit.MINUTES);
        ACTIVE_DUNGEONS.add(this);
        clanChat.getDungeons().add(this);
        height = ((clanChat.getIndex() + 1) * 4) + (clanChat.getDungeons().size() * 4);
        timerTask();
    }

    public void end() {
        if(!npcs.isEmpty()) {
            npcs.forEach(World::deregister);
        }
        if(!objects.isEmpty()) {
            objects.forEach(CustomObjects::deleteGlobalObject);
        }
        if(!clanChat.getMembers().isEmpty()) {
            clanChat.getMembers().forEach(player -> {
                if (player != null) {
                    if (player.getCurrentDungeon().equals(this) && player.isInDungeon()) {
                        player.getDungeonManager().leaveDungeon(true);
                    }
                }
            });
        }
        TaskManager.cancelTasks(this);
        clanChat.getDungeons().remove(this);
        ACTIVE_DUNGEONS.remove(this);
    }


    public void npcDeath(DungeonNPC npc) {
        npcs.remove(npc);
    }


    public void spawn() {

    }

    public Duration timeLeft() {
        return Duration.between(LocalDateTime.now(), endTime);
    }

    public void timerTask() {
        TaskManager.submit(new Task((int) (TimeUnit.MINUTES.toMillis(lengthInMinutes) / GameSettings.ENGINE_PROCESSING_CYCLE_RATE), this, false) {
            @Override
            protected void execute() {
                end();
                stop();
            }
        });
    }

    public int getHeight() {
        return height;
    }

    public List<DungeonNPC> getNpcs() {
        return this.npcs;
    }

    public List<GameObject> getObjects() {
        return this.objects;
    }

    public Position getStartPosition() {
        return this.startPosition;
    }

    public Position getExitPosition() {
        return this.exitPosition;
    }

    public ClanChat getClanChat() {
        return this.clanChat;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public int getLengthInMinutes() {
        return this.lengthInMinutes;
    }

    public String getDungeonName() {
        return this.dungeonName;
    }

    public void setClanChat(ClanChat clanChat) {
        this.clanChat = clanChat;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
