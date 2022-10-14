package com.ruse.world.content.Quest;

import com.ruse.model.entity.character.player.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

public class QuestManager implements Serializable {

    transient private final Player quester;

    private final HashMap<String, Quest> quests = new HashMap<>();

    private int questsCompleted;

    private final QuestTabInterface questInterface;

    public boolean startQuest(Quest quest) {
        return quests.putIfAbsent(quest.questTitle, quest) == null;
    }

    public Optional<Quest> getQuest(String title) {
        return Optional.ofNullable(quests.get(title));
    }

    public QuestManager(Player quester) {
        this.quester = quester;
        this.questInterface = new QuestTabInterface(quester);
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

    public Player getQuester() {
        return this.quester;
    }

    public HashMap<String, Quest> getQuests() {
        return this.quests;
    }

    public int getQuestsCompleted() {
        return this.questsCompleted;
    }

    public QuestTabInterface getQuestInterface() {
        return this.questInterface;
    }

    public void setQuestsCompleted(int questsCompleted) {
        this.questsCompleted = questsCompleted;
    }
}
