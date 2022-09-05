package com.ruse.world.content.Quest;

import com.ruse.model.entity.character.player.Player;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

@Data
public class QuestManager implements Serializable {

    transient private final Player quester;

    private final HashMap<String, Quest> quests = new HashMap<>();

    private int questsCompleted;

    public boolean startQuest(Quest quest) {
        return quests.putIfAbsent(quest.questTitle, quest) == null;
    }

    public Optional<Quest> getQuest(String title) {
        return Optional.ofNullable(quests.get(title));
    }

    public void completeStep(String questTitle, int step) {
        Optional<Quest> possibleQuest = getQuest(questTitle);
        if(possibleQuest.isPresent()) {
            Quest quest = possibleQuest.get();

            if (quest.isCompleted) return;

            QuestStep qs = quest.getStep(step);
            if (qs.isCompleted()) return;

            QuestStep current = quest.currentStep;

            if (qs.equals(quest.currentStep)) {
                if (!qs.isCompleted()) {
                    qs.setCompleted(true);

                    int stepNumber = current.getStepNumber();

                    if (stepNumber == quest.getSteps().length - 1) {
                        completeQuest(quest);
                    } else {
                        quest.setCurrentStep(quest.getSteps()[stepNumber + 1]);
                    }
                }
            }
        }
    }

    public void completeQuest(Quest quest) {
        quest.setCompleted(true);

    }
}
