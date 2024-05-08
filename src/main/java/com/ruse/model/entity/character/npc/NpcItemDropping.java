package com.ruse.model.entity.character.npc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.Lists;
import com.ruse.model.Graphic;
import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.entity.character.GroundItemManager;
import com.ruse.model.entity.character.player.Player;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.DropLog;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.minigames.WarriorsGuild;
import com.ruse.world.content.skill.prayer.BoneType;

public class NpcItemDropping {
    public static class ItemDropAnnouncer {

        private static List<Integer> ITEM_LIST;

        private static final int[] TO_ANNOUNCE = new int[] { 14484, 4224,
            11702, 11704, 11706, 11708, 11704, 11724, 11726, 11728, 11718,
            11720, 11722, 11730, 11716, 14876, 11286, 13427, 6731, 6737,
            6735, 4151, 2513, 15259, 13902, 13890, 13884, 13861, 13858,
            13864, 13905, 13887, 13893, 13899, 13873, 13879, 13876, 13870,
            6571, 14008, 14009, 14010, 14011, 14012, 14013, 14014, 14015,
            14016, 13750, 13748, 13746, 13752, 11335, 15486, 13870, 13873,
            13876, 13884, 13890, 13896, 13902, 13858, 13861, 13864, 13867,
            11995, 11996, 11997, 11978, 12001, 12002, 12003, 12004, 12005,
            12006, 11990, 11991, 11992, 11993, 11994, 11989, 11988, 11987,
            11986, 11985, 11984, 11983, 11982, 11981, 11979, 13659, 11235,
            20000, 20001, 20002, 15103, 6585, 12926, 12929, 15486, 16753, 17235, 16863, 22007, 13996, 12931,//drag boots
            15104, 15105, 15106, 12603, 12601, 12605, 19908, 22012,
            22012, 18786, 19780, 11335, 14479,
            18719,
            22034, //armadyl c'bow
            15109, //jar of dirt
            22033, 22049, 22050, //ZULRAH PETS
            22055, //Wildywyrm pet
            13999,

        };

        public static void init() {
            ITEM_LIST = new ArrayList<>();
            for (int i : TO_ANNOUNCE) {
                ITEM_LIST.add(i);
            }
        }

        public static boolean announce(int item) {
            return ITEM_LIST.contains(item);
        }
    }

    private static void resetInterface(Player player) {
        for(int i = 8145; i < 8196; i++)
            player.getPacketSender().sendString(i, "");
        for(int i = 12174; i < 12224; i++)
            player.getPacketSender().sendString(i, "");
        player.getPacketSender().sendString(8136, "Close window");
    }

    public static List<Item> getDrop(Player p, int npcId) {
        ArrayList<Item> items = Lists.newArrayList();
        NPCDrops drops = NPCDrops.forId(npcId);
        final boolean ringOfWealth = p.getEquipment().get(Equipment.RING_SLOT).getId() == 2572;
        HashSet<Integer> dropped = new HashSet<>();
        if(drops == null) return items;
        for (int i = 0; i < drops.getDropList().length; i++) {
            if (drops.getDropList()[i].getItem().getId() <= 0
                    || drops.getDropList()[i].getItem().getAmount() <= 0) {
                continue;
            }

            final int dropChance = drops.getDropList()[i].getChance();
            if (dropChance == 0 || shouldDrop(dropped, dropChance, ringOfWealth)) {
                items.add(drops.getDropList()[i].getItem());
                if (dropChance != 0) {
                    dropped.add(dropChance);
                }
            }
        }

        return items;
    }

    /**
     * Drops items for a player after killing an npc. A player can max receive
     * one item per drop chance.
     *
     * @param p
     *            Player to receive drop.
     * @param npc
     *            NPC to receive drop FROM.
     */
    public static void dropItems(Player p, NPC npc) {
        if (npc.getLocation() == Locations.Location.WARRIORS_GUILD)
            WarriorsGuild.handleDrop(p, npc);
        NPCDrops drops = NPCDrops.forId(npc.getId());
        final Position npcPos = npc.getPosition().copy();

        final boolean goGlobal = p.getPosition().getZ() >= 0 && p.getPosition().getZ() < 4;

        if (p.getLocation() == Locations.Location.GODWARS_DUNGEON) { //ecumenical key
            int count = enumenicalCount(p);

            if (count < 3) {
                int chance = count * 10 + 60;

                if (Misc.getRandom(chance) == 1) {
                    GroundItemManager.spawnGroundItem(p, new GroundItem(new Item(22053, 1), npcPos, p.getUsername(), false, 150, true, 200));
                    p.getPacketSender().sendMessage("@cya@<shad=0><img=10> An ecumenical key has been dropped.");
                }
            }
        }


        // Drop json drops
        getDrop(p, npc.getId()).forEach(item -> drop(p, item, npc, npcPos, goGlobal));
    }

    public static int enumenicalCount(Player p) {
        int count = 0;
        for (int i = 0; i < p.getBanks().length; i++) {
            if (p.getBanks()[i].contains(22053)) {
                count = count + p.getBanks()[i].getAmount(22053);
            }
        }
        if (p.getInventory().contains(22053)) {
            count = count + p.getInventory().getAmount(22053);
        }
        return count;
    }

