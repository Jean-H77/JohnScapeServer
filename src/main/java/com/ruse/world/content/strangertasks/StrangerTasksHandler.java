package com.ruse.world.content.strangertasks;

import com.google.common.collect.ImmutableMap;
import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.input.EnterAmount;
import com.ruse.scheduler.JobScheduler;
import com.ruse.util.Misc;
import com.ruse.world.content.ShopManager;
import com.ruse.world.content.dialogue.DialogueChain.DialogueChain;
import com.ruse.world.content.dialogue.DialogueChain.DialoguePart;
import com.ruse.world.content.dialogue.DialogueChain.NpcStatement;
import com.ruse.world.content.dialogue.DialogueChain.Options;
import com.ruse.world.content.dialogue.DialogueExpression;

import java.util.Map;

import static com.ruse.world.content.strangertasks.StrangerTask.Difficulty.*;

public class StrangerTasksHandler {

    private record Task(int itemId, int minAmount, int maxAmount) {}

    private static final int
            EASY_TASK = 1,
            MEDIUM_TASK = 2,
            HARD_TASK = 3,
            ELITE_TASK = 4;

    private static final Map<Integer, Task[]> tasks = ImmutableMap.<Integer, Task[]>builder().
            put(EASY_TASK, new Task[]{
                    new Task(4151, 1, 5),
                    new Task(4151, 5, 8)
            }).
            put(MEDIUM_TASK, new Task[]{
                    new Task(4151, 1, 5),
                    new Task(4151, 5, 8)
            }).
            put(HARD_TASK, new Task[]{
                    new Task(4151, 1, 5),
                    new Task(4151, 5, 8)
            }).
            put(ELITE_TASK, new Task[]{
                    new Task(919, 1, 2),
                    new Task(923, 1, 2),
                    new Task(920, 1, 2),
                    new Task(924, 1, 2),
                    new Task(921, 1, 2),
                    new Task(922, 1, 2)
            }).
            build();

    public static void dialogue(Player player) {
        DialogueChain.create(player)
                .addPart(new NpcStatement(DialogueExpression.PLAIN_TALKING,
                        38539,
                        "I offer various tasks of multiple difficulties",
                        "would you like a task or to view my shop?"))
                .addPart(new Options(((player1, parent, option) -> {
                    switch (option) {
                        case 1:
                            parent.addPart(new Options((p, c, op) -> {
                                StrangerTask strangerTask = getTask(op, player);

                                if(strangerTask != null) {
                                    if (strangerTask.isCompleted()) {
                                        c.addPart(new NpcStatement(DialogueExpression.PLAIN_TALKING, 38539, "You have already completed this task"));
                                        return;
                                    }

                                    if (strangerTask.inProgress()) {
                                        c.addPart(turnInDialogue(strangerTask, op, player));
                                        return;
                                    }
                                }

                                Task randomTask = Misc.randomElement(tasks.get(op));
                                int randomAmount = Misc.inclusiveRandom(randomTask.minAmount, randomTask.maxAmount);
                                StrangerTask task = new StrangerTask(randomAmount, randomTask.itemId);

                                player.getStrangerTasks().put(
                                        switch (op) {
                                          case 1 -> EASY;
                                          case 2 -> MEDIUM;
                                          case 3 -> HARD;
                                          case 4 -> ELITE;
                                            default -> throw new IllegalStateException("Unexpected value: " + op);
                                        }
                                ,task);

                                c.addPart(new NpcStatement(DialogueExpression.PLAIN_TALKING, 38539,
                                        "You must bring me ",
                                        ItemDefinition.forId(task.getItemId()).getName()
                                        + " @blu@x" + task.getAmount()));

                                c.addPart(turnInDialogue(task, op, player));

                            }, "Select an option",
                                    getTask(EASY, player) != null ? (getTask(EASY, player).isCompleted() ? "@str@Easy Task" : getTask(EASY, player).inProgress() ? "Easy Task @blu@(In Progress)" : "") : "Easy Task",
                                    getTask(MEDIUM, player) != null ? (getTask(MEDIUM, player).isCompleted() ? "@str@Medium Task" : getTask(MEDIUM, player).inProgress() ? "Medium Task @blu@(In Progress)" : "") : "Medium Task",
                                    getTask(HARD, player) != null ? (getTask(HARD, player).isCompleted() ? "@str@Hard Task" : getTask(HARD, player).inProgress() ? "Hard Task @blu@(In Progress)" : "") :  "Hard Task",
                                    getTask(ELITE, player) != null ? (getTask(ELITE, player).isCompleted() ? "@str@Elite Task" : getTask(ELITE, player).inProgress() ? "Elite Task @blu@(In Progress)" : "") :  "Elite Task"));
                            break;
                        case 2:
                            parent.addPart(new NpcStatement(DialogueExpression.PLAIN_TALKING, 38539,
                                    "Your tasks will reset in approx.",
                                    "@dre@" + JobScheduler.printTimeLeftUntilNextFire("MidnightReset") + "."));
                            break;
                        case 3:
                            parent.setRemoveInterface(false);
                            ShopManager.openShop("Stranger Store", player);
                        break;
                    }
                }), "Select an option", "View tasks", "When will my tasks reset?", "Open shop"))
                .start();
    }

