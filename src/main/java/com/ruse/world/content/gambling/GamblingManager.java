package com.ruse.world.content.gambling;

import com.ruse.model.entity.character.player.Player;
import com.ruse.world.World;
import com.ruse.world.content.gambling.impl.FlowerPoker;

public class GamblingManager {

    private GamblingGame gamblingGame;

    public void start() {
        gamblingGame = new FlowerPoker(new Player[]{World.getPlayerByName("John"), World.getPlayerByName("John1")});
        gamblingGame.start();
    }
}