    public static boolean shouldDrop(HashSet<Integer> dropped, int chance, boolean ringOfWealth) {
        int random = chance; //pull the chance from the table
        if (ringOfWealth && random >= 60) { //if the chance from the table is greater or equal to 60, and player is wearing ring of wealth
            random -= (random / 10); //the chance from the table is lowered by 10% of the table's value
        }
        return !dropped.contains(chance) && Misc.getRandom(random) == 1; //return true if random between 0 & table value is 1.
    }

    public static void drop(Player player, Item item, NPC npc, Position pos, boolean goGlobal) {
        if(npc.getId() == 2007 || npc.getId() == 2042 || npc.getId() == 2043 || npc.getId() == 2044) {
            pos = player.getPosition().copy();
        }
        if ((player.getInventory().contains(18337) || (player.getSkillManager().skillCape(Skill.PRAYER) && player.getBonecrushEffect())) && BoneType.forId(item.getId()) != null) {
            player.getPacketSender().sendGlobalGraphic(new Graphic(777), pos);
            if (player.getRights().isMember()) {
                player.getSkillManager().addExperience(Skill.PRAYER, BoneType.forId(item.getId()).getBuryingXP() * 2);
                return;
            } else {
                player.getSkillManager().addExperience(Skill.PRAYER, BoneType.forId(item.getId()).getBuryingXP());
                return;
            }
        }

        player.getCollectionLogManager().handleBossDrop(npc.getId(), item);

        int itemId = item.getId();
        int amount = item.getAmount();

        if (itemId == 995 && player.getEquipment().get(Equipment.RING_SLOT).getId() == 22045) {
            if (!player.getInventory().contains(itemId) && player.getInventory().getFreeSlots() == 0) {
                player.getPacketSender().sendMessage("Your inventory is full, your Dragonstone ring (e) is unable to pick up coins!");
            } else {
                player.getInventory().add(itemId, amount);
                return;
            }
        }

        Player toGive = player;

        boolean ccAnnounce = false;
        if(Locations.inMulti(player)) {
            if(player.getCurrentClanChat() != null && player.getCurrentClanChat().getLootShare()) {
                CopyOnWriteArrayList<Player> playerList = new CopyOnWriteArrayList<Player>();
                for(Player member : player.getCurrentClanChat().getMembers()) {
                    if(member != null) {
                        if(member.getPosition().isWithinDistance(player.getPosition())) {
                            playerList.add(member);
                        }
                    }
                }
                if(playerList.size() > 0) {
                    toGive = playerList.get(Misc.getRandom(playerList.size() - 1));
                    if(toGive == null || toGive.getCurrentClanChat() == null || toGive.getCurrentClanChat() != player.getCurrentClanChat()) {
                        toGive = player;
                    }
                    ccAnnounce = true;
                }
            }
        }

        if(itemId == 18778) { //Effigy, don't drop one if player already has one
            if(toGive.getInventory().contains(18778) || toGive.getInventory().contains(18779) || toGive.getInventory().contains(18780) || toGive.getInventory().contains(18781)) {
                return;
            }
            for(Bank bank : toGive.getBanks()) {
                if(bank == null) {
                    continue;
                }
                if(bank.contains(18778) || bank.contains(18779) || bank.contains(18780) || bank.contains(18781)) {
                    return;
                }
            }
        }

        if (NpcItemDropping.ItemDropAnnouncer.announce(itemId)) {
            String itemName = item.getDefinition().getName();
            String itemMessage = Misc.anOrA(itemName) + " " + itemName;
            String npcName = Misc.formatText(npc.getDefinition().getName());
            String link = null;
            switch (itemId) {
                case 14484:
                    itemMessage = "a pair of Dragon Claws";
                    break;
                case 20000:
                case 20001:
                case 20002:
                    itemMessage = itemName;
                    break;
            }
            switch (npc.getId()) {
                case 50:
                case 3200:
                case 8133:
                case 4540:
                case 1160:
                case 8549:
                    npcName = "The " + npcName + "";
                    break;
                case 51:
                case 54:
                case 5363:
                case 8349:
                case 1592:
                case 1591:
                case 1590:
                case 1615:
                case 9463:
                case 9465:
                case 9467:
                case 1382:
                case 13659:
                case 11235:
                    npcName = "" + Misc.anOrA(npcName) + " " + npcName + "";
                    break;
            }
            String message = "<img=10><col=009966><shad=0> " + toGive.getUsername()
                    + " has just received " + itemMessage + " from " + npcName
                    + "!";
            World.sendMessage(message);
            if(ccAnnounce) {
                ClanChatManager.sendMessage(player.getCurrentClanChat(), "<col=16777215>[<col=255>Lootshare<col=16777215>]<col=3300CC> "+toGive.getUsername()+" received " + itemMessage + " from "+npcName+"!");
            }

            PlayerLogs.log(toGive.getUsername(), "" + toGive.getUsername() + " received " + itemMessage + " from " + npcName + "!");
        }

        GroundItemManager.spawnGroundItem(toGive, new GroundItem(item, pos,
                toGive.getUsername(), false, 150, goGlobal, 200));

        if(npc.getConstitution() >= 500) {
            DropLog.submit(toGive, new DropLog.DropLogEntry(itemId, item.getAmount(), npc.getId()));
        }
    }

}
