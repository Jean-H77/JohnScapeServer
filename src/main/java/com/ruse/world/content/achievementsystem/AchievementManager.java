package com.ruse.world.content.achievementsystem;

import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;

import java.util.*;

public class AchievementManager {
    public static LinkedHashMap<String, Achievement> ACHIEVEMENTS = new LinkedHashMap<>();
    public static final int INTERFACE_ID = 51350;

    private final Player p;
    public final HashMap<String, PlayerAchievement> playerAchievements = new HashMap<>();
    public int achievementPoints;
    public int completed;

    public int completedBeginner;
    public int completedEasy;
    public int completedMedium;
    public int completedHard;
    public int completedElite;

    static int beginnerCount;
    static int easyCount;
    static int mediumCount;
    static int hardCount;
    static int eliteCount;

    public AchievementManager(Player p) {
        this.p = p;
    }

    static {
        ACHIEVEMENTS.put("bbeg1", new Achievement("This is beg1", 5, 100));
        ACHIEVEMENTS.put("bbeg2", new Achievement("This is Beg2", 5, 100));
        ACHIEVEMENTS.put("bbeg3", new Achievement("This is Beg3", 5, 100));
        ACHIEVEMENTS.put("bbeg4", new Achievement("This is Beg4", 5, 100));
        ACHIEVEMENTS.put("bbeg5", new Achievement("This is Beg5", 5, 100));
        ACHIEVEMENTS.put("bbeg6", new Achievement("This is Beg6", 5, 100));

        ACHIEVEMENTS.put("esBeg7", new Achievement("This is beg1", 7, 100));
        ACHIEVEMENTS.put("esBeg8", new Achievement("This is Beg2", 7, 100));
        ACHIEVEMENTS.put("esBeg9", new Achievement("This is Beg3", 7, 100));
        ACHIEVEMENTS.put("esBeg10", new Achievement("This is Beg4", 7, 100));
        ACHIEVEMENTS.put("esBeg11", new Achievement("This is Beg5", 7, 100));
        ACHIEVEMENTS.put("esBeg12", new Achievement("This is Beg6", 7, 100));

        ACHIEVEMENTS.put("mBeg19", new Achievement("This is beg1", 10, 100));
        ACHIEVEMENTS.put("mBeg20", new Achievement("This is Beg2", 10, 100));
        ACHIEVEMENTS.put("mBeg21", new Achievement("This is Beg3", 10, 100));
        ACHIEVEMENTS.put("mBeg22", new Achievement("This is Beg4", 10, 100));
        ACHIEVEMENTS.put("mBeg23", new Achievement("This is Beg5", 10, 100));
        ACHIEVEMENTS.put("mBeg24", new Achievement("This is Beg6", 10, 100));

        ACHIEVEMENTS.put("hBeg25", new Achievement("This is beg1", 15, 100));
        ACHIEVEMENTS.put("hBeg26", new Achievement("This is Beg2", 15, 100));
        ACHIEVEMENTS.put("hBeg27", new Achievement("This is Beg3", 15, 100));
        ACHIEVEMENTS.put("hBeg28", new Achievement("This is Beg4", 15, 100));
        ACHIEVEMENTS.put("hBeg29", new Achievement("This is Beg5", 15, 100));
        ACHIEVEMENTS.put("hBeg30", new Achievement("This is Beg6", 15, 100));

        ACHIEVEMENTS.put("eBeg31", new Achievement("This is beg1", 20, 100));
        ACHIEVEMENTS.put("eBeg32", new Achievement("This is Beg2", 20, 100));
        ACHIEVEMENTS.put("eBeg33", new Achievement("This is Beg3", 20, 100));
        ACHIEVEMENTS.put("eBeg34", new Achievement("This is Beg4", 20, 100));
        ACHIEVEMENTS.put("eBeg35", new Achievement("This is Beg5", 20, 100));
        ACHIEVEMENTS.put("eBeg36", new Achievement("This is Beg6", 20, 100));

         beginnerCount = (int) ACHIEVEMENTS.values().stream().filter(achievement -> achievement.getPoints() == 5).count();
         easyCount = (int) ACHIEVEMENTS.values().stream().filter(achievement -> achievement.getPoints() == 7).count();
         mediumCount = (int) ACHIEVEMENTS.values().stream().filter(achievement -> achievement.getPoints() == 10).count();
         hardCount = (int) ACHIEVEMENTS.values().stream().filter(achievement -> achievement.getPoints() == 15).count();
         eliteCount = (int) ACHIEVEMENTS.values().stream().filter(achievement -> achievement.getPoints() == 20).count();
    }

