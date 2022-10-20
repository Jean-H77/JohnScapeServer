package com.ruse.world.content.attendance;

import com.ruse.model.Item;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class AttendanceUI {
    private static final int INTERFACE_ID = 60000;

    private final Player p;

    public AttendanceUI(Player p) {
        this.p = p;
    }

    public void showInterface() {
       p.getPacketSender().sendConfig(0,178);
       sendTabData(AttendanceTab.LOYAL);
       p.getPacketSender().sendInterface(INTERFACE_ID);
    }

    private void sendTabData(AttendanceTab tab) {
        PacketBuilder packetBuilder = new PacketBuilder(43, Packet.PacketType.SHORT);
        AttendanceManager attendanceManager = p.getAttendanceManager();

        Item[] rewardArray = attendanceManager.getMonthlyRewardAsArray(tab);
        AttendanceProgress progress = attendanceManager.getPlayerAttendanceProgress().computeIfAbsent(tab, x -> new AttendanceProgress());

        int itemIdStart = 60074;
        int checkMarkIdStart = 60105;
        int boxIdStart = 60012;
        int dayStringIdStart = 60043;

        int unClaimedBoxSprite = 1507;
        int nextRewardBoxSprite = 1504;
        int finalDayBoxSprite = 1506;

        for(int i = 0; i < 31; i++) {
            if(rewardArray.length > i) {
                Item item = rewardArray[i];
                boolean hasReceived = progress.hasReceived((i+1));

                packetBuilder.putInt(itemIdStart+i);
                packetBuilder.putInt(item.getId()+1);
                packetBuilder.putInt(item.getAmount());

                packetBuilder.putInt(checkMarkIdStart+i);
                packetBuilder.put(hasReceived ? 1 : 0);

                packetBuilder.putInt(boxIdStart+i);

                if(i == rewardArray.length-1) {
                    packetBuilder.putShort(finalDayBoxSprite);
                } else {
                    int nextRewardDay = attendanceManager.getNextUnclaimedDay(tab);
                    if(nextRewardDay != -1 && (i+1)==nextRewardDay) {
                        packetBuilder.putShort(nextRewardBoxSprite);
                    } else {
                        packetBuilder.putShort(unClaimedBoxSprite);
                    }
                }
                packetBuilder.putString(String.valueOf((i+1)));
                packetBuilder.putInt(dayStringIdStart+i);
            } else {
                packetBuilder.putInt(itemIdStart+i);
                packetBuilder.putInt(0);
                packetBuilder.putInt(0);
                packetBuilder.putInt(checkMarkIdStart+i);
                packetBuilder.put(0);
                packetBuilder.putInt(boxIdStart+i);
                packetBuilder.putShort(0);
                packetBuilder.putString("");
                packetBuilder.putInt(dayStringIdStart+i);
            }
        }

        LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
        String month = localDate.getMonth().toString().substring(0,3).toLowerCase();
        String formattedMonth = month.substring(0,1).toUpperCase() + month.substring(1);

        packetBuilder.putString("Event Period: " + formattedMonth + " 1, " + localDate.getYear() + " (00:00 UTC) - " + formattedMonth + " " + localDate.lengthOfMonth() + " (23:59 UTC).");
        packetBuilder.putInt(60008);

        packetBuilder.putString("Claim Period Until " + formattedMonth + " " + localDate.lengthOfMonth () + " (23:59 UTC).");
        packetBuilder.putInt(60009);

        p.getSession().queueMessage(packetBuilder);
    }
}
