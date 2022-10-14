package com.ruse.world.content.Quest;

public class QuestStep {
    private final String description;
    private final int stepNumber;
    private boolean isCompleted;

    public QuestStep(String description, int stepNumber) {
        this.description = description;
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public int getStepNumber() {
        return this.stepNumber;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof QuestStep)) return false;
        final QuestStep other = (QuestStep) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        if (this.getStepNumber() != other.getStepNumber()) return false;
        if (this.isCompleted() != other.isCompleted()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof QuestStep;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        result = result * PRIME + this.getStepNumber();
        result = result * PRIME + (this.isCompleted() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "QuestStep(description=" + this.getDescription() + ", stepNumber=" + this.getStepNumber() + ", isCompleted=" + this.isCompleted() + ")";
    }
}
