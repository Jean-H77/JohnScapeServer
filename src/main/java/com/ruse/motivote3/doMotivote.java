package com.ruse.motivote3;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.motivoters.motivote.service.MotivoteRS;
import com.ruse.DiscordBot.JavaCord;
import com.ruse.GameSettings;
import com.ruse.model.Item;
import com.ruse.world.World;
import com.ruse.model.entity.character.player.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;

//import static com.ruse.world.content.Achievements.AchievementData.VOTE_100_TIMES;


/**
 * 
 * @author Crimson
 *
 */

public class doMotivote implements Runnable{
	
	private final static MotivoteRS motivote = new MotivoteRS("JohnScape", "005be5117b6d0005ed7e88e09deaa630"); //enter your motivote details here
	
	private static int voteCount = 0;
	private static final ExecutorService SERVICE = Executors.newCachedThreadPool();
	
	  public static void main(Player player, String auth){
		SERVICE.execute(() -> {
	    	try {
				boolean success = motivote.redeemVote(auth);
				Item item = new Item(19670, 1);
				if (success) {
					player.getInventory().add(item, true);
					player.getPacketSender().sendMessage("Auth redeemed, thanks for voting!");
					player.getLastVoteClaim().reset();
					//Achievements.doProgress(player, VOTE_100_TIMES, 1);
					voteCount ++;
					if (voteCount >= GameSettings.Vote_Announcer) {
						World.sendMessage("<img=10><shad=0><col=bb43df> 10 more players have just voted! Use ::vote for rewards! Thanks, <col="+player.getYellHex()+">"+player.getUsername()+"<col=bb43df>!");
						JavaCord.sendMessage("ingame-announcements","10 more players have just voted! Use ::vote for rewards! Thank you for your support!");
						JavaCord.sendEmbed("ingame-announcements", new EmbedBuilder().setTitle("Votes! Votes! Votes!") .setDescription("Another 10 votes have just been claimed! Thank you for your support! Do ::vote to open voting page")
																		.setColor(Color.CYAN).setTimestampToNow()
																		.setThumbnail("http://www.slate.com/content/dam/slate/articles/news_and_politics/slate_fare/2016/11/161104_SF_voting-slate.jpg.CROP.promo-xlarge2.jpg").setFooter("Powered by JavaCord"));
						voteCount = 0;
					} else {
						player.getPacketSender().sendMessage("<img=10><shad=0><col=bb43df>Thank you for voting and supporting JohnScape!");
					}
				} else {
						player.getPacketSender().sendMessage("Invalid voting auth supplied, please try again.");
						player.getLastVoteClaim().reset();
				}
	    	} catch (Exception ex) {
					ex.printStackTrace();
				}
		}); 
	  }
		  /*
	      Thread one = new Thread() {
	    	    public void run() {
	    	    	try {
	    				boolean success = motivote.redeemVote(auth);
	    				Item item = new Item(19670, 1);
	    				if (success) {
	    					player.getInventory().add(item, true);
	    					player.getPacketSender().sendMessage("Auth redeemed, thanks for voting!");
	    					player.getLastVoteClaim().reset();
	    					Achievements.doProgress(player, VOTE_100_TIMES, 1);
	    					voteCount ++;
	    					if (voteCount >= GameSettings.Vote_Announcer) {
	    						World.sendMessage("<img=10><shad=0><col=bb43df> 10 more players have just voted! Use ::vote for rewards! Thanks, <col="+player.getYellHex()+">"+player.getUsername()+"<col=bb43df>!");
	    						voteCount = 0;
	    					} else {
	    						player.getPacketSender().sendMessage("<img=10><shad=0><col=bb43df>Thank you for voting and supporting JohnScape!");
	    					}
	    				}
	    				else {
	    						player.getPacketSender().sendMessage("Invalid voting auth supplied, please try again.");
	    						player.getLastVoteClaim().reset();
	    				}
	    	    	} catch (Exception ex) {
	    					ex.printStackTrace();
	    				}
	    	    	} 
	    	};
	    	one.start();
	  }*/

	@Override
	public void run() {
		System.out.println("Thread should start");
	}

}