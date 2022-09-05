package com.ruse.world.content.gambling.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.GameObject;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.movement.MovementQueue;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.Gambling;
import com.ruse.world.content.gambling.GamblingGame;

import java.util.ArrayList;
import java.util.List;

public class FlowerPoker extends GamblingGame {

    private static final int PLANT_DELAY = 5;

    private final List<GameObject> flowers = new ArrayList<>();

    private int plantCount;

    public FlowerPoker(Player[] gamblers) {
        super(gamblers);
    }

    @Override
    public void start() {
        gamblers[0].setPlayerLocked(true);
        gamblers[1].setPlayerLocked(true);

        TaskManager.submit(new Task(PLANT_DELAY, this, false) {
            @Override
            protected void execute() {
                if(plantCount==5 || !(gamblers[0] != null && gamblers[1] != null)) {
                    end();
                } else {
                    plantFlower(gamblers[0]);
                    plantFlower(gamblers[1]);
                    plantCount++;
                }
            }
        });
    }

    @Override
    public void end() {
        TaskManager.cancelTasks(this);
        flowers.forEach(CustomObjects::deleteGlobalObject);
        gamblers[0].setPlayerLocked(false);
        gamblers[1].setPlayerLocked(false);
        payout();
        System.out.println("Ending | c: " + plantCount);
    }

    @Override
    public Player determineWinner() {
        Player winner = null;



        return winner;
    }

    public void plantFlower(Player gambler) {
        Gambling.FlowersData rndFlower = Gambling.FlowersData.generate(); //get random flower

        GameObject flower = new GameObject(rndFlower.objectId, gambler.getPosition().copy());
        flowers.add(flower);

        gambler.performAnimation(new Animation(827));
        gambler.getPacketSender().sendMessage("You plant the seed..");
        CustomObjects.spawnGlobalObject(flower);
        gambler.setInteractingObject(flower);
        MovementQueue.stepAway(gambler);
        gambler.setPositionToFace(flower.getPosition());
    }
}
