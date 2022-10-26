package com.ruse.world.content.teleports;

import lombok.Getter;
import lombok.RequiredArgsConstructor;



public enum TeleportType {
    AREAS(0),
    MINI_GAMES(1)
    ;

    private final int index;

    private TeleportType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
