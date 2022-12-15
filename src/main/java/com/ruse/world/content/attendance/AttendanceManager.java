package com.ruse.world.content.attendance;

import com.ruse.model.Item;
import com.ruse.model.PlayerRights;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.entity.character.player.PlayerSaving;
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

    public void newDay() {
        if(!lastLoggedInDate.getMonth().equals(LocalDate.now(ZoneOffset.UTC).getMonth())) {
            playerAttendanceProgress.clear();
        }
        lastLoggedInDate = LocalDate.now(ZoneOffset.UTC);
        for(AttendanceTab tab : getTabs()) {
            AttendanceProgress attendanceProgress = playerAttendanceProgress.computeIfAbsent(tab, x -> new AttendanceProgress());
            int nextUnclaimedDay = getNextUnclaimedDay(tab);
            if(nextUnclaimedDay != -1) {
                Item item = getRewardOfTheDay(tab, nextUnclaimedDay);
                if(item == null) {
                    p.getPacketSender().sendMessage("@red@This day has no reward.");
                    return;
                }
                if(attendanceProgress.put(nextUnclaimedDay)) {
                    if(p.getAttendanceUI().isPopUp() && !p.busy()) {
                        p.getAttendanceUI().showInterface();
                    }
                    p.getPacketSender().sendMessage("@red@You have been given " + item.getDefinition().getName() + " x " + item.getAmount() + " as attendance reward for day " + nextUnclaimedDay + "!");
                    p.addItemUnderAnyCircumstances(item);
                    PlayerSaving.save(p);
                }
            }
        }
    }

    public static void nextDay() {
        for(Player onlinePlayer : World.getPlayers()) {
            if(onlinePlayer != null && onlinePlayer.isRegistered() && onlinePlayer.getSession().getState() != SessionState.LOGGING_OUT && onlinePlayer.getAttendanceManager().isDifferentDay()) {
                onlinePlayer.getAttendanceManager().newDay();
            }
        }
    }

    public boolean isDifferentDay() {
        return !LocalDate.now(ZoneOffset.UTC).isEqual(lastLoggedInDate);
    }

    private static int getCurrentDay() {
        return LocalDate.now(ZoneOffset.UTC).getDayOfMonth();
    }

    public Item[] getMonthlyRewardAsArray(AttendanceTab tab) {
        MonthlyReward monthlyArray = Arrays.stream(tab.getMonthlyReward()).filter(monthlyReward -> monthlyReward.getMonth().equals(LocalDate.now(ZoneOffset.UTC).getMonth())).findFirst().orElse(null);
        if(monthlyArray != null) {
            return monthlyArray.getItems();
        }
        return null;
    }

    private Item getRewardOfTheDay(AttendanceTab tab, int day) {
        Item[] itemsArray = getMonthlyRewardAsArray(tab);
        if(itemsArray != null) {
            return itemsArray[day-1];
        }
        return null;
    }

    public int getNextUnclaimedDay(AttendanceTab tab) {
        int length = LocalDate.now(ZoneOffset.UTC).lengthOfMonth();
        AttendanceProgress progress = playerAttendanceProgress.computeIfAbsent(tab, x -> new AttendanceProgress());
        for(int i = 1; i <= length; i++) {
            if(!progress.hasReceived(i)) {
                return i;
            }
        }
        return -1;
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

        //if(month == Month.DECEMBER) {
        //    tabs.add(AttendanceTab.CHRISTMAS);
      //  }
        //if(p.getRights() == PlayerRights.CONTRIBUTOR) {
          //  tabs.add(AttendanceTab.DONATOR);
        //}

        return tabs;
    }
}
