package com.ruse.world.content.dialogue.DialogueChain;

import com.ruse.model.entity.character.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogueChain {

    public List<DialoguePart> dialogueParts;
    int step = 0;

    private final Player player;
    private boolean removeInterface = true;

    public DialogueChain(Player player, DialoguePart... dialogueParts) {
        this.player = player;
        this.dialogueParts = Arrays.asList(dialogueParts);
    }

    public DialogueChain(Player player) {
        this.player = player;
        this.dialogueParts = new ArrayList<>();
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

            ((Options) dialogueParts.get(step)).getClickOption().option(player, this, option);
        }

        nextDialogue();
        return true;
    }

    public void nextDialogue() {

        DialoguePart dp = dialogueParts.get(step);

        if(dp instanceof ItemStatement && ((ItemStatement) dp).getClickContinueEvent() != null) {
            ((ItemStatement) dp).getClickContinueEvent().event();
        } else if(dp instanceof NpcStatement && ((NpcStatement) dp).getClickContinueEvent() != null) {
            ((NpcStatement) dp).getClickContinueEvent().event();
        } else if(dp instanceof PlayerStatement && ((PlayerStatement) dp).getClickContinueEvent() != null) {
            ((PlayerStatement) dp).getClickContinueEvent().event();
        }

        if(step == dialogueParts.size()-1) {
            if(removeInterface) {
                player.getPacketSender().sendInterfaceRemoval();
            }
        } else {
            step++;
            start();
        }
    }

    public static DialogueChain create(Player player, DialoguePart... parts) {
        player.setDialogueChain(new DialogueChain(player, parts));
        return player.getDialogueChain();
    }

    public static DialogueChain create(Player player) {
        player.setDialogueChain(new DialogueChain(player));
        return player.getDialogueChain();
    }

    public DialogueChain addPart(DialoguePart dialoguePart) {
        if(dialogueParts.contains(dialoguePart)){
            return this;
        }
        dialogueParts.add(dialoguePart);
        return this;
    }

    public List<DialoguePart> getDialogueParts() {
        return dialogueParts;
    }

    public void setDialogueParts(List<DialoguePart> dialogueParts) {
        this.dialogueParts = dialogueParts;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isRemoveInterface() {
        return removeInterface;
    }

    public void setRemoveInterface(boolean removeInterface) {
        this.removeInterface = removeInterface;
    }
}
