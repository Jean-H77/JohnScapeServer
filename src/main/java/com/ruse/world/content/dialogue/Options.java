package com.ruse.world.content.dialogue;

import com.ruse.model.entity.character.player.Player;

public class Options implements DialoguePart {

    private final String title;
    private final String[] options;
    private final clickOption clickOption;

    public Options(clickOption clickOption, String title, String... options) {
        this.clickOption = clickOption;
        this.title = title;
        this.options = options;
    }

    @Override
    public void execute(Player player) {
        int firstChildId = OPTION_DIALOGUE_ID[options.length - 1];
        player.getPacketSender().sendString(firstChildId - 1, title);
        for (int i = 0; i < options.length; i++) {
            player.getPacketSender().sendString(firstChildId + i, options[i]);
        }
        player.getPacketSender().sendChatboxInterface(firstChildId - 2);
    }

    @FunctionalInterface
    public interface clickOption {
        void option(Player player, int option);
    }

    public Options.clickOption getClickOption() {
        return clickOption;
    }

    public static final int[] OPTION_DIALOGUE_ID = {
            13760,
            2461,
            2471,
            2482,
            2494,
    };
}
