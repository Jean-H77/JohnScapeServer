package com.ruse.world.content.skill.slayer;

import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.util.Misc;
import com.ruse.world.content.ShopManager;
import com.ruse.world.content.dialogue.DialogueChain.DialogueChain;
import com.ruse.world.content.dialogue.DialogueChain.NpcStatement;
import com.ruse.world.content.dialogue.DialogueChain.Options;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;

public class Slayer {

    public static void slayerMasterDialogue(Player player, int npcId) {
        SlayerMaster slayerMaster;
        if((slayerMaster = SlayerMaster.VALUES.get(npcId)) == null) {
            return;
        }

        DialogueChain.create(player)
                .addPart(new Options((p, chain, o) -> {
                    switch (o) {
                        case 1 -> {
                            if(player.getSlayerTask() != null) {
                                chain.addPart(new NpcStatement(DialogueExpression.PLAIN_TALKING, npcId, "You must finish your current task","before getting a new one"));
                                break;
                            }
                            chain.addPart(new NpcStatement(DialogueExpression.PLAIN_TALKING, npcId, "How difficult would you like your task?"));
                            chain.addPart(new Options((plr, c, opt) -> {
                                chain.setRemoveInterface(false);
                                switch (opt) {
                                    case 1 -> getTask(player, slayerMaster, SlayerTaskDifficulty.EASY);
                                    case 2 -> getTask(player, slayerMaster, SlayerTaskDifficulty.MEDIUM);
                                    case 3 -> getTask(player, slayerMaster, SlayerTaskDifficulty.HARD);
                                    case 4 -> getTask(player, slayerMaster, SlayerTaskDifficulty.ELITE);
                                }
                            }, "Select an option", "Easy (5-15 kills)", "Medium (16-35 kills)", "Hard (36-80 kills)", "Elite (81-125 kills)"));
                        }
                        case 2 -> {
                            chain.setRemoveInterface(false);
                            ShopManager.openShop("Slayer Store", player);
                        }
                    }
                }, "Select an option",  "Get slayer task", "View shop"))
                .start();
    }

    private static void getTask(Player player, SlayerMaster slayerMaster, SlayerTaskDifficulty slayerTaskDifficulty) {
        SlayerDungeon slayerDungeon = slayerMaster.getDungeon();
        SlayerTaskDetails randomTask = Misc.randomElement(slayerDungeon.getSlayerTaskDetails());
        int npcId = randomTask.npcId();

        int amount = switch (slayerTaskDifficulty) {
            case EASY -> Misc.inclusiveRandom(5, 15);
            case MEDIUM -> Misc.inclusiveRandom(16, 35);
            case HARD -> Misc.inclusiveRandom(36, 80);
            case ELITE -> Misc.inclusiveRandom(81, 125);
        };

        SlayerTask slayerTask = new SlayerTask(npcId, amount, slayerTaskDifficulty, slayerMaster);
        player.setSlayerTask(slayerTask);

        DialogueChain.create(player)
                .addPart(new NpcStatement(DialogueExpression.PLAIN_TALKING, slayerMaster.getNpcId(),
                "You have been assigned " + NpcDefinition.forId(npcId).getName() + " x" + amount+".",
                        "They are in the " + slayerDungeon.getName() + ".",
                        "Would you like to teleport directly to your task?"))
                .addPart(new Options((p, c, o) -> {
                    if(o == 1) {
                        TeleportHandler.teleportPlayer(player, randomTask.position(), TeleportType.NORMAL);
                    }
                }, "Select an option", "Yes", "No"))
                .start();
    }
}
