package com.ruse.world.content.skill;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Flag;
import com.ruse.model.GameMode;
import com.ruse.model.Graphic;
import com.ruse.model.Locations.Location;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.model.entity.character.player.Player;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.BonusManager;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;

/**
 * Represents a player's skills in the game, also manages
 * calculations such as combat level and total level.
 * 
 * @author relex lawl
 * @editor Gabbe
 */

public class SkillManager {

	/**
	 * The skillmanager's constructor
	 * @param player	The player's who skill set is being represented.
	 */
	public SkillManager(Player player) {
		this.player = player;
		newSkillManager();
	}

	/**
	 * Creates a new skillmanager for the player
	 * Sets current and max appropriate levels.
	 */
	public void newSkillManager() {
		this.skills = new Skills();
		for (int i = 0; i < MAX_SKILLS; i++) {
			skills.level[i] = skills.maxLevel[i] = 1;
			skills.experience[i] = 0;
		}
		skills.level[Skill.CONSTITUTION.ordinal()] = skills.maxLevel[Skill.CONSTITUTION.ordinal()] = 100;
		skills.experience[Skill.CONSTITUTION.ordinal()] = 1184;
		skills.level[Skill.PRAYER.ordinal()] = skills.maxLevel[Skill.PRAYER.ordinal()] = 10;
	}

	/**
	 * Roll for successful skilling actions
	 * @param skill
	 * 			the skill id
	 * @param levelRequired
	 * 			the level required
	 * @return
	 * 		if the success roll was true
	 */
	public boolean isSuccess(Skill skill, int levelRequired) {
		double level = getMaxLevel(skill);
		double req = levelRequired;
		double successChance = Math.ceil((level * 25 - req * 14) / req / 3 * 4);
		int roll = Misc.rand(99);
		return roll <= successChance;
	}

	/**
	 * Roll for successful skilling actions
	 * @param skill
	 * 			the skill id
	 * @param levelRequired
	 * 			the level required
	 * @param toolLevelRequired
	 * 			the tool level required
	 * @return
	 * 		if the success roll was true
	 */
	public boolean isSuccess(Skill skill, int levelRequired, int toolLevelRequired) {
		double level = getMaxLevel(skill) + (double) (toolLevelRequired / 3);
		if (level < levelRequired) {
			level = levelRequired;
		}
		double req = levelRequired;
		double successChance = Math.ceil((level * 25 - req * 14) / req / 3 * 4);
		int roll = Misc.rand(99);
		return roll <= successChance;
	}

