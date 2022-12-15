package com.ruse.world.content.collectionlog;

import com.ruse.model.Item;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.world.content.KillsTracker;

import java.util.*;

public class CollectionLogManager {

    public static final int MAIN_INTERFACE_ID = 40428;
    public static final int LOG_ITEM_CONTAINER_ID = 40556;
    public static final int REWARD_ITEM_CONTAINER_ID = 40554;

    public static final int OPEN_COLLECTION_LOG_BTN_ID = -16033;
    public static final int CLAIM_BTN_ID = -24987;

    public static final int BOSSES_TAB_BTN_ID = -25102;
    public static final int RAIDS_TAB_BTN_ID = -25101;
    private static final int CLUES_TAB_BTN_ID = -25100;
    public static final int MINIGAMES_TAB_BTN_ID = -25099;
    public static final int OTHER_TAB_BTN_ID = -25098;

    public static final int LOG_NAME_STRING_ID = 40557;
    public static final int OBTAINED_STRING_ID = 40558;
    public static final int COMPLETIONS_STRING_ID = 40559;
    public static final int CLAIM_STRING_ID = 40553;
    public static final int TITLE_STRING_ID = 40430;

    private final Player p;

    private HashMap<String, PlayerLog> logs = new HashMap<>();

    private int completionsCounter;

    private CollectionLogTab currentTab;
    private Log currentLog;

    public CollectionLogManager(Player p) {
        this.p = p;
    }


    public void displayMainInterface() {
        currentLog = null;
        currentTab = null;
        changeAndDisplayTab(CollectionLogTab.BOSSES);
        p.getPacketSender().sendConfig(118, 0);
        p.getPacketSender().sendString(TITLE_STRING_ID, "Collection Log - " + completionsCounter + "/" + CollectionLogTab.SIZE);
        p.getPacketSender().sendInterface(MAIN_INTERFACE_ID);
    }


    public boolean handleButtonClick(int btnID) {
        if(btnID >= -25088 && btnID <= -24989) {
            int index = 25088 + btnID;
            if(index >= currentTab.getLogs().length) return true;
            showLog(currentTab.getLogs()[index]);
            return true;
        }
        switch (btnID) {
            case OPEN_COLLECTION_LOG_BTN_ID -> {
                displayMainInterface();
                return true;
            }
            case CLAIM_BTN_ID -> {
                PlayerLog playerLog = logs.computeIfAbsent(currentLog.getName(), x -> new PlayerLog());
                if (playerLog.isCanClaim() && playerLog.isHasCompleted() && !playerLog.isHasClaimed()) {
                    int freeSlots = p.getInventory().getFreeSlots();
                    Item[] rewards = currentLog.getRewards();
                    if (freeSlots >= rewards.length) {
                        playerLog.setCanClaim(false);
                        playerLog.setHasClaimed(true);
                        p.getInventory().addItemSet(rewards);
                    } else {
                        p.getPacketSender().sendMessage("You need at least@red@" + freeSlots + "@bla@inventory spaces to claim this");
                    }
                } else {
                    p.getPacketSender().sendMessage(playerLog.isHasClaimed() ? "@red@You already claimed this reward." : "@red@You cannot claim this.");
                }
                return true;
            }
            case BOSSES_TAB_BTN_ID -> {
                changeAndDisplayTab(CollectionLogTab.BOSSES);
                return true;
            }
            case RAIDS_TAB_BTN_ID -> {
                changeAndDisplayTab(CollectionLogTab.RAIDS);
                return true;
            }
            case CLUES_TAB_BTN_ID -> {
                changeAndDisplayTab(CollectionLogTab.CLUES);
                return true;
            }
            case MINIGAMES_TAB_BTN_ID -> {
                changeAndDisplayTab(CollectionLogTab.MINIGAMES);
                return true;
            }
            case OTHER_TAB_BTN_ID -> {
                changeAndDisplayTab(CollectionLogTab.OTHER);
                return true;
            }
        }
        return false;
    }

