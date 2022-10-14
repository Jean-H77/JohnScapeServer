package com.ruse.world.content.Quest;


public abstract class Quest {
    protected final String questTitle;
    protected QuestStep currentStep;
    protected final QuestStep[] steps;
    protected boolean isCompleted;

    public Quest(String questTitle, QuestStep[] steps) {
        this.questTitle = questTitle;
        this.steps = steps;
    }

    public QuestStep getStep(int stepNumber) {
        return steps[stepNumber];
    }

    public String getQuestTitle() {
        return this.questTitle;
    }

    public QuestStep getCurrentStep() {
        return this.currentStep;
    }

    public QuestStep[] getSteps() {
        return this.steps;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public void setCurrentStep(QuestStep currentStep) {
        this.currentStep = currentStep;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Quest)) return false;
        final Quest other = (Quest) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$questTitle = this.getQuestTitle();
        final Object other$questTitle = other.getQuestTitle();
        if (this$questTitle == null ? other$questTitle != null : !this$questTitle.equals(other$questTitle))
            return false;
        final Object this$currentStep = this.getCurrentStep();
        final Object other$currentStep = other.getCurrentStep();
        if (this$currentStep == null ? other$currentStep != null : !this$currentStep.equals(other$currentStep))
            return false;
        if (!java.util.Arrays.deepEquals(this.getSteps(), other.getSteps())) return false;
        if (this.isCompleted() != other.isCompleted()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Quest;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $questTitle = this.getQuestTitle();
        result = result * PRIME + ($questTitle == null ? 43 : $questTitle.hashCode());
        final Object $currentStep = this.getCurrentStep();
        result = result * PRIME + ($currentStep == null ? 43 : $currentStep.hashCode());
        result = result * PRIME + java.util.Arrays.deepHashCode(this.getSteps());
        result = result * PRIME + (this.isCompleted() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "Quest(questTitle=" + this.getQuestTitle() + ", currentStep=" + this.getCurrentStep() + ", steps=" + java.util.Arrays.deepToString(this.getSteps()) + ", isCompleted=" + this.isCompleted() + ")";
    }
}