    public void openInterface() {
        p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void doProgress(String title, int amount) {
        PlayerAchievement playerAchievement = playerAchievements.computeIfAbsent(title, x -> new PlayerAchievement());
        if(playerAchievement.isCompleted()) return;

        int myProgress = playerAchievement.getProgress();
        Achievement achievement = ACHIEVEMENTS.get(title);
        int completionAmount = achievement.getCompletionAmount();

        if(myProgress < completionAmount) {
            int points = achievement.getPoints();
            playerAchievement.setProgress(myProgress += amount);
            if (myProgress >= completionAmount) {
                playerAchievement.setProgress(completionAmount);
                playerAchievement.setCompleted(true);
                achievementPoints += points;
                completed++;

                if(points == 5) {
                    completedBeginner++;
                } else if(points == 7) {
                    completedEasy++;
                } else if(points == 10) {
                    completedMedium++;
                } else if(points == 15) {
                    completedHard++;
                } else if(points == 20) {
                    completedElite++;
                }
            }
        }
        sendAchievementProgress(title, playerAchievement.getProgress(), achievement.getCompletionAmount());
    }

    public void sendAchievementData() {
        PacketBuilder packetBuilder = new PacketBuilder(42, Packet.PacketType.SHORT);

        packetBuilder.putShort(beginnerCount);
        packetBuilder.putShort(easyCount);
        packetBuilder.putShort(mediumCount);
        packetBuilder.putShort(hardCount);
        packetBuilder.putShort(eliteCount);

        for(Map.Entry<String, Achievement> ach : ACHIEVEMENTS.entrySet()) {
            packetBuilder.putString(ach.getKey());
            Achievement achievement = ach.getValue();
            packetBuilder.putString(achievement.getDescription());
            packetBuilder.put(achievement.getPoints());
        }

        p.getSession().queueMessage(packetBuilder);

        for(Map.Entry<String, PlayerAchievement> pach : playerAchievements.entrySet()) {
            PlayerAchievement pa = pach.getValue();
            String title = pach.getKey();
            sendAchievementProgress(title, pa.getProgress(), ACHIEVEMENTS.get(title).getCompletionAmount());
        }

        sendCompletedAchievementsAmount();
    }

    public void sendAchievementProgress(String title, int progress, int completionTotal) {
        PacketBuilder packetBuilder = new PacketBuilder(5, Packet.PacketType.SHORT);
        packetBuilder.putString(title);
        packetBuilder.putInt(progress);
        packetBuilder.putInt(completionTotal);

        if(progress == completionTotal) {
            sendCompletedAchievementsAmount();
        }

        p.getSession().queueMessage(packetBuilder);
    }

    public void sendCompletedAchievementsAmount() {
        PacketBuilder packetBuilder = new PacketBuilder(7, Packet.PacketType.SHORT);
        packetBuilder.putShort(completedBeginner);
        packetBuilder.putShort(completedEasy);
        packetBuilder.putShort(completedMedium);
        packetBuilder.putShort(completedHard);
        packetBuilder.putShort(completedElite);
        p.getSession().queueMessage(packetBuilder);
    }

    public void sendClaimedAchievementStatus(String title, boolean hasClaimed) {
        PacketBuilder packetBuilder = new PacketBuilder(6, Packet.PacketType.SHORT);
        packetBuilder.putString(title);
        packetBuilder.putString(String.valueOf(hasClaimed));
        p.getSession().queueMessage(packetBuilder);
    }

    public HashMap<String, PlayerAchievement> getPlayerAchievements() {
        return this.playerAchievements;
    }

    public int getAchievementPoints() {
        return this.achievementPoints;
    }

    public int getCompleted() {
        return this.completed;
    }

    public void setAchievementPoints(int achievementPoints) {
        this.achievementPoints = achievementPoints;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

}
