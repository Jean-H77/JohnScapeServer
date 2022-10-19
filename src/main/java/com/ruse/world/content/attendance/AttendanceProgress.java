package com.ruse.world.content.attendance;

import java.util.HashMap;

public class AttendanceProgress {
    private final HashMap<Integer, Boolean> days = new HashMap<>();

    public boolean hasReceived(int day) {
        return days.get(day) != null;
    }

    public boolean put(int day) {
        if(hasReceived(day)) return false;
        days.put(day, true);
        return true;
    }

}
