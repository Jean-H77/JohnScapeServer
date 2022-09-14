package com.ruse.world.content.Quest.impl;

import com.ruse.world.content.Quest.Quest;
import com.ruse.world.content.Quest.QuestStep;

public class JohnsJourney extends Quest {

    // start position 2729, 3368
    private static final QuestStep[] QUEST_STEPS = new QuestStep[] {
            new QuestStep("Investigate Sister Senga's hideout.",  0),
            new QuestStep("Defeat Sister Senga for her vial.",  1),
            new QuestStep("Bring the vial back to the guildmaster for a reward.",  2)
    };

    public static final String TITLE = "Learning the ropes";

    public JohnsJourney() {
        super(TITLE, QUEST_STEPS);
    }

}
