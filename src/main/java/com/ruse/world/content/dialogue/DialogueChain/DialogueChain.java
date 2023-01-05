package com.ruse.world.content.dialogue.DialogueChain;

import com.ruse.model.entity.character.player.Player;

import java.util.Arrays;
import java.util.List;

public class DialogueChain {

    public List<DialoguePart> dialogueParts;

    private final Player player;

    int step = 0;

    public DialogueChain(Player player, DialoguePart... dialogueParts) {
        this.player = player;
        this.dialogueParts = Arrays.asList(dialogueParts);
    }

    public void start() {
        DialoguePart dp = dialogueParts.get(step);
        dp.execute(player);

        if(dp instanceof ItemStatement && ((ItemStatement) dp).getOpenDialogueEvent() != null) {
            ((ItemStatement) dp).getOpenDialogueEvent().event();
        } else if(dp instanceof NpcStatement && ((NpcStatement) dp).getOpenDialogueEvent() != null) {
            ((NpcStatement) dp).getOpenDialogueEvent().event();
        } else if(dp instanceof PlayerStatement && ((PlayerStatement) dp).getOpenDialogueEvent() != null) {
            ((PlayerStatement) dp).getOpenDialogueEvent().event();
        }
    }

    public boolean click(int buttonId) {
        if(player.getDialogueChain() == null) return false;

        if (dialogueParts.get(step) instanceof Options) {

            int option = 0;

            switch (((Options) dialogueParts.get(step)).getOptions().length) {
                case 2 -> option = switch (buttonId) {
                    case 2461 -> 1;
                    case 2462 -> 2;
                    default -> throw new IllegalStateException("Unexpected value: " + buttonId);
                };
                case 3 -> option = switch (buttonId) {
                    case 2471 -> 1;
                    case 2472 -> 2;
                    case 2473 -> 3;
                    default -> throw new IllegalStateException("Unexpected value: " + buttonId);
                };
                case 4 -> option = switch (buttonId) {
                    case 2482 -> 1;
                    case 2483 -> 2;
                    case 2484 -> 3;
                    case 2485 -> 4;
                    default -> throw new IllegalStateException("Unexpected value: " + buttonId);
                };
                case 5 -> option = switch (buttonId) {
                   case 2494 -> 1;
                   case 2495 -> 2;
                   case 2496 -> 3;
                   case 2497 -> 4;
                   case 2498 -> 5;
                   default -> throw new IllegalStateException("Unexpected value: " + buttonId);
               };
            }

            ((Options) dialogueParts.get(step)).getClickOption().option(player, option);
        }

        nextDialogue();
        return true;
    }

    public void nextDialogue() {

        DialoguePart dp = dialogueParts.get(step);

        if(step == dialogueParts.size()-1) {
            player.getPacketSender().sendInterfaceRemoval();
        } else {
            step++;
            start();
        }

        if(dp instanceof ItemStatement && ((ItemStatement) dp).getClickContinueEvent() != null) {
            ((ItemStatement) dp).getClickContinueEvent().event();
        } else if(dp instanceof NpcStatement && ((NpcStatement) dp).getClickContinueEvent() != null) {
            ((NpcStatement) dp).getClickContinueEvent().event();
        } else if(dp instanceof PlayerStatement && ((PlayerStatement) dp).getClickContinueEvent() != null) {
            ((PlayerStatement) dp).getClickContinueEvent().event();
        }
    }

    public static DialogueChain create(Player player, DialoguePart... parts) {
        player.setDialogueChain(new DialogueChain(player, parts));
        return player.getDialogueChain();
    }
}
