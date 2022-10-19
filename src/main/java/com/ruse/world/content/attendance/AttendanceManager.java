package com.ruse.world.content.attendance;

import com.ruse.model.Item;
import com.ruse.model.PlayerRights;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.SessionState;
import com.ruse.world.World;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AttendanceManager {
    private final Player p;
    private LocalDate lastLoggedInDate;
    private final HashMap<AttendanceTab, AttendanceProgress> playerAttendanceProgress = new HashMap<>();

    public AttendanceManager(Player p) {
        this.p = p;
        this.lastLoggedInDate = LocalDate.now(ZoneOffset.UTC);
    }

    public void receiveReward() {
        for(AttendanceTab tab : getTabs()) {
            int currentDay = getCurrentDay();
            if(!lastLoggedInDate.getMonth().equals(LocalDate.now(ZoneOffset.UTC).getMonth())) {
                playerAttendanceProgress.clear(); //reset for new month
            }
            AttendanceProgress attendanceProgress = playerAttendanceProgress.computeIfAbsent(tab, x -> new AttendanceProgress());
            if(!attendanceProgress.hasReceived(currentDay)) {
                Item item = getRewardOfTheDay(tab);
                if(item == null) {
                    p.getPacketSender().sendMessage("@red@This day has no reward.");
                    return;
                }
                lastLoggedInDate = LocalDate.now(ZoneOffset.UTC);
                if(attendanceProgress.getDayReward(currentDay)) {
                    p.getPacketSender().sendMessage("@red@You have been given " + item.getDefinition().getName() + " x " + item.getAmount() + " as attendance reward for day " + currentDay + "!");
                    p.getInventory().add(item);
                }
            }
        }
    }

    public static void nextDay() {
        for(Player onlinePlayer : World.getPlayers()) {
            if(onlinePlayer != null && onlinePlayer.isRegistered() && onlinePlayer.getSession().getState() != SessionState.LOGGING_OUT) {
                onlinePlayer.getAttendanceManager().receiveReward();
            }
        }
    }

    public boolean checkIfDifferentDay() {
        return !LocalDate.now(ZoneOffset.UTC).isEqual(lastLoggedInDate);
    }

    public static int getCurrentDay() {
        return LocalDate.now(ZoneOffset.UTC).getDayOfMonth();
    }

    public Item[] getMonthlyRewardAsArray(AttendanceTab tab) {
        MonthlyReward monthlyArray = Arrays.stream(tab.getMonthlyReward()).filter(monthlyReward -> monthlyReward.getMonth().equals(LocalDate.now(ZoneOffset.UTC).getMonth())).findFirst().orElse(null);
        if(monthlyArray != null) {
            return monthlyArray.getItems();
        }
        return null;
    }

    public Item getRewardOfTheDay(AttendanceTab tab) {
        Item[] itemsArray = getMonthlyRewardAsArray(tab);
        if(itemsArray != null) {
            return itemsArray[LocalDate.now(ZoneOffset.UTC).getDayOfMonth()-1];
        } else {
            return null;
        }
    }

    public LocalDate getLastLoggedInDate() {
        return lastLoggedInDate;
    }

    public void setLastLoggedInDate(LocalDate lastLoggedInDate) {
        this.lastLoggedInDate = lastLoggedInDate;
    }

    public HashMap<AttendanceTab, AttendanceProgress> getPlayerAttendanceProgress() {
        return playerAttendanceProgress;
    }

    public List<AttendanceTab> getTabs() {
        List<AttendanceTab> tabs = new ArrayList<>();
        Month month = LocalDate.now(ZoneOffset.UTC).getMonth();

        tabs.add(AttendanceTab.LOYAL);

        if(month == Month.DECEMBER) {
            tabs.add(AttendanceTab.CHRISTMAS);
        }

        if(p.getRights() == PlayerRights.CONTRIBUTOR) {
            tabs.add(AttendanceTab.DONATOR);
        }

        return tabs;
    }
}
