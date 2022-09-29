package com.ruse.world.content.zombies;

import com.ruse.model.entity.character.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class ZombiesParty {
    private final List<Player> player = new ArrayList<>();
    private int wave;
    private long elapsedTime;
    private int totalKills;
}