    private static DialoguePart turnInDialogue(StrangerTask strangerTask, int difficulty, Player player) {
        return new Options((p, parent, option) -> {
            if(option == 1) {
                parent.setRemoveInterface(false);
                turnIn(strangerTask,difficulty, player);
            }
        }, "Select an option", "Turn in " + ItemDefinition.forId(strangerTask.getItemId()).getName(), "Nevermind");
    }

    private static void turnIn(StrangerTask strangerTask, int difficulty, Player player) {
        int itemId = strangerTask.getItemId();
        player.getPacketSender().sendInterfaceRemoval();
        player.getPacketSender().sendEnterAmountPrompt("Turn in " + ItemDefinition.forId(itemId).getName() + " x@blu@" + strangerTask.getAmount());
        player.setInputHandling(new EnterAmount() {
            @Override
            public void handleAmount(Player player, int amount) {
                int inventoryAmount = player.getInventory().getAmount(itemId);
                int amountLeft = strangerTask.getAmount();

                if(amount > inventoryAmount) {
                    amount = inventoryAmount;
                }

                if(amount > amountLeft) {
                    amount = amountLeft;
                }

                player.getInventory().delete(itemId, amount);
                strangerTask.decrementAmount(amount);

                if(strangerTask.getAmount() == 0) {
                    player.addItemUnderAnyCircumstances(new Item(50527, switch (difficulty) {
                        case EASY_TASK -> 50;
                        case MEDIUM_TASK -> 100;
                        case HARD_TASK -> 200;
                        case ELITE_TASK -> 300;
                        default -> throw new IllegalStateException("Unexpected value: " + difficulty);
                    }));

                    strangerTask.setCompleted(true);
                    DialogueChain.create(player).addPart(new NpcStatement(DialogueExpression.PLAIN_TALKING, 38539, "Thank you for completed your task.",
                            "Here are some tokens for your troubles.")).start();
                } else if(amount > 0) {
                    DialogueChain.create(player).addPart(new NpcStatement(DialogueExpression.PLAIN_TALKING, 38539, "Thank you for your turn ins.",
                            "You still have @blu@" + strangerTask.getAmount() + "@bla@ left to turn in.")).start();
                }
            }
        });
    }

    private static StrangerTask getTask(StrangerTask.Difficulty difficulty, Player player) {
        return player.getStrangerTasks().get(difficulty);
    }

    private static StrangerTask getTask(int option, Player player) {
        return switch (option) {
            case 1 -> getTask(EASY, player);
            case 2 -> getTask(MEDIUM, player);
            case 3 -> getTask(HARD, player);
            case 4 -> getTask(ELITE, player);
            default -> throw new IllegalStateException("Unexpected value: " + option);
        };
    }
}
