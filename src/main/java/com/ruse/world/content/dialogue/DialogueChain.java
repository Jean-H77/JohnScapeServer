package com.ruse.world.content.dialogue;

import com.ruse.model.entity.character.player.Player;

import java.util.Arrays;
import java.util.List;

public class DialogueChain {

    public List<DialoguePart> dialogueParts;

    int step = 0;

    public DialogueChain(DialoguePart... dialogueParts) {
        this.dialogueParts = Arrays.asList(dialogueParts);
    }

    public void start(Player player) {
        dialogueParts.get(step).execute(player);
    }

    public boolean click(Player player, int buttonId) {
        if(player.getDialogueChain() == null) return false;

        if (dialogueParts.get(step) instanceof Options) {
            int option = switch (buttonId) {
                case 2494 -> 1;
                case 2495 -> 2;
                case 2496 -> 3;
                case 2497 -> 4;
                case 2498 -> 5;
                default -> throw new IllegalStateException("Unexpected value: " + buttonId);
            };
            ((Options) dialogueParts.get(step)).getClickOption().option(player, option);
        }

        nextDialogue(player);

        return true;
    }

    public void nextDialogue(Player player) {

        DialoguePart dp = dialogueParts.get(step);

        if(dp instanceof ItemStatement && ((ItemStatement) dp).getClickContinueEvent() != null) {
            ((ItemStatement) dp).getClickContinueEvent().event();
        } else if(dp instanceof  NpcStatement  && ((NpcStatement) dp).getClickContinueEvent() != null) {
            ((NpcStatement) dp).getClickContinueEvent().event();
        } else if(dp instanceof  PlayerStatement  && ((PlayerStatement) dp).getClickContinueEvent() != null) {
            ((PlayerStatement) dp).getClickContinueEvent().event();
        }

        if(step == dialogueParts.size()-1) {
            player.getPacketSender().sendInterfaceRemoval();
        } else {
            step++;
            start(player);
        }
    }
}
