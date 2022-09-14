package com.ruse.world.content.Quest;

import com.google.common.collect.ImmutableList;
import com.ruse.GameSettings;
import com.ruse.model.entity.character.player.Player;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class QuestTabInterface {

    public static final int QUEST_TAB_ID = 49437;
    public static final int PLAYER_TAB_ID = 49426;
    public static final int WORLD_TAB_ID = 49475;
    public static final int KNOWLEDGE_BASE_ID = 49486;

    private final Player p;

    public boolean handleTabSwitch(int btnId) {

        Optional<TabType> optionalTabType = TabType.TABS
                .stream()
                .filter(tabType -> Arrays.stream(tabType.tabButtons).anyMatch(tabButton -> tabButton.btnId == btnId))
                .findFirst();

        if(optionalTabType.isPresent()) {
            Optional<TabButton> tabButton = Arrays.stream(optionalTabType.get().tabButtons)
                        .filter(tabButton_ -> tabButton_.btnId == btnId)
                        .findFirst();

                if (tabButton.isPresent()) {
                    p.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, tabButton.get().interfaceId);
                    return true;
                }

                return true;
        }

        return false;
    }

    @RequiredArgsConstructor
    enum TabType {
        PLAYER_INFORMATION(new TabButton[] {
                new TabButton(-16107, QUEST_TAB_ID),
                new TabButton(-16106, WORLD_TAB_ID),
                new TabButton(-16105, KNOWLEDGE_BASE_ID)
        }),
        QUEST(new TabButton[] {
                        new TabButton(-16097, PLAYER_TAB_ID),
                        new TabButton(-16095, WORLD_TAB_ID),
                        new TabButton(-16094, KNOWLEDGE_BASE_ID)
        }),
        WORLD(new TabButton[] {
                        new TabButton(-16059, PLAYER_TAB_ID),
                        new TabButton(-16058, QUEST_TAB_ID),
                        new TabButton(-16056, KNOWLEDGE_BASE_ID)
        }),
        KNOWLEDGE_BASE(new TabButton[] {
                        new TabButton(-16048, PLAYER_TAB_ID),
                        new TabButton(-16047, QUEST_TAB_ID),
                        new TabButton(-16046, WORLD_TAB_ID)
        });

        private final TabButton[] tabButtons;
        public static final ImmutableList<TabType> TABS = ImmutableList.copyOf(values());
    }

    @RequiredArgsConstructor
    static class TabButton {
        private final int btnId;
        private final int interfaceId;
    }
}
