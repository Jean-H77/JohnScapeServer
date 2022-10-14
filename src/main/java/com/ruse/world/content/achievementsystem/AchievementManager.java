package com.ruse.world.content.achievementsystem;

import com.google.gson.Gson;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.*;

public class AchievementManager {
    public static HashMap<AchievementType, List<Achievement>> ACHIEVEMENTS_AS_LIST_VALUES = new HashMap<>();
    public static HashMap<String, Achievement> ACHIEVEMENTS = new HashMap<>();
    public static final int INTERFACE_ID = 51350;

    private final Player p;
    public final HashMap<String, PlayerAchievement> playerAchievements = new HashMap<>();
    public int achievementPoints;
    public int completed;
    public AchievementType currentTab;

    public AchievementManager(Player p) {
        this.p = p;
    }

    public static void loadAchievements() {
        convert(new Yaml(new Constructor(Map.class)).load(ClassLoader.class.getClassLoader().getResourceAsStream("achievements.yaml")));
     //   ClassLoader.class.getClass().getClassLoader().getResourceAsStream()
    }

    public static void convert(LinkedHashMap<String, List<String>> map) {
        Gson gson = new Gson();
        for(Map.Entry<String, List<String>> entry : map.entrySet()) {
            AchievementType type = AchievementType.valueOf(entry.getKey());
            List<Achievement> achievements = new ArrayList<>();
            List<String> l = entry.getValue();
            for(Object s : l) {
                String str = gson.toJson(s);
                Achievement achievement = gson.fromJson(str, Achievement.class);
                achievements.add(achievement);
                ACHIEVEMENTS.put(achievement.getDescription(), achievement);
            }
            ACHIEVEMENTS_AS_LIST_VALUES.put(type, achievements);
        }
    }

    public void openInterface() {
        currentTab = AchievementType.GENERAL;
        p.getPacketSender().sendConfig(321, 0)
                   .sendString(51375, String.valueOf(achievementPoints))
                   .sendString(51377, String.valueOf(completed))
                   .sendString(51378, completed + "/" + ACHIEVEMENTS.size());
        sendTabData();
        p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public void doProgress(String achievementDesc, int amount) {
        PlayerAchievement playerAchievement = playerAchievements.computeIfAbsent(achievementDesc, x -> new PlayerAchievement());
        if(playerAchievement.isCompleted()) return;

        int myProgress = playerAchievement.getProgress();
        Achievement achievement = ACHIEVEMENTS.get(achievementDesc);
        int completionAmount = achievement.getCompletionAmount();

        if(myProgress < completionAmount) {
            myProgress += amount;
            playerAchievement.setProgress(myProgress += amount);
            if (myProgress >= completionAmount) {
                playerAchievement.setProgress(completionAmount);
                playerAchievement.setCompleted(true);
                achievementPoints += achievement.getCompletionAmount();
                completed++;
            }
        }
    }

    public boolean handleButtonClick(int id) {
        if(p.getInterfaceId() == INTERFACE_ID) {
            if (id == -14174) {
                // shop
                return true;
            }
            if (id >= -14181 && id <= -14175) {
                AchievementType achievementType;
                if(id == -14181) {
                    achievementType = AchievementType.GENERAL;
                } else if(id == -14180) {
                    achievementType = AchievementType.DUNGEONS_RAIDS;
                } else if(id == -14179) {
                    achievementType = AchievementType.WORLD_EVENTS;
                } else if(id == -14178) {
                    achievementType = AchievementType.COLLECTIONS;
                } else if(id == -14177) {
                    achievementType = AchievementType.SKILLING;
                }  else if(id == -14176) {
                    achievementType = AchievementType.PLAYER_VS_MONSTER;
                } else {
                    achievementType = AchievementType.MINIGAMES;
                }
                if (achievementType != currentTab) {
                    currentTab = achievementType;
                    sendTabData();
                }
                return true;
            }
        }
        return false;
    }

    public void sendTabData() {
        List<Achievement> achievements = ACHIEVEMENTS_AS_LIST_VALUES.get(currentTab);
        PacketBuilder packetBuilder = new PacketBuilder(42, Packet.PacketType.SHORT);

        int pBar = 51381;
        int desc = 51456;
        int shield = 51532;
        int chest = 51607;
        int size = achievements.size();
        packetBuilder.putInt(size);
        for(int i = 0; i < size; i++) {
               Achievement achievement = achievements.get(i);

               String description = achievement.getDescription();
               int completionAmount = achievement.getCompletionAmount();
               PlayerAchievement playerAchievement = playerAchievements.get(description);

               packetBuilder.putString(description);
               packetBuilder.putInt(desc+i);

               packetBuilder.putInt(shield+i);
               packetBuilder.putShort(getPointSprite(completionAmount));

               packetBuilder.putInt(pBar+i);
               packetBuilder.putInt(playerAchievement == null ? 0 : playerAchievement.getProgress());
               packetBuilder.putInt(completionAmount);

               packetBuilder.putInt(chest+i);
               packetBuilder.putShort(playerAchievement == null ? 1449 : playerAchievement.getProgress() == completionAmount ? 1450 : 1449);
        }

        p.getSession().queueMessage(packetBuilder);
        p.getPacketSender().sendScrollMax(51379, 34*achievements.size()-1);
    }

    public static int getPointSprite(int point) {
        if(point == 1) {
            return 1453;
        } else if(point == 2) {
            return 1454;
        } else if(point == 3) {
            return 1455;
        } else if(point == 4) {
            return 1456;
        } else if(point == 5) {
            return 1457;
        }
        return 1453;
    }

    public Player getP() {
        return this.p;
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

    public AchievementType getCurrentTab() {
        return this.currentTab;
    }

    public void setAchievementPoints(int achievementPoints) {
        this.achievementPoints = achievementPoints;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public void setCurrentTab(AchievementType currentTab) {
        this.currentTab = currentTab;
    }
}