	/**
	 * Adds experience to {@code skill} by the {@code experience} amount.
	 * @param skill			The skill to add experience to.
	 * @param experience	The amount of experience to add to the skill.
	 * @return				The Skills instance.
	 */
	public SkillManager addExperience(Skill skill, int experience) {
		if(player.experienceLocked())
			return this;

		if (this.skills.experience[skill.ordinal()] >= MAX_EXPERIENCE) {
			return this;
		}

		int startingLevel = isNewSkill(skill) ? (skills.maxLevel[skill.ordinal()]/10) : skills.maxLevel[skill.ordinal()];
		this.skills.experience[skill.ordinal()] = Math.min(this.skills.experience[skill.ordinal()] + experience, MAX_EXPERIENCE);
		int newLevel = getLevelForExperience(this.skills.experience[skill.ordinal()]);

		if (newLevel > startingLevel) {
			int level = newLevel - startingLevel;
			String skillName = Misc.formatText(skill.toString().toLowerCase());
			skills.maxLevel[skill.ordinal()] += isNewSkill(skill) ? level * 10 : level;

			setCurrentLevel(skill, getCurrentLevel(skill) + level * (isNewSkill(skill) ? 10 : 1));

			player.setDialogue(null);
			player.getPacketSender().sendString(4268, "Congratulations! You have achieved a " + skillName + " level!");
			player.getPacketSender().sendString(4269, "Well done. You are now level " + newLevel + ".");
			player.getPacketSender().sendString(358, "Click here to continue.");
			player.getPacketSender().sendChatboxInterface(skill.getChatboxInterface());
			player.performGraphic(new Graphic(312));
			player.getPacketSender().sendMessage("You've just advanced " + skillName + " level! You have reached level " + newLevel + ".");
			Sounds.sendSound(player, Sound.LEVELUP);
			if (skills.maxLevel[skill.ordinal()] == getMaxAchievingLevel(skill)) {
				player.getPacketSender().sendMessage("Well done! You've achieved the highest possible level in this skill!");
				World.sendMessage("<shad=15536940><img=10> "+player.getUsername()+" has just achieved the highest possible level in "+skillName+"!");
				if(maxed(player)) {
					World.sendMessage("<shad=15536940><img=10> "+player.getUsername()+" has just achieved the highest possible level in all skills!");
				}
				TaskManager.submit(new Task(2, player, true) {
					int localGFX = 1634;
					@Override
					public void execute() {
						player.performGraphic(new Graphic(localGFX));
						if (localGFX == 1637) {
							stop();
							return;
						}
						localGFX++;
						player.performGraphic(new Graphic(localGFX));
					}
				});
			} else {
				TaskManager.submit(new Task(2, player, false) {
					@Override
					public void execute() {
						player.performGraphic(new Graphic(199));
						stop();
					}
				});
			}
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		updateSkill(skill);
		this.totalGainedExp += experience;
		return this;
	}
	
	public boolean skillCape(Skill skill) {
		int c = skill.getSkillCapeId();
		int ct = skill.getSkillCapeTrimmedId();
		if(player.checkItem(Equipment.CAPE_SLOT, c) || player.checkItem(Equipment.CAPE_SLOT, ct) || 
				player.checkItem(Equipment.CAPE_SLOT, 14019) || player.checkItem(Equipment.CAPE_SLOT, 14022) || player.checkItem(Equipment.CAPE_SLOT, 20081) || (player.checkItem(Equipment.CAPE_SLOT, 22052) && player.getSkillManager().getMaxLevel(skill) >= 99) && player.getRights().isMember()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean maxed(Player p) {
		for(int i = 0; i < Skill.values().length; i++) {
			if(i == 21)
				continue;
			if(p.getSkillManager().getMaxLevel(i) < (i == 3 || i == 5 ? 990 : 99)) {
				return false;
			}
		}
		return true;
	}
	
	public SkillManager stopSkilling() {
		if(player.getCurrentTask() != null) {
			player.getCurrentTask().stop();
			player.setCurrentTask(null);
		}
		player.setResetPosition(null);
		player.setInputHandling(null);
		return this;
	}

	/**
	 * Updates the skill strings, for skill tab and orb updating.
	 * @param skill	The skill who's strings to update.
	 * @return		The Skills instance.
	 */
	public SkillManager updateSkill(Skill skill) {
		int maxLevel = getMaxLevel(skill), currentLevel = getCurrentLevel(skill);
		if (skill == Skill.PRAYER)
			player.getPacketSender().sendString(687, currentLevel + "/" + maxLevel);
		if (isNewSkill(skill)) {
			maxLevel = (maxLevel / 10);
			currentLevel = (currentLevel / 10);
		}
		player.getPacketSender().sendString(31200, Integer.toString(getTotalLevel()));
		player.getPacketSender().sendString(19000, "Combat level: " + getCombatLevel());
		player.getPacketSender().sendSkill(skill);
		return this;
	}

	public SkillManager resetSkill(Skill skill, boolean prestige) {
		if(player.getEquipment().getFreeSlots() != player.getEquipment().capacity()) {
			player.getPacketSender().sendMessage("Please unequip all your items first.");
			return this;
		}
		if(player.getLocation() == Location.WILDERNESS || player.getCombatBuilder().isBeingAttacked()) {
			player.getPacketSender().sendMessage("You cannot do this at the moment");
			return this;
		}
		if(prestige && player.getSkillManager().getMaxLevel(skill) < getMaxAchievingLevel(skill)) {
			player.getPacketSender().sendMessage("You must have reached the maximum level in a skill to prestige in it.");
			return this;
		}
		if(prestige) {
			int pts = getPrestigePoints(player, skill);
			player.getPointsHandler().setPrestigePoints(pts, true);
			player.getPacketSender().sendMessage("You've received "+pts+" Prestige points!");
		} else {
			player.getInventory().delete(13663, 1);
		}
		setCurrentLevel(skill, skill == Skill.PRAYER ? 10 : skill == Skill.CONSTITUTION ? 100 : 1).setMaxLevel(skill, skill == Skill.PRAYER ? 10 : skill == Skill.CONSTITUTION ? 100 : 1).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
		PrayerHandler.deactivateAll(player); 
		CurseHandler.deactivateAll(player); 
		BonusManager.update(player);
		WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
		WeaponAnimations.update(player);
		player.getPacketSender().sendMessage("You have reset your "+skill.getFormatName()+" level.");
		return this;
	}

	public static int getPrestigePoints(Player player, Skill skill) {
		float MAX_EXP = (float) MAX_EXPERIENCE;
		float experience = player.getSkillManager().getExperience(skill);			
		int basePoints = skill.getPrestigePoints();
		double bonusPointsModifier = player.getGameMode() == GameMode.IRONMAN ? 1.3 : player.getGameMode() == GameMode.ULTIMATE_IRONMAN ? 1.6 : 1;
		bonusPointsModifier += (experience/MAX_EXP) * 5;
		int totalPoints = (int) (basePoints * bonusPointsModifier);
		return totalPoints;
	}

	/**
	 * Gets the minimum experience in said level.
	 * @param level		The level to get minimum experience for.
	 * @return			The least amount of experience needed to achieve said level.
	 */
	public static int getExperienceForLevel(int level) {
		if(level <= 99) {
			return EXP_ARRAY[--level > 98 ? 98 : level];
		} else {
			int points = 0;
			int output = 0;
			for (int lvl = 1; lvl <= level; lvl++) {
				points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
				if (lvl >= level) {
					return output;
				}
				output = (int)Math.floor(points / 4);
			}
		}
		return 0;
	}

	/**
	 * Gets the level from said experience.
	 * @param experience	The experience to get level for.
	 * @return				The level you obtain when you have specified experience.
	 */
	public static int getLevelForExperience(int experience) {
		if(experience <= EXPERIENCE_FOR_99) {
			for(int j = 98; j >= 0; j--) {
				if(EXP_ARRAY[j] <= experience) {
					return j+1;
				}
			}
		} else {
			int points = 0, output = 0;
			for (int lvl = 1; lvl <= 99; lvl++) {
				points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
				output = (int) Math.floor(points / 4);
				if (output >= experience) {
					return lvl;
				}
			}
		}
		return 99;
	}

	/**
	 * Calculates the player's combat level.
	 * @return	The average of the player's combat skills.
	 */
	public int getCombatLevel() {
		final int attack = skills.maxLevel[Skill.ATTACK.ordinal()];
		final int defence = skills.maxLevel[Skill.DEFENCE.ordinal()];
		final int strength = skills.maxLevel[Skill.STRENGTH.ordinal()];
		final int hp = skills.maxLevel[Skill.CONSTITUTION.ordinal()] / 10;
		final int prayer = skills.maxLevel[Skill.PRAYER.ordinal()] / 10;
		final int ranged = skills.maxLevel[Skill.RANGED.ordinal()];
		final int magic = skills.maxLevel[Skill.MAGIC.ordinal()];
		final int summoning = skills.maxLevel[Skill.SUMMONING.ordinal()];
		int combatLevel = 3;
		combatLevel = (int) ((defence + hp + (double) (prayer / 2)) * 0.2535) + 1;
		final double melee = (attack + strength) * 0.325;
		final double ranger = Math.floor(ranged * 1.5) * 0.325;
		final double mage = Math.floor(magic * 1.5) * 0.325;
		if (melee >= ranger && melee >= mage) {
			combatLevel += melee;
		} else if (ranger >= melee && ranger >= mage) {
			combatLevel += ranger;
		} else if (mage >= melee && mage >= ranger) {
			combatLevel += mage;
		}
		if(player.getLocation() != Location.WILDERNESS) {
			combatLevel += summoning * 0.125;
		} else {
			if (combatLevel > 126) {
				return 126;
			}
		}
		if (combatLevel > 138) {
			return 138;
		} else if (combatLevel < 3) {
			return 3;
		}
		return combatLevel;
	}

	/**
	 * Gets the player's total level.
	 * @return	The value of every skill summed up.
	 */
	public int getTotalLevel() {
		int total = 0;
		for (Skill skill : Skill.values()) {
			/*
			 * If the skill is not equal to constitution or prayer, total can 
			 * be summed up with the maxLevel.
			 */
			if (!isNewSkill(skill)) {
				total += skills.maxLevel[skill.ordinal()];
				/*
				 * Other-wise add the maxLevel / 10, used for 'constitution' and prayer * 10.
				 */
			} else {
				total += skills.maxLevel[skill.ordinal()] / 10;
			}
		}
		return total;
	}

	/**
	 * Gets the player's total experience.
	 * @return	The experience value from the player's every skill summed up.
	 */
	public long getTotalExp() {
		long xp = 0;
		for (Skill skill : Skill.values())
			xp += player.getSkillManager().getExperience(skill);
		return xp;
	}

	/**
	 * Checks if the skill is a x10 skill.
	 * @param skill		The skill to check.
	 * @return			The skill is a x10 skill.
	 */
	public static boolean isNewSkill(Skill skill) {
		return skill == Skill.CONSTITUTION || skill == Skill.PRAYER;
	}

	/**
	 * Gets the max level for <code>skill</code>
	 * @param skill		The skill to get max level for.
	 * @return			The max level that can be achieved in said skill.
	 */
	public static int getMaxAchievingLevel(Skill skill) {
		int level = 99;
		if (isNewSkill(skill)) {
			level = 990;
		}
		/*if (skill == Skill.DUNGEONEERING) {
			level = 120;
		}*/
		return level;
	}

	/**
	 * Gets the current level for said skill.
	 * @param skill		The skill to get current/temporary level for.
	 * @return			The skill's level.
	 */
	public int getCurrentLevel(Skill skill) {
		return skills.level[skill.ordinal()];
	}

	/**
	 * Gets the max level for said skill.
	 * @param skill		The skill to get max level for.
	 * @return			The skill's maximum level.
	 */
	public int getMaxLevel(Skill skill) {
		return skills.maxLevel[skill.ordinal()];
	}

	/**
	 * Gets the max level for said skill.
	 * @param skill		The skill to get max level for.
	 * @return			The skill's maximum level.
	 */
	public int getMaxLevel(int skill) {
		return skills.maxLevel[skill];
	}

	/**
	 * Gets the experience for said skill.
	 * @param skill		The skill to get experience for.
	 * @return			The experience in said skill.
	 */
	public int getExperience(Skill skill) {
		return skills.experience[skill.ordinal()];
	}
	
	/**
	 * Sets the current level of said skill.
	 * @param skill		The skill to set current/temporary level for.
	 * @param level		The level to set the skill to.
	 * @param refresh	If <code>true</code>, the skill's strings will be updated.
	 * @return			The Skills instance.
	 */
	public SkillManager setCurrentLevel(Skill skill, int level, boolean refresh) {
		this.skills.level[skill.ordinal()] = level < 0 ? 0 : level;
		if (refresh)
			updateSkill(skill);
		return this;
	}

	/**
	 * Sets the maximum level of said skill.
	 * @param skill		The skill to set maximum level for.
	 * @param level		The level to set skill to.
	 * @param refresh	If <code>true</code>, the skill's strings will be updated.
	 * @return			The Skills instance.
	 */
	public SkillManager setMaxLevel(Skill skill, int level, boolean refresh) {
		skills.maxLevel[skill.ordinal()] = level;
		if (refresh)
			updateSkill(skill);
		return this;
	}

	/**
	 * Sets the experience of said skill.
	 * @param skill			The skill to set experience for.
	 * @param experience	The amount of experience to set said skill to.
	 * @param refresh		If <code>true</code>, the skill's strings will be updated.
	 * @return				The Skills instance.
	 */
	public SkillManager setExperience(Skill skill, int experience, boolean refresh) {
		this.skills.experience[skill.ordinal()] = experience < 0 ? 0 : experience;
		if (refresh)
			updateSkill(skill);
		return this;
	}

	/**
	 * Sets the current level of said skill.
	 * @param skill		The skill to set current/temporary level for.
	 * @param level		The level to set the skill to.
	 * @return			The Skills instance.
	 */
	public SkillManager setCurrentLevel(Skill skill, int level) {
		setCurrentLevel(skill, level, true);
		return this;
	}

	/**
	 * Sets the maximum level of said skill.
	 * @param skill		The skill to set maximum level for.
	 * @param level		The level to set skill to.
	 * @return			The Skills instance.
	 */
	public SkillManager setMaxLevel(Skill skill, int level) {
		setMaxLevel(skill, level, true);
		return this;
	}

	/**
	 * Sets the experience of said skill.
	 * @param skill			The skill to set experience for.
	 * @param experience	The amount of experience to set said skill to.
	 * @return				The Skills instance.
	 */
	public SkillManager setExperience(Skill skill, int experience) {
		setExperience(skill, experience, true);
		return this;
	}

	/**
	 * The player associated with this Skills instance.
	 */
	private Player player;
	private Skills skills;
	private long totalGainedExp;

	public class Skills {

		public Skills() {
			level = new int[MAX_SKILLS];
			maxLevel = new int[MAX_SKILLS];
			experience = new int[MAX_SKILLS];
		}

		private int[] level, maxLevel, experience;

	}

	public Skills getSkills() {
		return skills;
	}

	public void setSkills(Skills skills) {
		this.skills = skills;
	}

	public long getTotalGainedExp() {
		return totalGainedExp;
	}

	public void setTotalGainedExp(long totalGainedExp) {
		this.totalGainedExp = totalGainedExp;
	}

	/**
	 * The maximum amount of skills in the game.
	 */
	public static final int MAX_SKILLS = 25;

	/**
	 * The maximum amount of experience you can
	 * achieve in a skill.
	 */
	private static final int MAX_EXPERIENCE = 2000000000;

	private static final int EXPERIENCE_FOR_99 = 13034431;

	private static final int[] EXP_ARRAY = {
			0,83,174,276,388,512,650,801,969,1154,1358,1584,1833,2107,2411,2746,3115,3523,
			3973,4470,5018,5624,6291,7028,7842,8740,9730,10824,12031,13363,14833,16456,18247,
			20224,22406,24815,27473,30408,33648,37224,41171,45529,50339,55649,61512,67983,75127,
			83014,91721,101333,111945,123660,136594,150872,166636,184040,203254,224466,247886,
			273742,302288,333804,368599,407015,449428,496254,547953,605032,668051,737627,814445,
			899257,992895,1096278,1210421,1336443,1475581,1629200,1798808,1986068,2192818,2421087,
			2673114,2951373,3258594,3597792,3972294,4385776,4842295,5346332,5902831,6517253,7195629,
			7944614,8771558,9684577,10692629,11805606,13034431	
	};

}