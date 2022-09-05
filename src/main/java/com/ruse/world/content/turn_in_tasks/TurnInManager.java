package com.ruse.world.content.turn_in_tasks;

import com.google.common.collect.ImmutableList;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.turn_in_tasks.Dialogues.Forester;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public class TurnInManager {

    private final Player player;

    private final List<TurnInTask> myTurnInTasks = new ArrayList<>();

    public boolean talkToNpc(int npcId) {
        Optional<TurnInTask> possibleTask = getPossibleTurnInTask(npcId);
        if(possibleTask.isPresent()) {
            DialogueManager.start(player, possibleTask.get().getDialogue());
            return true;
        }
        return false;
    }

    public void startTask(TurnInTask turnInTask) {
        myTurnInTasks.add(turnInTask);
    }

    public boolean isActive(TurnInTask turnInTask) {
        return myTurnInTasks.contains(turnInTask);
    }

    @RequiredArgsConstructor
    @Getter
    enum TurnInTask {
        FORESTER(4151, 30, Forester.ID, new Forester());

        private final int requiredItem;
        private final int requiredAmount;
        private final int questGiverNpcId;

        private final Dialogue dialogue;
        public static final ImmutableList<TurnInTask> TURN_IN_TASKS = ImmutableList.copyOf(TurnInTask.values());
    }

    public Optional<TurnInTask> getPossibleTurnInTask(int id) {
        return TurnInTask.TURN_IN_TASKS.stream()
                .filter(turnInTask -> turnInTask.questGiverNpcId == id)
                .findFirst();
    }
}
