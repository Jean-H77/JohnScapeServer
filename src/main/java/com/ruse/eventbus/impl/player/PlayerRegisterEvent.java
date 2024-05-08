package com.ruse.eventbus.impl.player;

import com.ruse.eventbus.Event;

public record PlayerRegisterEvent(
        PlayerRegisterRequest playerRegisterRequest
) implements Event {
}
