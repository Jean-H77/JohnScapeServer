package com.ruse.DiscordBot;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ruse.GameServer;
import lombok.extern.java.Log;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.Activity;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.util.logging.ExceptionLogger;

import com.ruse.GameSettings;
import com.ruse.world.World;
import com.ruse.model.entity.character.player.Player;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;


/**
 * @author Patrity || https://www.rune-server.ee/members/patrity/
 */
@Log
public class JavaCord {

    private static String token = "NjUxNDU5NzUwNzM5MDUwNTI4.XeaNaw.nksJJBCHAzi4P9UTqUEzGWzh--w";

    private static String serverName = "JohnScape";

    private static DiscordApi api = null;

    public static CompletableFuture<Void> init() {
        return new DiscordApiBuilder().setToken(token).login().thenAccept(api -> {
            JavaCord.api = api;
            System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
            //sendMessage("general", serverName+" is now online!");		//Disabled this for the time being as my team has gotten annoyed ;P >;P The server isn't on long periods at a time at this point
            api.addMessageCreateListener(event -> {

                /* I robbed this from the JavaCord repository. Wanted it for reference for future things (A.K.A my discord to server chat which is not being released with the rest of this.)
                 * Thanks guys!
                 */
                if (event.getMessageContent().equalsIgnoreCase("::userInfo")) {

                    MessageAuthor author = event.getMessage().getAuthor();

                    EmbedBuilder embed = new EmbedBuilder()

                            .setTitle("User Info")

                            .addField("Display Name", author.getDisplayName(), true)

                            .addField("Name + Discriminator", author.getDiscriminatedName(), true)

                            .addField("User Id", author.getIdAsString(), true)

                            .setAuthor(author);

                    // Keep in mind that a message author can either be a webhook or a normal user

                    author.asUser().ifPresent(user -> {

                        embed.addField("Online Status", user.getStatus().getStatusString(), true);

                        embed.addField("Connected Clients", user.getCurrentClients().toString());

                        // The User#getActivity() method returns an Optional

                        embed.addField("Activity", user.getActivity().map(Activity::getName).orElse("none"), true);

                    });

                    // Keep in mind that messages can also be sent as private messages

                    event.getMessage().getServer()

                            .ifPresent(server -> embed.addField("Server Admin", author.isServerAdmin() ? "yes" : "no", true));

                    // Send the embed. It logs every exception, besides missing permissions (you are not allowed to send message in the channel)

                    event.getChannel().sendMessage(embed);



                }

                if (event.getMessageContent().equalsIgnoreCase("::players")) {
                    int online = (int)(World.getPlayers().size());
                    event.getChannel().sendMessage("Players currently online: "+online);
                }

                if (event.getMessageContent().equalsIgnoreCase("::online")) {
                    event.getChannel().sendMessage(":tada: "+serverName+" is Online! :tada:");
                }
                if (event.getMessageContent().equalsIgnoreCase("::vote")) {
                    event.getChannel().sendMessage(":bell: Voting Link here :bell:");
                }
                if (event.getMessageContent().equalsIgnoreCase("::store")) {
                    event.getChannel().sendMessage(":moneybag: store Link here :moneybag:");
                }
                if (event.getMessageContent().equalsIgnoreCase("::donate")) {
                    event.getChannel().sendMessage(":moneybag: store Link here :moneybag:");
                }


                if (event.getMessageContent().startsWith("::movehome")) {
                    if (event.getMessageAuthor().isServerAdmin()) {
                        System.out.println("perms");
                        String target = event.getMessageContent().replace("::movehome ", "");
                        Player playerToMove = World.getPlayerByName(target);
                        if (playerToMove != null) {
                            playerToMove.moveTo(GameSettings.DEFAULT_POSITION.copy());
                            playerToMove.getPacketSender().sendMessage("You have been teleported home by " + event.getMessageAuthor().getDisplayName() + ".");
                            event.getChannel().sendMessage("Sucessfully moved "+playerToMove.getUsername()+" to home.");

                        } else {
                            event.getChannel().sendMessage("Player is not online!");
                        }
                    } else {
                        event.getChannel().sendMessage("You do not have permission to preform this command");
                    }
                }

                if (event.getMessageContent().startsWith("::kick")) {
                    if (event.getMessageAuthor().isServerAdmin()) {
                        String target = event.getMessageContent().replace("::kick ", "");
                        Player playerToKick = World.getPlayerByName(target);
                        if (playerToKick != null) {
                            if (playerToKick.getDueling().duelingStatus >= 5) {
                                event.getChannel().sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
                                return;
                            }
                            World.getPlayers().remove(playerToKick);
                            event.getChannel().sendMessage("Successfully kicked: " + playerToKick.getUsername() + ".");
                            World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> An Admin on Discord has just kicked " + playerToKick.getUsername() + ".");

                        } else {
                            event.getChannel().sendMessage(playerToKick.getUsername() +" is not online!");
                        }
                    } else {
                        event.getChannel().sendMessage("You do not have permission to preform this command");
                    }
                }


            });
        })
                // Log any exceptions that happened
                .exceptionally(ExceptionLogger.get());
    }


    public static void sendMessage(String channel, String msg) {
        try {
            if (GameServer.getConfiguration().isDiscordBotEnabled()) {
                new MessageBuilder()
                        .append(msg)
                        .send((TextChannel) api.getTextChannelsByNameIgnoreCase(channel).toArray()[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void sendEmbed(String ch, EmbedBuilder embed) {
        try {
            if (GameServer.getConfiguration().isDiscordBotEnabled()) {
                Collection<TextChannel> channels = api.getTextChannelsByName(ch);
                if (channels == null || channels.isEmpty()) {
                    //log.info(String.format("No channel %s", ch));
                } else {
                    Channel channel = (TextChannel) channels.toArray()[0];
                   // channel.asServerTextChannel().ifPresentOrElse(c -> c.sendMessage(embed));
                    /*log.info(String.format("No channel %s", ch))*///);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testEmbed() {
        if (GameServer.getConfiguration().isDiscordBotEnabled()) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Some Title")
                    .setDescription("Description goes Here")
                    .setAuthor("Your Bot", "URL_HERE", "Bot's avatar url")
                    .addField("Content Title", "Some Content")
                    .setColor(Color.GREEN)
                    .setImage("Bottom Image URL")
                    .setThumbnail("Top-Right Image URL");

            String ch = null;
            sendEmbed(ch, embed);
        }
    }


}