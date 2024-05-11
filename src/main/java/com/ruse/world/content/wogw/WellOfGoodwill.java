package com.ruse.world.content.wogw;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.MessageType;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.EnterAmount;
import com.ruse.model.input.Input;
import com.ruse.world.World;

import java.text.DecimalFormat;

public class WellOfGoodwill {

    private static final int INTERFACE_ID = 51065;
    private static final int CURRENCY_ID = 995;
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    public static int contributedAmount;

    private static final Input inputHandler = new EnterAmount() {
        @Override
        public void handleAmount(Player player, int amount) {
            int playerAmount = player.getInventory().getAmount(CURRENCY_ID);

            if(amount > player.getInventory().getAmount(CURRENCY_ID)) {
                amount = playerAmount;
            }

            if(amount <= 0) {
                return;
            }

            calculateIncrease(player, amount);
        }
    };

    public static void open(Player player) {
        player.getPacketSender()
                .sendProgressBar((int)getCurrentPercentage(), 100, 51074)
                .sendString(51090, FORMAT.format(getCurrentPercentage()) + "%")
                .sendInterface(INTERFACE_ID);
    }

    public static void contribute(Player player) {
        player.getPacketSender().sendEnterAmountPrompt("How much would you like to contribute?");
        player.setInputHandling(inputHandler);
    }

    private static void calculateIncrease(Player player, int amount) {
        if(contributedAmount == 1000000) {
            player.getPacketSender().sendMessage("@red@The well is currently full.");
            return;
        }

        if(contributedAmount + amount > 1000000) {
            amount = 1000000 - contributedAmount;
        }

        if(contributedAmount == 0) {
            startDepleteTask();
        }

        double beforePercentage = getCurrentPercentage();
        contributedAmount += amount;
        double afterPercentage = getCurrentPercentage();

        player.getInventory().delete(CURRENCY_ID, amount);
        open(player);

        if(beforePercentage < 25.0 && afterPercentage >= 25) {
            World.sendMessage(MessageType.SERVER_ALERT,"[WOGW] Players are now receiving 10% increased damage!");
        }

        if(beforePercentage < 50.0 && afterPercentage >= 50) {
            World.sendMessage(MessageType.SERVER_ALERT, "[WOGW] Players are now receiving 35% increased experience!");
        }

        if(beforePercentage < 75.0 && afterPercentage >= 75) {
            World.sendMessage(MessageType.SERVER_ALERT,"[WOGW] Players are now receiving 15% extra slayer points!");
        }

        if(afterPercentage >= 100.0) {
            World.sendMessage(MessageType.SERVER_ALERT, "[WOGW] Players are now receiving 20% increased drop rate!");
        }
    }

    public static void deplete() {
        double percentage = getCurrentPercentage();
        contributedAmount -= 100000;

        if(contributedAmount <= 0) {
            contributedAmount = 0;
        }

        for(Player player : World.getPlayers()) {
            if(player == null) continue;
            if(player.getInterfaceId() == INTERFACE_ID) {
                player.getPacketSender()
                        .sendProgressBar((int)getCurrentPercentage(), 100, 51074)
                        .sendString(51090, FORMAT.format(getCurrentPercentage()) + "%");
            }

            if(percentage >= 25.0 && contributedAmount < 250000) {
                player.getPacketSender().sendMessage(MessageType.SERVER_ALERT, "[WOGW] Damage boost has ended.");
            }

            if(percentage >= 50.0 && contributedAmount < 500000) {
                player.getPacketSender().sendMessage(MessageType.SERVER_ALERT, "[WOGW] Experience boost has ended.");
            }

            if(percentage >= 75.0  && contributedAmount < 750000) {
                player.getPacketSender().sendMessage(MessageType.SERVER_ALERT,"[WOGW] Slayer boost has ended.");
            }

            if(percentage >= 100.0) {
                player.getPacketSender().sendMessage(MessageType.SERVER_ALERT, "[WOGW] Drop rate boost has ended.");
            }
        }
    }

    public static void startDepleteTask() {
        TaskManager.submit(new Task(3000) {
            @Override
            protected void execute() {
                deplete();

                if(contributedAmount <= 0) {
                    stop();
                }
            }
        });
    }

    public static double getCurrentPercentage() {
        return contributedAmount / 10000.0;
    }

    public static boolean isDamageOn() {
        return contributedAmount >= 250000;
    }

    public static boolean isExpOn() {
        return contributedAmount >= 500000;
    }

    public static boolean isSlayerOn() {
        return contributedAmount >= 750000;
    }

    public static boolean isDropRateOn() {
        return contributedAmount == 1000000;
    }
}