    public void changeAndDisplayTab(CollectionLogTab tab) {
        if(currentTab == tab) return;
        currentTab = tab;
        Log[] logs = tab.getLogs();

        if(logs == null || logs.length == 0) {
            p.getPacketSender().sendMessage("@red@This tab contains no logs to view.");
            displayMainInterface();
            return;
        }

        p.getPacketSender().sendScrollMax(40444, Math.max(255, logs.length * 15));
        showLog(logs[0]);

        PacketBuilder builder = new PacketBuilder(37, Packet.PacketType.SHORT);

        for(int i = 0; i < 100; i++) {
            if (i < logs.length) {
                PlayerLog playerLog = this.logs.computeIfAbsent(logs[i].getName(), x -> new PlayerLog());
                builder.putString(getNameText(logs[i], playerLog));
                builder.putInt(40448+i);
            } else {
                builder.putString("");
                builder.putInt(40448+i);
            }
        }
        p.getSession().queueMessage(builder);
    }

    private List<Item> getItemsFromEntries(PlayerLog log) {
        List<Item> itemList = new ArrayList<>();
        for(int i = 0; i < log.getEntries().size(); i++) {
            PlayerLog.LogEntry temp = log.getEntries().get(i);
            itemList.add(new Item(temp.getItemId(), temp.getItemAmount()));
        }
        return itemList;
    }

    public void showLog(Log log) {
       if(log == currentLog) return;
        currentLog = log;
        PlayerLog playerLog = logs.computeIfAbsent(log.getName(), x -> new PlayerLog());
        List<Item> temp = getItemsFromEntries(playerLog);
        List<Item> toDisplay = new ArrayList<>();
        int[] logsItemsIds = log.getRequiredItemIds();

        for(int i = 0; i < logsItemsIds.length; i++) {
            int finalI = i;
            Optional<Item> optionalItem = temp
                    .parallelStream()
                    .filter(it -> it.getId() == logsItemsIds[finalI])
                    .findFirst();
            if(optionalItem.isPresent()) {
                toDisplay.add(optionalItem.get());
            } else {
                toDisplay.add(new Item(logsItemsIds[i], 0));
            }
        }

        p.getPacketSender().sendScrollMax(40445, Math.max(146, (toDisplay.size() / 6 * 15)));

        PacketBuilder builder = new PacketBuilder(39, Packet.PacketType.SHORT);

        putItemData(builder, toDisplay, LOG_ITEM_CONTAINER_ID);
        putItemData(builder, currentLog.getRewards(), REWARD_ITEM_CONTAINER_ID);

        builder.putString(log.getName());
        builder.putInt(LOG_NAME_STRING_ID);

        builder.putString(getObtainedText(log, playerLog));
        builder.putInt(OBTAINED_STRING_ID);

        builder.putString(getCompletionsText(log));
        builder.putInt(COMPLETIONS_STRING_ID);

        builder.putString(playerLog.isHasClaimed() ? "@whi@Claimed" : (playerLog.isCanClaim() ? "@gre@" : "@dre@@str@") + "Claim");
        builder.putInt(CLAIM_STRING_ID);

        p.getSession().queueMessage(builder);
    }

    public void putItemData(PacketBuilder builder, List<Item> items, int interfaceId) {
        builder.putInt(interfaceId);
        builder.putShort(items.size());
        for(Item item : items) {
            builder.putInt(item.getId() + 1);
            builder.putInt(item.getAmount());
        }
    }

    public void putItemData(PacketBuilder builder, Item[] items, int interfaceId) {
        builder.putInt(interfaceId);
        builder.putShort(items.length);
        for(Item item : items) {
            builder.putInt(item.getId() + 1);
            builder.putInt(item.getAmount());
        }
    }

    public String getObtainedText(Log log, PlayerLog plog) {
        int logAmount = log.getRequiredItemIds().length;
        int pLogAmount = plog.getEntries().size();
        return "Obtained: " +
                (logAmount == pLogAmount ? "@gre@"+logAmount+"/"+logAmount :
                        pLogAmount != 0 ? "@yel@" + pLogAmount + "/" + logAmount :
                        "@red@"+0+"/"+logAmount);
    }

    public String getNameText(Log log, PlayerLog plog) {
        int pLogAmount = plog.getEntries().size();
        return  log.getRequiredItemIds().length == pLogAmount ? "@gre@"+log.getName():
                        pLogAmount > 0 ? "@yel@" +  log.getName() :
                                log.getName();
    }

