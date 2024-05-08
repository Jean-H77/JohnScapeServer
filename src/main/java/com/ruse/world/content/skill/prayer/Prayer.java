package com.ruse.world.content.skill.prayer;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Item;
import com.ruse.model.Skill;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.model.entity.character.player.Player;

/**
 * The prayer skill is based upon burying the corpses of enemies. Obtaining a higher level means
 * more prayer abilities being unlocked, which help out in combat.
 * 
 * @author Gabriel Hannason
 */

public class Prayer {

	private static final Animation ANIMATION = new Animation(827);

	public static boolean isBone(int bone) {
		return BoneType.forId(bone) != null;
	}
	
	public static void buryBone(final Player player, final int itemId) {
		if(!player.getClickDelay().elapsed(2000))
			return;
		final BoneType currentBone = BoneType.forId(itemId);
		if(currentBone == null)
			return;
		player.getSkillManager().stopSkilling();
		player.getPacketSender().sendInterfaceRemoval();
		player.performAnimation(ANIMATION);
		player.getPacketSender().sendMessage("You dig a hole in the ground..");
		final Item bone = new Item(itemId);
		player.getInventory().delete(bone);
		TaskManager.submit(new Task(2, player, false) {
			@Override
			public void execute() {
				player.getPacketSender().sendMessage("..and bury the "+bone.getDefinition().getName()+".");
				player.getSkillManager().addExperience(Skill.PRAYER, currentBone.getBuryingXP());
				Sounds.sendSound(player, Sound.BURY_BONE);
				stop();				
			}
		});
		player.getClickDelay().reset();
	}
}
