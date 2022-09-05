package com.ruse.world.content.turn_in_tasks.Dialogues;

import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueType;

public class Forester extends Dialogue {

    public static final int ID = 1508;

    @Override
    public int npcId() {
        return ID;
    }

    @Override
    public DialogueType type() {
        return DialogueType.NPC_STATEMENT;
    }

    @Override
    public DialogueExpression animation() {
        return DialogueExpression.PLAIN_TALKING;
    }

    @Override
    public String[] dialogue() {
        return new String[] {
                "Hello"
        };
    }
}