    public String getCompletionsText(Log log) {
        if(log.getLogType() == LogType.KILLING) {
            Optional<KillsTracker.KillsEntry> optionalKillsEntry =  p.getKillsTracker()
                    .parallelStream()
                    .filter(killsEntry -> killsEntry.getNpcName().equalsIgnoreCase(log.getName()))
                    .findFirst();

            return "Kills: @whi@" + (optionalKillsEntry.isPresent() ? optionalKillsEntry.get().getAmount() : "0");

        } else if(log.getLogType() == LogType.CLUE_OPENINGS) {
            String clueName = log.getName();
            int amount = 0;

            /**
             * temp values
             */
            if(clueName.equals("Beginner clue")) {
                amount = 5;
            } else if(clueName.equals("Easy clue")){
                amount = 10;
            } else if(clueName.equals("Medium clue")){
                amount = 10;
            } else if(clueName.equals("Hard clue")){
                amount = 10;
            } else if(clueName.equals("Elite clue")){
                amount = 10;
            } else if(clueName.equals("Master clue")){
                amount = 10;
            }

            return log.getName() + " completed: @whi@" + clueName + "s completed: " + amount;

        } else if(log.getLogType() == LogType.BOX_CHEST_LOOTING) {
            return log.getName() + " openings: @whi@ 0";
        }
        return "";
    }

    public void handleBossDrop(int id, Item droppedItem) {
        NpcDefinition def = NpcDefinition.forId(id);
        if(def.isCollectionLogNpc() && droppedItem.getDefinition().isCollectionLogItem()) {
            String name = def.getName();
            PlayerLog playerLog = logs.computeIfAbsent(name, x -> new PlayerLog());
            handleEntry(name, droppedItem, playerLog);
        }
    }

    public void handleClueOrChestOrBoxOpening(String name, Item[] items) {
        for(Item item : items) {
            if(item.getDefinition().isCollectionLogItem()) {
                PlayerLog playerLog = logs.computeIfAbsent(name, x -> new PlayerLog());
                handleEntry(name, item, playerLog);
            }
        }
    }


    public void handleShopPurchases(String shopName, Item purchasedItem) {
       if(purchasedItem.getDefinition().isCollectionLogItem()) {
             PlayerLog playerLog = logs.computeIfAbsent(shopName, x -> new PlayerLog());
             handleEntry(shopName, purchasedItem, playerLog);
       }
    }

    private void handleEntry(String logName, Item item, PlayerLog playerLog) {
        Optional<Log> optionalLog =  CollectionLogTab.ALL_LOGS
                .parallelStream()
                .filter(log -> log.getName().equalsIgnoreCase(logName))
                .findFirst();
        Log log;
        if(optionalLog.isPresent()) {
            log = optionalLog.get();
            if(Arrays.stream(log.getRequiredItemIds()).anyMatch(l -> l == item.getId())) {
                Optional<PlayerLog.LogEntry> optionalLogEntry = playerLog
                        .getEntries()
                        .parallelStream()
                        .filter(logEntry -> logEntry.getItemId() == item.getId())
                        .findFirst();

                PlayerLog.LogEntry logEntry;

                if(optionalLogEntry.isPresent()) {
                    logEntry = optionalLogEntry.get();
                    logEntry.incrementAndGet(item.getAmount());
                } else {
                    logEntry = new PlayerLog.LogEntry(item.getId());
                    logEntry.incrementAndGet(item.getAmount());
                    playerLog.getEntries().add(logEntry);

                    p.getPacketSender().sendMessage("New item added to your collection log: @red@" + item.getDefinition().getName());

                        if(playerLog.getEntries().size() == log.getRequiredItemIds().length && !playerLog.isHasCompleted()) {
                            playerLog.setHasCompleted(true);
                            playerLog.setCanClaim(true);
                            p.getPacketSender().sendMessage("You have completed the collection log of: @red@" + log.getName() + "!");
                            p.getPacketSender().sendMessage("You can now claim your reward");
                            completionsCounter++;
                       }
                }
            }
        }
    }
}

