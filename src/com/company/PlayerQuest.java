package com.company;

public class PlayerQuest {
    private Quest details;
    private boolean questComplete;

    public PlayerQuest(Quest details, boolean questComplete) {
        this.details = details;
        this.questComplete = questComplete;
    }

    public PlayerQuest() {

    }

    public Quest getDetails() {
        return details;
    }

    public void setDetails(Quest details) {
        this.details = details;
    }

    public boolean isQuestComplete() {
        return questComplete;
    }

    public void setQuestComplete(boolean questComplete) {
        this.questComplete = questComplete;
    }
}

