package com.ruse.world.content.teleports;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TeleportType {
    AREAS(0),
    MINI_GAMES(1),
    RAIDS(2),
    MISC(3)
    ;

    private final int index;
}
